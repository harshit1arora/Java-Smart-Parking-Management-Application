const API = 'http://localhost:8080/api';
let selectedSlot     = null;
let selectedDuration = 60;
let currentAreaId    = null;
let currentUser      = null;

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
  currentUser  = JSON.parse(sessionStorage.getItem('user') || 'null');
  if (!currentUser) { window.location.href = 'index.html'; return; }

  const av = document.getElementById('userAvatar');
  if (av) av.textContent = currentUser.name.charAt(0).toUpperCase();

  currentAreaId = parseInt(sessionStorage.getItem('selectedAreaId') || '1');
  showEmergencyCard();
  loadAreaInfo();
  loadSlots();
});

/* ---- Load Area Info ---- */
async function loadAreaInfo() {
  try {
    const res  = await apiFetch(`${API}/areas?q=`);
    const data = await res.json();
    if (data.success && data.data) {
      const area = data.data.find(a => a.areaId === currentAreaId) || data.data[0];
      fillAreaInfo(area);
    }
  } catch (e) {
    const demos = getDemoAreas();
    fillAreaInfo(demos.find(a => a.areaId === currentAreaId) || demos[0]);
  }
}

function fillAreaInfo(area) {
  if (!area) return;
  document.getElementById('areaName').textContent     = area.name;
  document.getElementById('areaLocation').textContent = '📍 ' + area.location;
}

/* ---- Load Slots ---- */
async function loadSlots() {
  try {
    const res  = await apiFetch(`${API}/slots?area_id=${currentAreaId}`);
    const data = await res.json();
    if (data.success) {
      renderSlots(data.data);
      updateCounts(data.data);
    } else {
      renderSlots(getDemoSlots(currentAreaId));
    }
  } catch (e) {
    console.warn('[Booking] Backend offline — demo slots');
    renderSlots(getDemoSlots(currentAreaId));
  }
}

function updateCounts(slots) {
  const free  = slots.filter(s => s.status === 'free').length;
  const occ   = slots.filter(s => s.status === 'occupied').length;
  document.getElementById('freeCount').textContent     = free;
  document.getElementById('occupiedCount').textContent = occ;
}

/* ---- Render Slot Grid ---- */
function renderSlots(slots) {
  const grid = document.getElementById('slotGrid');
  grid.innerHTML = slots.map(slot => {
    const icon = slot.slotType === 'emergency' ? '🚨' :
                 slot.status === 'free'     ? '🟢' :
                 slot.status === 'occupied' ? '🔴' : '🟡';
    const extra = slot.slotType === 'emergency' ? ' emergency' : '';
    const classes = `slot-cell ${slot.status}${extra}`;
    return `
      <div class="${classes}"
           id="slot-${slot.slotId}"
           onclick="selectSlot(${slot.slotId}, '${slot.slotNumber}', '${slot.status}', '${slot.slotType}')"
           title="${slot.slotNumber} — ${slot.status}">
        <span>${icon}</span>
        ${slot.slotNumber}
      </div>`;
  }).join('');
}

/* ---- Select Slot ---- */
function selectSlot(slotId, slotNumber, status, slotType) {
  if (status !== 'free') {
    showToast(`Slot ${slotNumber} is ${status}`, 'warning');
    return;
  }

  // Deselect previous
  document.querySelectorAll('.slot-cell.selected').forEach(el => el.classList.remove('selected'));

  // Select new
  document.getElementById('slot-' + slotId).classList.add('selected');
  selectedSlot = { slotId, slotNumber, slotType };

  // Show selected indicator
  document.getElementById('selectedSlotNum').textContent = slotNumber;
  document.getElementById('selectedSlotType').textContent = slotType === 'emergency' ? '🚨 Emergency' : '🚗 Regular';
  document.getElementById('selectedSlotDisplay').classList.add('visible');

  showToast(`Slot ${slotNumber} selected!`, 'success');
}

/* ---- Open Booking Modal ---- */
function openBookingModal() {
  if (!selectedSlot) { showToast('Please select a slot first', 'warning'); return; }
  document.getElementById('modalSlotNum').textContent = selectedSlot.slotNumber;
  updateCostPreview();
  document.getElementById('bookingModal').classList.add('open');
}

function closeModal() {
  document.getElementById('bookingModal').classList.remove('open');
}

/* ---- Duration selection ---- */
function selectDuration(mins) {
  selectedDuration = mins;
  document.querySelectorAll('.duration-btn').forEach(b => b.classList.remove('active'));
  document.getElementById('dur-' + mins).classList.add('active');
  updateCostPreview();
}

function updateCostPreview() {
  const blocks = Math.ceil(selectedDuration / 30);
  const cost   = blocks * 10;
  document.getElementById('estimatedCost').textContent = '₹' + cost;
  document.getElementById('modalDuration').textContent = selectedDuration + ' min';
}

/* ---- Confirm Booking ---- */
async function confirmBooking() {
  if (!selectedSlot || !currentUser) { showToast('Please select a slot', 'warning'); return; }
  const btn = document.getElementById('confirmBookingBtn');
  btn.classList.add('loading');

  try {
    const res  = await apiFetch(`${API}/bookSlot`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        user_id: currentUser.user_id || 1,
        slot_id: selectedSlot.slotId,
        duration_minutes: selectedDuration
      })
    });
    const data = await res.json();
    if (data.success) {
      sessionStorage.setItem('activeBooking', JSON.stringify(data.data));
      closeModal();
      showToast('Slot reserved! Proceed to payment.', 'success');
      setTimeout(() => window.location.href = 'payment.html', 1500);
    } else {
      showToast(data.message || 'Booking failed', 'error');
    }
  } catch (e) {
    // Demo offline booking
    const mockBooking = {
      booking_id: Math.floor(Math.random() * 1000) + 100,
      slot_id: selectedSlot.slotId,
      slot_number: selectedSlot.slotNumber,
      duration_minutes: selectedDuration,
      estimated_amount: Math.ceil(selectedDuration / 30) * 10,
      status: 'reserved'
    };
    sessionStorage.setItem('activeBooking', JSON.stringify(mockBooking));
    // Mark slot visually
    const slotEl = document.getElementById('slot-' + selectedSlot.slotId);
    if(slotEl) { slotEl.className = 'slot-cell reserved'; slotEl.innerHTML = '<span>🟡</span>' + selectedSlot.slotNumber; }
    closeModal();
    showToast('Slot reserved! (Demo mode)', 'success');
    setTimeout(() => window.location.href = 'payment.html', 1500);
  } finally {
    btn.classList.remove('loading');
  }
}

/* ---- Emergency Slot ---- */
function showEmergencyCard() {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}');
  if (user.role === 'emergency') {
    document.getElementById('emergencyCard').style.display = 'block';
  }
}

async function requestEmergencySlot() {
  const vehicleId = document.getElementById('vehicleId').value.trim();
  if (!vehicleId) { showToast('Enter vehicle ID', 'warning'); return; }

  try {
    const res  = await apiFetch(`${API}/emergency/requestSlot`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        user_id: currentUser.user_id,
        vehicle_id: vehicleId,
        area_id: currentAreaId
      })
    });
    const data = await res.json();
    if (data.success) {
      showToast(`Emergency slot ${data.data.slotNumber} allocated!`, 'success');
      loadSlots();
    } else {
      showToast(data.message, 'error');
    }
  } catch (e) {
    showToast('Emergency slot AE-01 allocated (Demo mode)', 'success');
  }
}

/* ---- Demo Data ---- */
function getDemoSlots(areaId) {
  const prefix = areaId === 1 ? 'A' : areaId === 2 ? 'B' : 'C';
  const statuses = ['occupied','occupied','reserved','free','free','occupied','free','occupied','free','occupied',
                    'reserved','free','occupied','free','free','occupied','free','free'];
  return [
    ...statuses.map((st,i) => ({ slotId: (areaId-1)*20+i+1, slotNumber:`${prefix}-${String(i+1).padStart(2,'0')}`, slotType:'regular', status:st })),
    { slotId:(areaId-1)*20+19, slotNumber:`${prefix}E-01`, slotType:'emergency', status:'free' },
    { slotId:(areaId-1)*20+20, slotNumber:`${prefix}E-02`, slotType:'emergency', status:'free' }
  ];
}

function getDemoAreas() {
  return [
    { areaId:1, name:'Central Mall Parking',  location:'MG Road, Bangalore',     totalSlots:20, freeSlots:7 },
    { areaId:2, name:'Tech Park Parking',     location:'Whitefield, Bangalore',  totalSlots:20, freeSlots:16 },
    { areaId:3, name:'Airport Parking Zone',  location:'Devanahalli, Bangalore', totalSlots:20, freeSlots:14 },
  ];
}

/* ---- Toast ---- */
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span>${type==='success'?'✅':type==='error'?'❌':type==='warning'?'⚠️':'ℹ️'}</span><span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 4000);
}
