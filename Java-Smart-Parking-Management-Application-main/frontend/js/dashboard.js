const API = 'http://localhost:8080/api';
let allAreas  = [];

async function apiFetch(url, options = {}) {
  const user = JSON.parse(sessionStorage.getItem('user') || 'null');
  const headers = { ...options.headers };
  if (user && user.token) {
    headers['Authorization'] = `Bearer ${user.token}`;
  }
  return fetch(url, { ...options, headers });
}

let activeFilter = 'all';
let userLat = null, userLng = null;

/* ---- On Load ---- */
window.addEventListener('DOMContentLoaded', () => {
  initUser();
  loadAreas();
});

function initUser() {
  const user = JSON.parse(sessionStorage.getItem('user') || 'null');
  if (!user) { window.location.href = 'index.html'; return; }
  const av = document.getElementById('userAvatar');
  if (av) av.textContent = user.name.charAt(0).toUpperCase();
}

/* ---- Load All Areas ---- */
async function loadAreas() {
  try {
    const res  = await apiFetch(`${API}/areas`);
    const data = await res.json();
    if (data.success) {
      allAreas = data.data;
    } else {
      allAreas = getDemoAreas();
    }
  } catch (e) {
    console.warn('[Dashboard] Backend offline — using demo data.');
    allAreas = getDemoAreas();
  }
  renderStats(allAreas);
  renderAreas(allAreas);
}

/* ---- Search ---- */
async function searchAreas() {
  const q = document.getElementById('searchInput').value.trim();
  if (!q) { loadAreas(); return; }

  try {
    const res  = await apiFetch(`${API}/areas?q=${encodeURIComponent(q)}`);
    const data = await res.json();
    allAreas = data.success ? data.data : allAreas.filter(a =>
      a.name.toLowerCase().includes(q.toLowerCase()) ||
      a.location.toLowerCase().includes(q.toLowerCase())
    );
  } catch (e) {
    allAreas = allAreas.filter(a =>
      a.name.toLowerCase().includes(q.toLowerCase()) ||
      a.location.toLowerCase().includes(q.toLowerCase())
    );
  }
  renderAreas(allAreas);
}

function handleSearchKey(e) {
  if (e.key === 'Enter') searchAreas();
}

/* ---- Filter ---- */
function setFilter(filter) {
  activeFilter = filter;
  document.querySelectorAll('.filter-pill').forEach(p => p.classList.remove('active'));
  document.getElementById('filter-' + filter).classList.add('active');

  let filtered = [...allAreas];
  if (filter === 'free')    filtered = filtered.filter(a => (a.freeSlots || 0) > 0);
  if (filter === 'hotspot') filtered = filtered.filter(a => a.isHotspot || a.hotspot);
  if (filter === 'nearby') {
    if (userLat && userLng) {
      filtered = filtered
        .map(a => ({ ...a, dist: haversine(userLat, userLng, a.lat, a.lng) }))
        .sort((a,b) => a.dist - b.dist)
        .slice(0, 5);
    } else {
      useMyLocation();
      return;
    }
  }
  renderAreas(filtered);
}

/* ---- Use GPS ---- */
function useMyLocation() {
  if (!navigator.geolocation) { showToast('Geolocation not supported', 'error'); return; }
  const btn = document.getElementById('locationBtn');
  btn.textContent = '⏳ Getting location...';
  navigator.geolocation.getCurrentPosition(
    async pos => {
      userLat = pos.coords.latitude;
      userLng = pos.coords.longitude;
      btn.textContent = '📍 Location ON';
      try {
        const res  = await apiFetch(`${API}/areas/nearby?lat=${userLat}&lng=${userLng}&radius_km=20`);
        const data = await res.json();
        if (data.success && data.data.length > 0) {
          allAreas = data.data;
          renderAreas(allAreas);
          showToast('Showing areas near you!', 'success');
          return;
        }
      } catch(e) {}
      // Fallback: client-side sort
      allAreas = allAreas.map(a => ({
        ...a,
        distanceKm: haversine(userLat, userLng, a.lat, a.lng)
      })).sort((a,b) => a.distanceKm - b.distanceKm);
      renderAreas(allAreas);
      showToast('Sorted by distance!', 'success');
    },
    () => {
      btn.textContent = '📍 Use My Location';
      showToast('Location permission denied', 'warning');
    }
  );
}

/* ---- Haversine (client-side fallback) ---- */
function haversine(lat1, lng1, lat2, lng2) {
  const R = 6371;
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLng = (lng2 - lng1) * Math.PI / 180;
  const a = Math.sin(dLat/2)**2 + Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dLng/2)**2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
}

/* ---- Render Stats ---- */
function renderStats(areas) {
  const totalFree = areas.reduce((s, a) => s + (a.freeSlots || 0), 0);
  const hotspots  = areas.filter(a => a.isHotspot || a.hotspot).length;
  document.getElementById('statAreas').textContent     = areas.length;
  document.getElementById('statFreeSlots').textContent = totalFree;
  document.getElementById('statHotspots').textContent  = hotspots;
  const banner = document.getElementById('hotspotBanner');
  if (hotspots > 0) banner.style.display = 'flex';
}

/* ---- Render Area Cards ---- */
function renderAreas(areas) {
  const grid  = document.getElementById('areasGrid');
  const label = document.getElementById('resultsLabel');
  label.textContent = `Showing ${areas.length} area${areas.length !== 1 ? 's' : ''}`;

  if (areas.length === 0) {
    grid.innerHTML = `
      <div class="empty-state" style="grid-column:1/-1;">
        <div class="empty-icon">🔍</div>
        <h3>No areas found</h3>
        <p style="color:var(--text-muted);margin-top:0.5rem;">Try a different search term</p>
      </div>`;
    return;
  }

  grid.innerHTML = areas.map(area => {
    const free      = area.freeSlots      ?? 0;
    const total     = area.totalSlots     ?? 20;
    const occupied  = total - free;
    const pct       = Math.round((occupied / total) * 100);
    const isHot     = area.isHotspot || area.hotspot || false;
    const dist      = area.distanceKm ? `${area.distanceKm.toFixed(1)} km` : '';

    return `
    <div class="area-card ${isHot ? 'hotspot' : ''}" onclick="goToBooking(${area.areaId})">
      <div class="area-card-header">
        <div>
          <div class="area-name">${area.name}</div>
          <div class="area-location">📍 ${area.location}</div>
        </div>
        <div>
          ${isHot ? '<span class="badge badge-hotspot">🔥 Hotspot</span>' : ''}
          ${free > 0 ? '<span class="badge badge-free" style="display:block;margin-top:0.25rem;">Available</span>'
                     : '<span class="badge badge-busy" style="display:block;margin-top:0.25rem;">Full</span>'}
        </div>
      </div>

      <div class="area-stats">
        <div class="area-stat">
          <div class="area-stat-value" style="color:var(--accent);">${free}</div>
          <div class="area-stat-label">Free</div>
        </div>
        <div class="area-stat">
          <div class="area-stat-value" style="color:var(--danger);">${occupied}</div>
          <div class="area-stat-label">Occupied</div>
        </div>
        <div class="area-stat">
          <div class="area-stat-value">${total}</div>
          <div class="area-stat-label">Total</div>
        </div>
      </div>

      <div class="area-occupancy-bar">
        <div style="display:flex;justify-content:space-between;font-size:0.78rem;color:var(--text-muted);margin-bottom:0.3rem;">
          <span>Occupancy</span><span>${pct}%</span>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" style="width:${pct}%;background:${pct>70?'linear-gradient(90deg,var(--warning),var(--danger))':'linear-gradient(90deg,var(--primary),var(--accent))'};"></div>
        </div>
      </div>

      <div class="area-card-footer">
        <button class="btn btn-primary btn-sm">View Slots →</button>
        <button class="btn btn-ghost btn-sm" onclick="event.stopPropagation();navigateTo(${area.lat},${area.lng})">
          🗺️ Navigate
        </button>
        ${dist ? `<span class="distance-tag">📏 ${dist}</span>` : ''}
      </div>
    </div>`;
  }).join('');
}

/* ---- Navigation ---- */
function goToBooking(areaId) {
  sessionStorage.setItem('selectedAreaId', areaId);
  window.location.href = 'booking.html';
}

function navigateTo(lat, lng) {
  window.open(`https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}`, '_blank');
}

/* ---- Demo Data (offline fallback) ---- */
function getDemoAreas() {
  return [
    { areaId:1, name:'Central Mall Parking',  location:'MG Road, Bangalore',     totalSlots:20, freeSlots:7,  lat:12.9716, lng:77.5946, isHotspot:true  },
    { areaId:2, name:'Tech Park Parking',     location:'Whitefield, Bangalore',  totalSlots:20, freeSlots:16, lat:12.9698, lng:77.7499, isHotspot:false },
    { areaId:3, name:'Airport Parking Zone',  location:'Devanahalli, Bangalore', totalSlots:20, freeSlots:14, lat:13.1989, lng:77.7068, isHotspot:false },
  ];
}

/* ---- Toast ---- */
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <span>${type==='success'?'✅':type==='error'?'❌':type==='warning'?'⚠️':'ℹ️'}</span>
    <span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 4000);
}
