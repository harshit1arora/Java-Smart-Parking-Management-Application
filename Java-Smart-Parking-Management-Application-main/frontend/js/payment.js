const API = 'http://localhost:8080/api';
let activeBooking  = null;
let selectedMethod = 'CARD';

async function apiFetch(url, options = {}) {
  const user = JSON.parse(sessionStorage.getItem('user') || 'null');
  const headers = { ...options.headers };
  if (user && user.token) {
    headers['Authorization'] = `Bearer ${user.token}`;
  }
  return fetch(url, { ...options, headers });
}

/* ---- Init ---- */
window.addEventListener('DOMContentLoaded', () => {
  const user = JSON.parse(sessionStorage.getItem('user') || 'null');
  if (!user) { window.location.href = 'index.html'; return; }
  
  const av = document.getElementById('userAvatar');
  if (av) av.textContent = user.name.charAt(0).toUpperCase();

  activeBooking = JSON.parse(sessionStorage.getItem('activeBooking') || 'null');
  if (!activeBooking) {
      alert('No active booking found. Redirecting to booking page...');
      window.location.href = 'booking.html';
      return;
  }
  fillSummary();
});

function fillSummary() {
  document.getElementById('summaryBookingId').textContent = '#' + activeBooking.booking_id;
  document.getElementById('summarySlot').textContent      = activeBooking.slot_number || 'A-01';
  document.getElementById('summaryDuration').textContent  = (activeBooking.duration_minutes || 60) + ' min';
  document.getElementById('summaryTotal').textContent     = '₹' + (activeBooking.estimated_amount || 20);
}

/* ---- Initiate Payment ---- */
window.initiatePayment = async function() {
    const btn = document.getElementById('payBtn');
    const user = JSON.parse(sessionStorage.getItem('user') || 'null');
    
    if (!activeBooking || !user) {
        showToast('Session error. Please try again.', 'error');
        return;
    }

    btn.classList.add('loading');
    btn.disabled = true;

    try {
        console.log('[Payment] Initiating order for booking:', activeBooking.booking_id);
        
        const response = await apiFetch(`${API}/payment/initiate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                booking_id: activeBooking.booking_id,
                amount: activeBooking.estimated_amount || 20,
                method: 'Razorpay'
            })
        });

        if (response.status === 401) {
            showToast('Session expired. Please login again.', 'error');
            setTimeout(() => window.location.href = 'index.html', 2000);
            return;
        }

        const data = await response.json();
        console.log('[Payment] Backend response:', data);

        if (!data.success) {
            throw new Error(data.message || 'Server failed to create order.');
        }

        const rzpData = data.data;
        const options = {
            "key": rzpData.key,
            "amount": rzpData.amount,
            "currency": rzpData.currency,
            "name": "ParkEngineer",
            "description": "Parking Slot Booking",
            "order_id": rzpData.order_id,
            "handler": async function (rzpResponse) {
                console.log('[Payment] Razorpay success:', rzpResponse);
                await verifyPayment(rzpResponse, rzpData.order_id);
            },
            "prefill": {
                "name": user.name,
                "email": user.email,
                "contact": user.phone || ""
            },
            "theme": { "color": "#2563eb" },
            "modal": {
                "ondismiss": function() {
                    btn.classList.remove('loading');
                    btn.disabled = false;
                }
            }
        };

        const rzp = new window.Razorpay(options);
        rzp.on('payment.failed', function (err) {
            console.error('[Payment] Razorpay failure:', err.error);
            showToast('Payment failed: ' + err.error.description, 'error');
            btn.classList.remove('loading');
            btn.disabled = false;
        });
        rzp.open();

    } catch (err) {
        console.error('[Payment] Initiation error:', err);
        showToast('Error: ' + err.message, 'error');
        btn.classList.remove('loading');
        btn.disabled = false;
    }
};

async function verifyPayment(rzpResponse, orderId) {
    const btn = document.getElementById('payBtn');
    try {
        showToast('Verifying payment...', 'info');
        
        const response = await apiFetch(`${API}/payment/verify`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                razorpay_order_id: rzpResponse.razorpay_order_id,
                razorpay_payment_id: rzpResponse.razorpay_payment_id,
                razorpay_signature: rzpResponse.razorpay_signature
            })
        });

        const data = await response.json();
        console.log('[Payment] Verification response:', data);

        if (data.success) {
            saveSession(data.data, rzpResponse.razorpay_payment_id);
            showSuccessScreen(rzpResponse.razorpay_payment_id);
        } else {
            showToast(data.message || 'Verification failed.', 'error');
            btn.classList.remove('loading');
            btn.disabled = false;
        }
    } catch (err) {
        console.error('[Payment] Verification error:', err);
        showToast('Verification error. Please contact support.', 'error');
        btn.classList.remove('loading');
        btn.disabled = false;
    }
}

function saveSession(paymentData, txnId) {
  const session = {
    booking_id:      activeBooking.booking_id,
    slot_number:     activeBooking.slot_number || 'A-01',
    duration_minutes: activeBooking.duration_minutes || 60,
    amount:          activeBooking.estimated_amount || 20,
    method:          'Razorpay',
    transaction_id:  txnId,
    start_time:      new Date().toISOString()
  };
  sessionStorage.setItem('activeSession', JSON.stringify(session));
}

function showSuccessScreen(txnId) {
  document.getElementById('bookingSummary').style.display = 'none';
  document.getElementById('paymentCard').style.display    = 'none';
  document.querySelector('.page-header').style.display    = 'none';
  document.getElementById('successScreen').classList.add('visible');

  document.getElementById('confirmBookingId').textContent = 'BKG-' + activeBooking.booking_id;
  document.getElementById('confirmTxnId').textContent     = 'TXN: ' + txnId;
  document.getElementById('confirmSlot').textContent      = activeBooking.slot_number || 'A-01';
  document.getElementById('confirmAmount').textContent    = '₹' + (activeBooking.estimated_amount || 20);
  document.getElementById('confirmMethod').textContent    = 'Razorpay';

  showToast('Payment successful! 🎉', 'success');
}

window.goToTimer = function() {
  window.location.href = 'timer.html';
};

function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  if (!container) return;
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span>${type==='success'?'✅':type==='error'?'❌':type==='warning'?'⚠️':'ℹ️'}</span><span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 4500);
}
