/* File purpose: Contains supporting implementation for the Fleet Management application. */
const logEl = document.getElementById('activityLog');
const vehiclesListEl = document.getElementById('vehiclesList');
const tripsListEl = document.getElementById('tripsList');
const driversListEl = document.getElementById('driversList');

const healthBadgeEl = document.getElementById('healthBadge');
const totalVehiclesEl = document.getElementById('totalVehicles');
const totalTripsEl = document.getElementById('totalTrips');
const openTripsEl = document.getElementById('openTrips');
const knownDriversEl = document.getElementById('knownDrivers');
const lastUpdatedEl = document.getElementById('lastUpdated');
const heroClockEl = document.getElementById('heroClock');

const assignmentVehicleSelect = document.getElementById('assignmentVehicleSelect');
const assignmentDriverSelect = document.getElementById('assignmentDriverSelect');
const tripVehicleSelect = document.getElementById('tripVehicleSelect');
const tripDriverSelect = document.getElementById('tripDriverSelect');

const vehicleFormErrorEl = document.getElementById('vehicleFormError');
const driverFormErrorEl = document.getElementById('driverFormError');
const assignmentFormErrorEl = document.getElementById('assignmentFormError');
const tripFormErrorEl = document.getElementById('tripFormError');

const DRIVER_CACHE_KEY = 'fleet.console.drivers';

const state = {
  vehicles: [],
  trips: [],
  drivers: loadCachedDrivers(),
};

function log(message, payload) {
  const stamp = new Date().toLocaleTimeString();
  const type = classifyLog(message);
  const entry = document.createElement('div');
  entry.className = `log-entry ${type}`;

  const time = document.createElement('span');
  time.className = 'log-time';
  time.textContent = `[${stamp}]`;

  const text = document.createElement('span');
  text.className = 'log-text';
  text.textContent = message;

  entry.append(time, text);

  if (payload) {
    const details = document.createElement('pre');
    details.className = 'log-payload';
    details.textContent = JSON.stringify(payload, null, 2);
    entry.append(details);
  }

  logEl.prepend(entry);
}

function classifyLog(message) {
  const lower = message.toLowerCase();
  if (lower.includes('failed') || lower.includes('error')) return 'error';
  if (lower.includes('created') || lower.includes('deleted') || lower.includes('completed') || lower.includes('loaded') || lower.includes('refreshed') || lower.includes('synced')) return 'success';
  return 'info';
}

function setHealth(ok, text) {
  healthBadgeEl.textContent = text;
  healthBadgeEl.className = `badge ${ok ? 'badge-ok' : 'badge-err'}`;
}

function updateLastUpdated() {
  lastUpdatedEl.textContent = `Last updated: ${new Date().toLocaleString()}`;
}

function tickHeroClock() {
  if (!heroClockEl) return;
  // Keep a lightweight live clock in the hero for at-a-glance dashboard recency.
  heroClockEl.textContent = new Date().toLocaleTimeString();
}

function loadCachedDrivers() {
  try {
    const parsed = JSON.parse(localStorage.getItem(DRIVER_CACHE_KEY) || '[]');
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function saveCachedDrivers() {
  localStorage.setItem(DRIVER_CACHE_KEY, JSON.stringify(state.drivers));
}

async function api(path, options = {}) {
  const response = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok || data.success === false) {
    throw new Error(data.message || `Request failed (${response.status})`);
  }
  return data;
}

function formToObject(form) {
  const raw = Object.fromEntries(new FormData(form).entries());
  return Object.fromEntries(
    Object.entries(raw)
      .filter(([, v]) => String(v).trim() !== '')
      .map(([k, v]) => {
        if (["year", "startOdometer", "endOdometer", "currentOdometer"].includes(k)) {
          return [k, Number(v)];
        }
        return [k, v];
      })
  );
}

function setFormError(errorEl, message = '') {
  errorEl.textContent = message;
}

function setButtonLoading(button, loading, loadingText) {
  if (!button) return;
  if (loading) {
    // Preserve original text so we can restore exact labels after async work finishes.
    if (!button.dataset.originalText) {
      button.dataset.originalText = button.textContent;
    }
    button.textContent = loadingText;
    button.disabled = true;
    button.classList.add('btn-loading');
  } else {
    button.textContent = button.dataset.originalText || button.textContent;
    button.disabled = false;
    button.classList.remove('btn-loading');
  }
}

function setFormLoading(form, loading, loadingText) {
  const submitBtn = form.querySelector('button[type="submit"]');
  setButtonLoading(submitBtn, loading, loadingText);
}

function populateSelect(selectEl, items, valueFn, labelFn, placeholder) {
  // Keep current selection if possible during list refresh to avoid accidental form resets.
  const current = selectEl.value;
  const options = [`<option value="">${placeholder}</option>`].concat(
    items.map((item) => `<option value="${valueFn(item)}">${labelFn(item)}</option>`)
  );
  selectEl.innerHTML = options.join('');
  if (current) {
    selectEl.value = current;
  }
}

function refreshDropdowns() {
  populateSelect(
    assignmentVehicleSelect,
    state.vehicles,
    (v) => v.vehicleId,
    (v) => `${v.registrationNumber} (${v.vehicleId})`,
    'Select vehicle ID'
  );
  populateSelect(
    tripVehicleSelect,
    state.vehicles,
    (v) => v.vehicleId,
    (v) => `${v.registrationNumber} (${v.vehicleId})`,
    'Select vehicle ID'
  );
  populateSelect(
    assignmentDriverSelect,
    state.drivers,
    (d) => d.driverId,
    (d) => `${d.firstName || 'Driver'} ${d.lastName || ''} (${d.driverId})`,
    'Select driver ID'
  );
  populateSelect(
    tripDriverSelect,
    state.drivers,
    (d) => d.driverId,
    (d) => `${d.firstName || 'Driver'} ${d.lastName || ''} (${d.driverId})`,
    'Select driver ID'
  );
}

function renderVehicles(items) {
  if (!items.length) {
    vehiclesListEl.innerHTML = `
      <div class="item empty-state">
        <div class="empty-icon">ðŸšš</div>
        <h3>No vehicles yet</h3>
        <p>Add a vehicle to start tracking your fleet.</p>
      </div>
    `;
    return;
  }

  vehiclesListEl.innerHTML = items
    .map((item) => `
      <div class="item" data-vehicle-id="${item.vehicleId}">
        <button class="btn btn-small btn-danger icon-btn card-delete" title="Delete vehicle" aria-label="Delete vehicle" data-action="delete-vehicle" data-id="${item.vehicleId}">ðŸ—‘</button>
        <b>${item.registrationNumber}</b>
        <dl class="data-grid">
          <dt>ID</dt><dd>${item.vehicleId}</dd>
          <dt>Type</dt><dd>${item.vehicleType || 'N/A'}</dd>
          <dt>Model</dt><dd>${item.model || 'N/A'}</dd>
          <dt>Year</dt><dd>${item.year || 'N/A'}</dd>
        </dl>
      </div>
    `)
    .join('');
}

function renderTrips(items) {
  if (!items.length) {
    tripsListEl.innerHTML = `
      <div class="item empty-state">
        <div class="empty-icon">ðŸ—ºï¸</div>
        <h3>No trips yet</h3>
        <p>Start a trip to see live movement and status.</p>
      </div>
    `;
    return;
  }

  tripsListEl.innerHTML = items
    .map((item) => {
      const isOpen = !item.endDate;
      return `
        <div class="item trip-item" data-trip-id="${item.tripId}">
          <div class="trip-head">
            <span class="trip-id">Trip #${item.tripId}</span>
            <span class="status-pill ${isOpen ? 'status-open' : 'status-completed'}">${isOpen ? 'OPEN' : 'DONE'}</span>
          </div>
          <button class="btn btn-small btn-danger icon-btn card-delete" title="Delete trip" aria-label="Delete trip" data-action="delete-trip" data-id="${item.tripId}">ðŸ—‘</button>
          <dl class="data-grid">
            <dt>Vehicle</dt><dd>${item.vehicleId}</dd>
            <dt>Driver</dt><dd>${item.driverId}</dd>
            <dt>Purpose</dt><dd>${item.purpose || 'N/A'}</dd>
            <dt>Route</dt><dd>${item.startLocation || 'N/A'}${item.endLocation ? ` -> ${item.endLocation}` : ''}</dd>
          </dl>
          <div class="item-actions">
            <input class="inline-input" data-role="end-location" placeholder="End location" ${isOpen ? '' : 'disabled'} />
            <input class="inline-input" data-role="end-odometer" type="number" min="0" placeholder="End odometer" ${isOpen ? '' : 'disabled'} />
            <button class="btn btn-small btn-accent2" data-action="complete-trip" data-id="${item.tripId}" ${isOpen ? '' : 'disabled'}>Complete Trip</button>
          </div>
        </div>
      `;
    })
    .join('');
}

function renderDrivers(items) {
  if (!items.length) {
    driversListEl.innerHTML = `
      <div class="item empty-state">
        <div class="empty-icon">ðŸ‘¤</div>
        <h3>No known drivers</h3>
        <p>Create a driver to populate assignment and trip dropdowns.</p>
      </div>
    `;
    return;
  }

  driversListEl.innerHTML = items
    .map((d) => `
      <div class="item driver-item" data-driver-id="${d.driverId}">
        <button class="btn btn-small btn-danger icon-btn card-delete" title="Remove cached driver" aria-label="Remove cached driver" data-action="remove-driver" data-id="${d.driverId}">ðŸ—‘</button>
        <div class="driver-copy">
          <b>${d.firstName || 'Driver'} ${d.lastName || ''}</b>
          <dl class="data-grid">
            <dt>ID</dt><dd>${d.driverId}</dd>
            <dt>License</dt><dd>${d.licenseNumber || 'N/A'}</dd>
            <dt>Email</dt><dd>${d.email || 'N/A'}</dd>
          </dl>
        </div>
      </div>
    `)
    .join('');
}

function removeDriverFromCache(driverId) {
  // This only removes cached driver options (localStorage), not server-side driver records.
  state.drivers = state.drivers.filter((d) => String(d.driverId) !== String(driverId));
  saveCachedDrivers();
  renderDrivers(state.drivers);
  refreshDropdowns();
  updateDashboard();
}

function updateDashboard() {
  totalVehiclesEl.textContent = String(state.vehicles.length);
  totalTripsEl.textContent = String(state.trips.length);
  openTripsEl.textContent = String(state.trips.filter((t) => !t.endDate).length);
  knownDriversEl.textContent = String(state.drivers.length);
  updateLastUpdated();
}

async function loadVehicles() {
  const result = await api('/api/vehicles');
  state.vehicles = result.data.content || [];
  renderVehicles(state.vehicles);
  refreshDropdowns();
  updateDashboard();
  log('Loaded vehicles', { count: state.vehicles.length });
}

async function loadTrips() {
  const result = await api('/api/trips');
  state.trips = result.data.content || [];
  renderTrips(state.trips);
  updateDashboard();
  log('Loaded trips', { count: state.trips.length });
}

async function refreshAll() {
  try {
    await Promise.all([loadVehicles(), loadTrips()]);
    renderDrivers(state.drivers);
    setHealth(true, 'API Healthy');
  } catch (err) {
    setHealth(false, `Degraded: ${err.message}`);
    log(`Refresh failed: ${err.message}`);
  }
}

function upsertDriver(driver) {
  const index = state.drivers.findIndex((d) => d.driverId === driver.driverId);
  if (index >= 0) {
    state.drivers[index] = driver;
  } else {
    state.drivers.push(driver);
  }
  saveCachedDrivers();
  renderDrivers(state.drivers);
  refreshDropdowns();
  updateDashboard();
}

document.getElementById('vehicleForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const form = e.currentTarget;
  setFormError(vehicleFormErrorEl);
  setFormLoading(form, true, 'Adding...');
  try {
    const payload = formToObject(form);
    const res = await api('/api/vehicles', { method: 'POST', body: JSON.stringify(payload) });
    log('Vehicle created', res.data);
    form.reset();
    await loadVehicles();
  } catch (err) {
    setFormError(vehicleFormErrorEl, err.message);
    log(`Vehicle create failed: ${err.message}`);
  } finally {
    setFormLoading(form, false, 'Adding...');
  }
});

document.getElementById('driverForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const form = e.currentTarget;
  setFormError(driverFormErrorEl);
  setFormLoading(form, true, 'Adding...');
  try {
    const payload = formToObject(form);
    const res = await api('/api/drivers', { method: 'POST', body: JSON.stringify(payload) });
    upsertDriver(res.data);
    log('Driver created', res.data);
    form.reset();
  } catch (err) {
    setFormError(driverFormErrorEl, err.message);
    log(`Driver create failed: ${err.message}`);
  } finally {
    setFormLoading(form, false, 'Adding...');
  }
});

document.getElementById('assignmentForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const form = e.currentTarget;
  setFormError(assignmentFormErrorEl);
  setFormLoading(form, true, 'Assigning...');
  try {
    const payload = formToObject(form);
    payload.assignmentDate = new Date().toISOString().slice(0, 10);
    const res = await api('/api/assignments', { method: 'POST', body: JSON.stringify(payload) });
    log('Assignment created', res.data);
    form.reset();
  } catch (err) {
    setFormError(assignmentFormErrorEl, err.message);
    log(`Assignment create failed: ${err.message}`);
  } finally {
    setFormLoading(form, false, 'Assigning...');
  }
});

document.getElementById('tripForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const form = e.currentTarget;
  setFormError(tripFormErrorEl);
  setFormLoading(form, true, 'Starting...');
  try {
    const payload = formToObject(form);
    payload.startDate = new Date().toISOString();
    const res = await api('/api/trips', { method: 'POST', body: JSON.stringify(payload) });
    log('Trip created', res.data);
    form.reset();
    await loadTrips();
  } catch (err) {
    setFormError(tripFormErrorEl, err.message);
    log(`Trip create failed: ${err.message}`);
  } finally {
    setFormLoading(form, false, 'Starting...');
  }
});

vehiclesListEl.addEventListener('click', async (e) => {
  const btn = e.target.closest('button[data-action="delete-vehicle"]');
  if (!btn) return;

  const vehicleId = btn.dataset.id;
  const confirmed = window.confirm(`Delete vehicle ${vehicleId}? This cannot be undone.`);
  if (!confirmed) return;

  setButtonLoading(btn, true, 'Deleting');
  try {
    await api(`/api/vehicles/${vehicleId}`, { method: 'DELETE' });
    log(`Vehicle deleted: ${vehicleId}`);
    await refreshAll();
  } catch (err) {
    log(`Vehicle delete failed: ${err.message}`);
  } finally {
    setButtonLoading(btn, false, 'Deleting');
  }
});

tripsListEl.addEventListener('click', async (e) => {
  const btn = e.target.closest('button[data-action]');
  if (!btn) return;

  const tripId = btn.dataset.id;

  if (btn.dataset.action === 'delete-trip') {
    const confirmed = window.confirm(`Delete trip ${tripId}? This cannot be undone.`);
    if (!confirmed) return;

    setButtonLoading(btn, true, 'Deleting');
    try {
      await api(`/api/trips/${tripId}`, { method: 'DELETE' });
      log(`Trip deleted: ${tripId}`);
      await loadTrips();
    } catch (err) {
      log(`Trip delete failed: ${err.message}`);
    } finally {
      setButtonLoading(btn, false, 'Deleting');
    }
    return;
  }

  if (btn.dataset.action === 'complete-trip') {
    const card = btn.closest('.item');
    const endLocation = card.querySelector('[data-role="end-location"]').value.trim();
    const endOdometerRaw = card.querySelector('[data-role="end-odometer"]').value.trim();

    if (!endLocation || !endOdometerRaw) {
      log('Complete trip failed: end location and end odometer are required.');
      return;
    }

    setButtonLoading(btn, true, 'Completing');
    try {
      const payload = { endLocation, endOdometer: Number(endOdometerRaw) };
      const res = await api(`/api/trips/${tripId}/complete`, { method: 'POST', body: JSON.stringify(payload) });
      log(`Trip completed: ${tripId}`, res.data);
      await loadTrips();
    } catch (err) {
      log(`Trip complete failed: ${err.message}`);
    } finally {
      setButtonLoading(btn, false, 'Completing');
    }
  }
});

driversListEl.addEventListener('click', (e) => {
  const btn = e.target.closest('button[data-action="remove-driver"]');
  if (!btn) return;

  const driverId = btn.dataset.id;
  removeDriverFromCache(driverId);
  log(`Cached driver removed: ${driverId}`);
});

document.getElementById('loadVehicles').addEventListener('click', () => {
  loadVehicles().catch((err) => log(`Load vehicles failed: ${err.message}`));
});

document.getElementById('loadTrips').addEventListener('click', () => {
  loadTrips().catch((err) => log(`Load trips failed: ${err.message}`));
});

document.getElementById('refreshVehicleOptions').addEventListener('click', () => {
  loadVehicles().catch((err) => log(`Vehicle sync failed: ${err.message}`));
});

document.getElementById('refreshDriverOptions').addEventListener('click', () => {
  renderDrivers(state.drivers);
  refreshDropdowns();
  log('Driver options synced from local cache.');
});

document.getElementById('refreshDashboard').addEventListener('click', async () => {
  await refreshAll();
  log('Dashboard refreshed.');
});

document.getElementById('clearLog').addEventListener('click', () => {
  logEl.innerHTML = '';
});

tickHeroClock();
setInterval(tickHeroClock, 1000);
renderDrivers(state.drivers);
refreshDropdowns();
refreshAll();

