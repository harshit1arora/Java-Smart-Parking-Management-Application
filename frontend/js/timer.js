const API = 'http://localhost:8080/api';
let activeSession  = null;
let timerInterval  = null;
let costInterval   = null;
let elapsedSeconds = 0;
let totalSeconds   = 0;
const RATE_PER_30  = 10;  // ₹10 per 30 min

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

  // Load session — from payment page or demo
  activeSession = JSON.parse(sessionStorage.getItem('activeSession') || 'null');
  if (!activeSession) {
    // Demo session for direct access
    activeSession = {
      booking_id:      101,
      slot_number:     'A-04',
      duration_minutes: 60,
      amount:          20,
      method:          'UPI',
      transaction_id:  'TXN_DEMO123456',
      start_time:      new Date().toISOString()
    };
    sessionStorage.setItem('activeSession', JSON.stringify(activeSession));
  }

  initTimer();
  fillSessionInfo();
  startTimers();
});

/* ---- Fill Session Info ---- */
function fillSessionInfo() {
  const s = activeSession;
  const startTime = new Date(s.start_time);

  document.getElementById('heroSlotNum').textContent    = s.slot_number;
  document.getElementById('infoBookingId').textContent  = '#' + s.booking_id;
  document.getElementById('infoSlot').textContent       = s.slot_number;
  document.getElementById('infoStart').textContent      = formatTime(startTime);
  document.getElementById('infoDuration').textContent   = s.duration_minutes + ' min';
  document.getElementById('infoAmount').textContent     = '₹' + s.amount;
  document.getElementById('infoMethod').textContent     = s.method;
  document.getElementById('startTimeDisplay').textContent = 'Started at ' + formatTime(startTime);
}

/* ---- Init Timer State ---- */
function initTimer() {
  const startTime = new Date(activeSession.start_time);
  totalSeconds    = activeSession.duration_minutes * 60;
  elapsedSeconds  = Math.floor((Date.now() - startTime.getTime()) / 1000);
  if (elapsedSeconds < 0) elapsedSeconds = 0;
}

/* ---- Start All Timers ---- */
function startTimers() {
  // Tick every second
  timerInterval = setInterval(() => {
    elapsedSeconds++;
    updateCountdown();
    updateRing();
    updateElapsedDisplay();
  }, 1000);

  // Update cost every minute (and immediately)
  updateCost();
  costInterval = setInterval(updateCost, 60000);

  // Initial render
  updateCountdown();
  updateRing();
  updateElapsedDisplay();
}

/* ---- Countdown ---- */
function updateCountdown() {
  const remaining = totalSeconds - elapsedSeconds;
  const display   = document.getElementById('countdownDisplay');

  if (remaining > 0) {
    display.textContent = formatDuration(remaining);
    display.classList.remove('warning');
    // Red warning in last 10 min
    if (remaining < 600) display.classList.add('warning');
  } else {
    // Overtime
    const overtime = Math.abs(remaining);
    display.textContent = '+' + formatDuration(overtime);
    display.classList.add('warning');
    document.getElementById('overtimeWarning').classList.add('visible');
  }
}

/* ---- SVG Progress Ring ---- */
function updateRing() {
  const pct    = Math.min(elapsedSeconds / totalSeconds, 1);
  const circumference = 2 * Math.PI * 70; // r=70
  const offset = circumference * (1 - pct);
  document.getElementById('ringFill').style.strokeDashoffset = offset;
  document.getElementById('ringFill').setAttribute('stroke-dasharray', circumference);
}

/* ---- Elapsed Display ---- */
function updateElapsedDisplay() {
  const mins = Math.floor(elapsedSeconds / 60);
  const hrs  = Math.floor(mins / 60);
  const el   = document.getElementById('ringElapsed');
  el.textContent = hrs > 0 ? `${hrs}h${mins%60}m` : `${mins}m`;
}

/* ---- Running Cost (updates every minute) ---- */
function updateCost() {
  const elapsedMin = Math.floor(elapsedSeconds / 60);
  const blocks     = Math.ceil(Math.max(elapsedMin, 1) / 30);
  const cost       = blocks * RATE_PER_30;

  document.getElementById('runningCost').textContent = '₹' + cost;
  document.getElementById('cbBlocks').textContent    = blocks;
  document.getElementById('cbDuration').textContent  = elapsedMin + 'm';
}

/* ---- Release Slot ---- */
function openReleaseConfirm() {
  const elapsedMin = Math.floor(elapsedSeconds / 60);
  const blocks     = Math.ceil(Math.max(elapsedMin, 1) / 30);
  const est        = blocks * RATE_PER_30;
  document.getElementById('releaseEstimate').textContent = '₹' + est;
  document.getElementById('releaseModal').classList.add('open');
}

function closeReleaseModal() {
  document.getElementById('releaseModal').classList.remove('open');
}

async function releaseSlot() {
  const btn = document.getElementById('confirmReleaseBtn');
  btn.classList.add('loading');

  try {
    const res  = await apiFetch(`${API}/releaseSlot`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ booking_id: activeSession.booking_id })
    });
    const data = await res.json();
    if (data.success) {
      stopTimers();
      closeReleaseModal();
      showFinalBill(data.data.final_amount, elapsedSeconds);
    } else {
      showToast(data.message || 'Release failed', 'error');
    }
  } catch (e) {
    // Demo offline
    const elapsedMin = Math.floor(elapsedSeconds / 60);
    const blocks     = Math.ceil(Math.max(elapsedMin, 1) / 30);
    const amount     = blocks * RATE_PER_30;
    stopTimers();
    closeReleaseModal();
    showFinalBill(amount, elapsedSeconds);
  } finally {
    btn.classList.remove('loading');
  }
}

/* ---- Show Final Bill ---- */
function showFinalBill(amount, elapsed) {
  // Clear active session
  sessionStorage.removeItem('activeSession');

  // Hide active view
  document.getElementById('activeSessionView').style.display = 'none';
  document.getElementById('finalBillView').classList.add('visible');

  const elapsedMin = Math.floor(elapsed / 60);
  document.getElementById('finalAmount').textContent    = '₹' + amount;
  document.getElementById('billBookingId').textContent  = '#' + activeSession.booking_id;
  document.getElementById('billSlot').textContent       = activeSession.slot_number;
  document.getElementById('billDuration').textContent   = elapsedMin + ' min';
  document.getElementById('billTotal').textContent      = '₹' + amount;

  showToast('Slot released! Final charge: ₹' + amount, 'success');
}

/* ---- Stop Timers ---- */
function stopTimers() {
  clearInterval(timerInterval);
  clearInterval(costInterval);
}

/* ---- Helpers ---- */
function formatDuration(seconds) {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  return h > 0
    ? `${h}:${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
    : `${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`;
}

function formatTime(date) {
  return date.toLocaleTimeString('en-IN', { hour:'2-digit', minute:'2-digit', hour12:true });
}

/* ---- Toast ---- */
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span>${type==='success'?'✅':type==='error'?'❌':type==='warning'?'⚠️':'ℹ️'}</span><span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 4500);
}
