/**
 * Silicon FoodCourt - Frontend Logic
 * All data flows through the Java Servlet backend.
 */

document.addEventListener('DOMContentLoaded', () => {
  // Protect protected pages
  const isPublicPage = window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('register.html') || window.location.pathname === '/' || window.location.pathname.endsWith('/hostelmess/') || window.location.pathname.endsWith('/hostelmessmanagement/');
  if (!getCurrentUser() && !isPublicPage) {
    window.location.href = 'index.html';
    return;
  }

  initAuthTabs();
  initUserNav();       // Populate name/room/avatar on every page from sessionStorage

  // Page-specific inits
  if (document.getElementById('student-login'))   initLogin();
  if (document.getElementById('register-form'))   initRegistration();
  if (document.getElementById('dash-menu-date'))  initStudentDashboard();
  if (document.getElementById('student-menu-list')) initStudentMenuPage();
  if (document.getElementById('update-menu-form')) initMenuAdminPage();
  if (document.getElementById('inventory-table')) initInventoryPage();
  if (document.getElementById('students-table'))  initAdminStudentsPage();
  if (document.getElementById('stat-lunch'))      initAdminDashboard();
  if (document.getElementById('extras-table'))    initExtrasPage();
  if (document.getElementById('optout-form'))     initOptOutPage();
  if (document.getElementById('bill-page-total')) initStudentBillPage();

  initLogoutLinks();
});

// Global for inline button onclick
window.markAttendancePrompt = function() {
  const studentId = prompt("Enter Student ID to mark attendance:");
  if (!studentId) return;
  const params = new URLSearchParams();
  params.append('studentId', studentId);
  params.append('attendanceDate', new Date().toISOString().split('T')[0]);
  params.append('breakfast', '1');
  params.append('lunch', '1');
  params.append('dinner', '1');
  
  fetch('api/attendance', { method: 'POST', body: params })
    .then(r => r.json())
    .then(d => {
      if(d.status === 'success') showToast('Attendance marked for ' + studentId);
      else showToast(d.message || 'Error', 'error');
    }).catch(() => showToast('Network Error', 'error'));
};

/* ================================================
   AUTH TABS (index.html)
================================================ */
function initAuthTabs() {
  const tabs = document.querySelectorAll('.auth-tab');
  if (tabs.length === 0) return;
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      document.querySelectorAll('.auth-form').forEach(f => f.style.display = 'none');
      const targetForm = document.getElementById(tab.getAttribute('data-target'));
      if (targetForm) {
        targetForm.style.display = 'block';
        targetForm.style.opacity = '0';
        targetForm.style.transform = 'translateY(10px)';
        requestAnimationFrame(() => {
          targetForm.style.transition = 'all 0.3s ease';
          targetForm.style.opacity = '1';
          targetForm.style.transform = 'translateY(0)';
        });
      }
    });
  });
}

/* ================================================
   REGISTRATION
================================================ */
function initRegistration() {
  document.getElementById('register-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    params.append('name',             document.getElementById('reg-name').value.trim());
    params.append('studentId',        document.getElementById('reg-id').value.trim());
    params.append('roomNo',           document.getElementById('reg-room').value.trim());
    params.append('subscriptionType', document.querySelector('input[name="sub_plan"]:checked').value);
    params.append('password',         document.getElementById('reg-pass').value);

    fetch('api/auth/register', { method: 'POST', body: params })
      .then(r => r.json())
      .then(data => {
        if (data.status === 'success') {
          showToast('Registration successful! Redirecting...', 'success');
          setTimeout(() => window.location.href = 'index.html', 1200);
        } else {
          showToast(data.message || 'Registration failed. Student ID may already exist.', 'error');
        }
      })
      .catch(() => showToast('Network error. Is Tomcat running?', 'error'));
  });
}

/* ================================================
   LOGIN (Student & Admin)
================================================ */
function initLogin() {
  // --- Student Login ---
  const studentForm = document.getElementById('student-login');
  if (studentForm) {
    studentForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const params = new URLSearchParams();
      params.append('studentId', document.getElementById('student-id').value.trim());
      params.append('password',  document.getElementById('student-pass').value);

      fetch('api/auth/login', { method: 'POST', body: params })
        .then(r => r.json())
        .then(data => {
          if (data.status === 'success') {
            sessionStorage.setItem('currentUser', JSON.stringify(data.user));
            showToast('Welcome back, ' + data.user.name + '!', 'success');
            setTimeout(() => {
              window.location.href = data.user.role === 'admin'
                ? 'admin-dashboard.html'
                : 'student-dashboard.html';
            }, 1000);
          } else {
            showToast(data.message || 'Invalid credentials', 'error');
          }
        })
        .catch(() => showToast('Network error. Is Tomcat running?', 'error'));
    });
  }

  // --- Admin Login (simple hardcoded check for demo; wire to backend if needed) ---
  const adminForm = document.getElementById('admin-login');
  if (adminForm) {
    adminForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const id   = document.getElementById('admin-id').value.trim();
      const pass = document.getElementById('admin-pass').value;
      // Try same backend login
      const params = new URLSearchParams();
      params.append('studentId', id);
      params.append('password',  pass);
      fetch('api/auth/login', { method: 'POST', body: params })
        .then(r => r.json())
        .then(data => {
          if (data.status === 'success') {
            sessionStorage.setItem('currentUser', JSON.stringify(data.user));
            showToast('Admin login successful!', 'success');
            setTimeout(() => window.location.href = 'admin-dashboard.html', 1000);
          } else {
            showToast(data.message || 'Invalid admin credentials', 'error');
          }
        })
        .catch(() => showToast('Network error.', 'error'));
    });
  }
}

/* ================================================
   POPULATE USER INFO IN NAVBAR (all pages)
================================================ */
function initUserNav() {
  const user = getCurrentUser();
  if (!user) return;

  // Greeting / welcome text in dashboard hero
  const greetingEl = document.getElementById('welcome-greeting');
  if (greetingEl) {
    const hour = new Date().getHours();
    const greet = hour < 12 ? 'Good Morning' : hour < 17 ? 'Good Afternoon' : 'Good Evening';
    greetingEl.textContent = `${greet}, ${user.name}`;
  }

  // Room number in navbar
  document.querySelectorAll('.user-room').forEach(el => el.textContent = 'Room ' + user.roomNo);

  // Avatar initials
  const initials = user.name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
  document.querySelectorAll('.avatar').forEach(el => {
    if (!el.dataset.fixed) el.textContent = initials;
  });

  // Hardcoded "Room 402" and "AK" replacement
  document.querySelectorAll('.user-profile span').forEach(el => {
    if (el.textContent === 'Room 402') el.textContent = 'Room ' + user.roomNo;
  });
}

function getCurrentUser() {
  try { return JSON.parse(sessionStorage.getItem('currentUser')); } catch { return null; }
}

/* ================================================
   LOGOUT
================================================ */
window.doLogout = function(e) {
  if (e) e.preventDefault();
  fetch('api/auth/logout', { method: 'POST' }).finally(() => {
    sessionStorage.removeItem('currentUser');
    window.location.href = 'index.html';
  });
};

function initLogoutLinks() {
  document.querySelectorAll('a[href="index.html"]').forEach(link => {
    if (link.textContent.includes('Logout')) {
      link.addEventListener('click', window.doLogout);
    }
  });
}

/* ================================================
   STUDENT DASHBOARD
================================================ */
function initStudentDashboard() {
  const user = getCurrentUser();
  if (!user) return;

  const now   = new Date();
  const today = now.toISOString().split('T')[0];

  // Date label
  const dateEl = document.getElementById('dash-menu-date');
  if (dateEl) dateEl.textContent = today;

  // 1. Load today's menu & handle Time Windows
  fetchMenuForDate(today).then(meals => {
    document.getElementById('dash-menu-breakfast').textContent = meals.Breakfast || 'Not set';
    document.getElementById('dash-menu-lunch').textContent     = meals.Lunch     || 'Not set';
    document.getElementById('dash-menu-dinner').textContent    = meals.Dinner    || 'Not set';

    const promptEl  = document.getElementById('welcome-meal-prompt');
    const itemsEl   = document.getElementById('welcome-meal-items');
    const timeEl    = document.getElementById('welcome-meal-time');
    const badgeEl   = document.getElementById('welcome-meal-badge');
    const quickOptLabel = document.getElementById('quick-opt-label');

    if (promptEl) {
      const hours   = now.getHours();
      const mins    = now.getMinutes();
      const timeVal = hours + mins/60.0;
      
      let mealKey, mealLabel, until, isServing;

      // Time Windows:
      // Breakfast: 07:30 to 09:30
      // Lunch: 12:30 to 14:30
      // Dinner: 19:30 to 21:30
      
      if (timeVal < 7.5) {
        mealKey = 'Breakfast'; mealLabel = 'Breakfast'; until = 'Starts at 07:30 AM'; isServing = false;
      } else if (timeVal >= 7.5 && timeVal <= 9.5) {
        mealKey = 'Breakfast'; mealLabel = 'Breakfast'; until = 'Available until 09:30 AM'; isServing = true;
      } else if (timeVal > 9.5 && timeVal < 12.5) {
        mealKey = 'Lunch'; mealLabel = 'Lunch'; until = 'Starts at 12:30 PM'; isServing = false;
      } else if (timeVal >= 12.5 && timeVal <= 14.5) {
        mealKey = 'Lunch'; mealLabel = 'Lunch'; until = 'Available until 02:30 PM'; isServing = true;
      } else if (timeVal > 14.5 && timeVal < 19.5) {
        mealKey = 'Dinner'; mealLabel = 'Dinner'; until = 'Starts at 07:30 PM'; isServing = false;
      } else if (timeVal >= 19.5 && timeVal <= 21.5) {
        mealKey = 'Dinner'; mealLabel = 'Dinner'; until = 'Available until 09:30 PM'; isServing = true;
      } else {
        mealKey = 'Breakfast'; mealLabel = "Tomorrow's Breakfast"; until = 'Starts at 07:30 AM tomorrow'; isServing = false;
      }

      promptEl.textContent = isServing ? `Ready for ${mealLabel}?` : `Upcoming: ${mealLabel}`;
      itemsEl.textContent  = meals[mealKey] || 'Menu not published yet';
      timeEl.textContent   = until;
      badgeEl.textContent  = isServing ? 'Serving Now' : 'Closed';
      badgeEl.className    = isServing ? 'badge badge-success' : 'badge badge-danger';
      
      if (quickOptLabel) quickOptLabel.textContent = `Opt out of ${mealLabel}`;

      // Initialize Opt-Out Toggle for this upcoming/current meal
      initOptOutToggle(today, mealKey, user.studentId);
    }
  });

  // 2. Fetch Live Bill
  fetch('api/bill/live?studentId=' + encodeURIComponent(user.studentId))
    .then(r => r.json())
    .then(data => {
      if (data.status === 'success') {
        const billVal = document.querySelector('.bento-stat.delay-3 .stat-value');
        const detailsDiv = document.querySelector('.bento-stat.delay-3 .flex.justify-between.text-muted');
        if (billVal) billVal.textContent = '₹' + data.total;
        if (detailsDiv) {
          detailsDiv.innerHTML = `<span>Meals Attended: ${data.attendedMeals}</span><span>Extras: ₹${data.extras}</span>`;
        }
      }
    }).catch(console.error);

  // 3. Fetch Weekly Attendance Sync
  fetch('api/attendance?studentId=' + encodeURIComponent(user.studentId))
    .then(r => r.json())
    .then(records => {
      // Calculate attendance for the last 7 days
      let count = 0;
      const sevenDaysAgo = new Date();
      sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);
      
      records.forEach(r => {
        const d = new Date(r.attendanceDate);
        if (d >= sevenDaysAgo) {
          count += r.breakfast + r.lunch + r.dinner;
        }
      });
      const attEl = document.getElementById('att-week-count');
      if (attEl) {
        attEl.innerHTML = `${count} <span style="font-size: 1rem; color: var(--text-muted); font-family: var(--font-sans);">/ 21 meals</span>`;
      }
    }).catch(console.error);
}

function initOptOutToggle(date, meal, studentId) {
  const toggle = document.getElementById('opt-out-toggle');
  if (!toggle) return;
  
  // Check existing status
  fetch('api/optout?studentId=' + encodeURIComponent(studentId))
    .then(r => r.json())
    .then(optOuts => {
      // Look for active opt-out for this date & meal
      const existing = optOuts.find(o => o.optoutDate === date && o.mealType === meal && o.status !== 'Cancelled');
      toggle.checked = !!existing;
      
      // Add event listener to handle toggle
      toggle.addEventListener('change', (e) => {
        const isOptingOut = e.target.checked;
        const params = new URLSearchParams();
        
        if (isOptingOut) {
          params.append('studentId', studentId);
          params.append('optoutDate', date);
          params.append('mealType', meal);
          fetch('api/optout', { method: 'POST', body: params })
            .then(r => r.json())
            .then(d => {
              if(d.status === 'success') showToast(`Opted out of ${meal}`);
              else { showToast(d.message || 'Error', 'error'); toggle.checked = false; }
            });
        } else {
          // If un-checking, we need to find the opt-out ID to cancel it.
          // For simplicity in this UI, we might just reload the list or we need the ID.
          // We will fetch it fresh to guarantee we have the ID.
          fetch('api/optout?studentId=' + encodeURIComponent(studentId))
            .then(r => r.json())
            .then(freshOpts => {
               const activeOpt = freshOpts.find(o => o.optoutDate === date && o.mealType === meal && o.status !== 'Cancelled');
               if (activeOpt) {
                 const cancelParams = new URLSearchParams();
                 cancelParams.append('action', 'updateStatus');
                 cancelParams.append('optoutId', activeOpt.optoutId);
                 cancelParams.append('status', 'Cancelled');
                 fetch('api/optout', { method: 'POST', body: cancelParams })
                   .then(r => r.json())
                   .then(d => {
                      if(d.status === 'success') showToast(`Opt-in restored for ${meal}`);
                      else { showToast('Error', 'error'); toggle.checked = true; }
                   });
               }
            });
        }
      });
    });
}

/* ================================================
   STUDENT BILL PAGE
================================================ */
function initStudentBillPage() {
  const user = getCurrentUser();
  if (!user) return;
  
  fetch('api/bill/live?studentId=' + encodeURIComponent(user.studentId))
    .then(r => r.json())
    .then(data => {
      if (data.status === 'success') {
        const totalEls = document.querySelectorAll('#bill-page-total, #bill-page-total-calc');
        const dueEl = document.getElementById('bill-page-due');
        const baseEl = document.getElementById('bill-page-base');
        const baseLabelEl = document.getElementById('bill-page-base-label');
        const extrasEl = document.getElementById('bill-page-extras');
        
        totalEls.forEach(el => el.textContent = '₹' + data.total);
        if (baseEl) baseEl.textContent = '₹' + data.baseAmount;
        if (baseLabelEl) baseLabelEl.textContent = `Meals Attended Fee (${data.attendedMeals} meals @ ₹50)`;
        if (extrasEl) extrasEl.textContent = '+ ₹' + data.extras;
        
        if (dueEl) {
          // Set due date to 5th of next month
          const [year, month] = data.month.split('-');
          let d = new Date(year, month, 5); // month is 0-indexed, so passing `month` gets next month
          dueEl.textContent = 'Due by ' + d.toLocaleDateString('en-GB', {day: 'numeric', month: 'short', year: 'numeric'});
        }
      }
    }).catch(console.error);
}

/* ================================================
   STUDENT MENU PAGE
================================================ */
function initStudentMenuPage() {
  const datePicker = document.getElementById('menu-date-picker');
  const today = new Date().toISOString().split('T')[0];
  datePicker.value = today;
  loadStudentMenu(today);
  datePicker.addEventListener('change', e => loadStudentMenu(e.target.value));
}

function loadStudentMenu(date) {
  document.getElementById('display-date').textContent = date;
  fetchMenuForDate(date).then(meals => {
    document.getElementById('s-menu-breakfast').textContent = meals.Breakfast || 'Not set';
    document.getElementById('s-menu-lunch').textContent     = meals.Lunch     || 'Not set';
    document.getElementById('s-menu-dinner').textContent    = meals.Dinner    || 'Not set';
  });
}

/* Fetch menu from backend and return {Breakfast, Lunch, Dinner} */
function fetchMenuForDate(date) {
  return fetch(`api/menu?date=${date}`)
    .then(r => r.json())
    .then(arr => {
      const map = {};
      arr.forEach(m => map[m.mealType] = m.items);
      return map;
    })
    .catch(() => ({}));
}

/* ================================================
   ADMIN MENU PAGE – post each meal to backend
================================================ */
function initMenuAdminPage() {
  // Pre-fill today's date
  const dateInput = document.getElementById('menu-date');
  if (dateInput && !dateInput.value) dateInput.value = new Date().toISOString().split('T')[0];

  document.getElementById('update-menu-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const date      = document.getElementById('menu-date').value;
    const breakfast = document.getElementById('menu-breakfast').value.trim();
    const lunch     = document.getElementById('menu-lunch').value.trim();
    const dinner    = document.getElementById('menu-dinner').value.trim();

    const meals = [
      { mealType: 'Breakfast', items: breakfast },
      { mealType: 'Lunch',     items: lunch     },
      { mealType: 'Dinner',    items: dinner    }
    ];

    let allOk = true;
    for (const meal of meals) {
      if (!meal.items) continue;
      const params = new URLSearchParams();
      params.append('menuDate', date);
      params.append('mealType', meal.mealType);
      params.append('items',    meal.items);
      const r = await fetch('api/menu', { method: 'POST', body: params });
      const d = await r.json();
      if (d.status !== 'success') { allOk = false; }
    }

    showToast(allOk ? `Menu published for ${date}!` : 'Some meals failed to save.', allOk ? 'success' : 'error');
  });
}


/* ================================================
   OPT-OUT PAGE
================================================ */
function initOptOutPage() {
  const user = getCurrentUser();
  if (!user) return;
  const renderTable = () => {
    const tbody = document.querySelector('#optouts-table tbody');
    tbody.innerHTML = '<tr><td colspan="4">Loading...</td></tr>';
    
    fetch('api/optout?studentId=' + encodeURIComponent(user.studentId))
      .then(r => r.json())
      .then(optOuts => {
        tbody.innerHTML = '';
        if (optOuts.length === 0) {
          tbody.innerHTML = '<tr><td colspan="4">No opt-outs scheduled.</td></tr>';
          return;
        }
        optOuts.forEach(opt => {
          let badgeClass = opt.status === 'Cancelled' ? 'badge-danger' : 'badge-success';
          let actionBtn = opt.status === 'Cancelled' ? '' : `<button class="btn btn-outline" style="padding:0.25rem 0.5rem;font-size:0.75rem;color:var(--danger);border-color:var(--danger);" onclick="cancelOptOut(${opt.optoutId})">Cancel</button>`;
          tbody.innerHTML += `<tr>
            <td>${opt.optoutDate}</td><td>${opt.mealType}</td>
            <td><span class="badge ${badgeClass}">${opt.status}</span></td>
            <td>${actionBtn}</td>
          </tr>`;
        });
      }).catch(() => { tbody.innerHTML = '<tr><td colspan="4">Error loading opt-outs.</td></tr>'; });
  };
  renderTable();

  window.cancelOptOut = (id) => {
    if(!confirm('Cancel this opt-out?')) return;
    const params = new URLSearchParams();
    params.append('action', 'updateStatus');
    params.append('optoutId', id);
    params.append('status', 'Cancelled');
    
    fetch('api/optout', { method: 'POST', body: params })
      .then(r => r.json())
      .then(d => {
        if(d.status === 'success') { showToast('Opt-out cancelled.'); renderTable(); }
        else { showToast(d.message || 'Error', 'error'); }
      }).catch(() => showToast('Network Error', 'error'));
  };

  document.getElementById('optout-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    params.append('studentId', user.studentId);
    params.append('optoutDate', document.getElementById('opt-date').value);
    params.append('mealType', document.getElementById('opt-meal').value);
    
    fetch('api/optout', { method: 'POST', body: params })
      .then(r => r.json())
      .then(d => {
        if(d.status === 'success') { showToast('Opt-out scheduled!'); e.target.reset(); renderTable(); }
        else { showToast(d.message || 'Error', 'error'); }
      }).catch(() => showToast('Network Error', 'error'));
  });
}

/* ================================================
   INVENTORY PAGE
================================================ */
function initInventoryPage() {
  window.renderInventoryTable = () => {
    const tbody = document.querySelector('#inventory-table tbody');
    tbody.innerHTML = '<tr><td colspan="5">Loading...</td></tr>';
    fetch('api/inventory').then(r => r.json()).then(items => {
      tbody.innerHTML = '';
      window.inventoryItems = window.inventoryItems || {};
      items.forEach(item => {
        const threshold = item.threshold || 10;
        const status = item.quantity <= threshold
          ? '<span class="badge badge-danger">Low</span>'
          : '<span class="badge badge-success">Good</span>';
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td>#${item.inventoryId || item.id}</td>
          <td>${item.name}</td>
          <td>${item.quantity} ${item.unit}</td>
          <td>${status}</td>
          <td style="display:flex;gap:0.5rem;">
            <button class="btn btn-outline" style="padding:0.25rem 0.5rem;font-size:0.75rem;" onclick="editInventoryRow(this,${item.inventoryId || item.id})">Edit</button>
          </td>`;
        tbody.appendChild(tr);
        window.inventoryItems[item.inventoryId || item.id] = item;
      });
    }).catch(() => { tbody.innerHTML = '<tr><td colspan="5">Failed to load inventory.</td></tr>'; });
  };

  window.editInventoryRow = (btn, id) => {
    const tr   = btn.closest('tr');
    const item = window.inventoryItems[id];
    tr.innerHTML = `
      <td>#${item.inventoryId || item.id}</td>
      <td><input type="text" class="input-field" style="padding:0.25rem;font-size:0.85rem;" value="${item.name}" id="edit-name-${id}" readonly style="opacity: 0.7;"></td>
      <td style="display:flex;gap:0.25rem;">
        <input type="number" class="input-field" style="width:60px;padding:0.25rem;" value="${item.quantity}" id="edit-qty-${id}">
        <input type="text"   class="input-field" style="width:50px;padding:0.25rem;" value="${item.unit}" id="edit-unit-${id}">
      </td>
      <td><div style="font-size:0.7rem;margin-bottom:2px;">Threshold:</div>
        <input type="number" class="input-field" style="width:60px;padding:0.25rem;" value="${item.threshold || 10}" id="edit-threshold-${id}">
      </td>
      <td style="display:flex;gap:0.5rem;">
        <button class="btn btn-primary" style="padding:0.25rem 0.5rem;font-size:0.75rem;" onclick="saveInventoryRow(${id})">Save</button>
        <button class="btn btn-outline"  style="padding:0.25rem 0.5rem;font-size:0.75rem;" onclick="window.renderInventoryTable()">Cancel</button>
      </td>`;
  };

  window.saveInventoryRow = (id) => {
    const params = new URLSearchParams();
    params.append('name', document.getElementById(`edit-name-${id}`).value);
    params.append('quantity', document.getElementById(`edit-qty-${id}`).value);
    params.append('unit', document.getElementById(`edit-unit-${id}`).value);
    params.append('threshold', document.getElementById(`edit-threshold-${id}`).value);
    
    fetch('api/inventory', { method: 'POST', body: params })
      .then(r => r.json())
      .then(d => {
        if(d.status === 'success') { showToast('Inventory updated!'); window.renderInventoryTable(); }
        else { showToast(d.message || 'Error', 'error'); }
      }).catch(() => showToast('Network Error', 'error'));
  };

  window.renderInventoryTable();

  const form = document.getElementById('add-inventory-form');
  if (form && !form.dataset.bound) {
    form.dataset.bound = 'true';
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const params = new URLSearchParams();
      params.append('name', document.getElementById('inv-name').value);
      params.append('quantity', document.getElementById('inv-qty').value);
      params.append('unit', document.getElementById('inv-unit').value);
      params.append('threshold', 10);
      
      fetch('api/inventory', { method: 'POST', body: params })
        .then(r => r.json())
        .then(d => {
          if(d.status === 'success') { showToast('Inventory added!'); e.target.reset(); window.renderInventoryTable(); }
          else { showToast(d.message || 'Error', 'error'); }
        }).catch(() => showToast('Network Error', 'error'));
    });
  }
}

/* ================================================
   ADMIN DASHBOARD STATS
================================================ */
function initAdminDashboard() {
  fetch('api/stats').then(r => r.json()).then(data => {
    const mealLabel = document.getElementById('stat-meal-label');
    if (mealLabel) mealLabel.textContent = 'Expected for ' + data.nextMeal;
    document.getElementById('stat-lunch').textContent = data.expectedCount;
    document.getElementById('stat-total').textContent = data.totalStudents;
  }).catch(() => {});

  fetch('api/extras').then(r => r.json()).then(charges => {
    const tbody = document.querySelector('#recent-extras-table tbody');
    tbody.innerHTML = '';
    if (charges.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5">No recent charges.</td></tr>';
      return;
    }
    // Only show top 5
    charges.slice(0, 5).forEach(c => {
      tbody.innerHTML += `<tr><td>${c.chargeDate}</td><td>${c.studentId}</td><td>${c.item}</td><td>${c.qty}</td><td>₹${c.amount}</td></tr>`;
    });
  }).catch(() => {});

  // Load inventory alerts
  const alertsDiv = document.getElementById('inventory-alerts');
  if (alertsDiv) {
    fetch('api/inventory').then(r => r.json()).then(items => {
      alertsDiv.innerHTML = '';
      if (!items || items.length === 0) {
        alertsDiv.innerHTML = '<p class="text-muted" style="font-size:0.85rem;">No inventory items. <a href="admin-inventory.html" style="color:var(--primary);">Add items →</a></p>';
        return;
      }
      const lowItems = items.filter(i => i.qty <= (i.threshold || 10));
      if (lowItems.length === 0) {
        alertsDiv.innerHTML = '<p style="font-size:0.85rem; color: var(--secondary);">✓ All items well stocked</p>';
        return;
      }
      lowItems.forEach(item => {
        alertsDiv.innerHTML += `<div class="flex justify-between items-center" style="margin-bottom: 0.75rem;">
          <span style="font-weight: 500;">${item.name}</span>
          <span class="badge badge-danger">Low: ${item.qty} ${item.unit || ''}</span>
        </div>`;
      });
    }).catch(() => {
      alertsDiv.innerHTML = '<p class="text-muted" style="font-size:0.85rem;">Could not load inventory.</p>';
    });
  }
}

/* ================================================
   ADMIN STUDENTS PAGE
================================================ */
function initAdminStudentsPage() {
  fetch('api/students').then(r => r.json()).then(students => {
    const tbody = document.querySelector('#students-table tbody');
    tbody.innerHTML = '';
    if (students.length === 0) {
      tbody.innerHTML = '<tr><td colspan="4">No students registered.</td></tr>';
      return;
    }
    students.forEach(s => {
      tbody.innerHTML += `<tr><td>${s.studentId}</td><td>${s.name}</td><td>${s.roomNo}</td><td>${s.subscriptionType}</td></tr>`;
    });
  }).catch(() => {
    document.querySelector('#students-table tbody').innerHTML = '<tr><td colspan="4">Failed to load students.</td></tr>';
  });
}

/* ================================================
   EXTRA CHARGES PAGE
================================================ */
function initExtrasPage() {
  const renderTable = () => {
    const tbody = document.querySelector('#extras-table tbody');
    tbody.innerHTML = '<tr><td colspan="5">Loading...</td></tr>';
    fetch('api/extras').then(r => r.json()).then(charges => {
      tbody.innerHTML = '';
      if (charges.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">No charges logged yet.</td></tr>';
        return;
      }
      charges.forEach(c => {
        tbody.innerHTML += `<tr><td>${c.chargeDate}</td><td>${c.studentId}</td><td>${c.item}</td><td>${c.qty}</td><td>₹${c.amount}</td></tr>`;
      });
    }).catch(() => tbody.innerHTML = '<tr><td colspan="5">Error loading charges.</td></tr>');
  };
  renderTable();

  document.getElementById('add-extra-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    params.append('chargeDate', new Date().toISOString().split('T')[0]);
    params.append('studentId', document.getElementById('ext-student').value);
    params.append('item', document.getElementById('ext-item').value);
    params.append('qty', document.getElementById('ext-qty').value);
    params.append('amount', document.getElementById('ext-amount').value);

    fetch('api/extras', { method: 'POST', body: params })
      .then(r => r.json())
      .then(d => {
        if(d.status === 'success') {
          showToast('Charge logged!');
          e.target.reset();
          renderTable();
        } else {
          showToast('Failed to log charge.', 'error');
        }
      }).catch(() => showToast('Network Error', 'error'));
  });
}

/* ================================================
   TOAST NOTIFICATION
================================================ */
function showToast(message, type = 'success') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.style.cssText = 'position:fixed;bottom:2rem;right:2rem;display:flex;flex-direction:column;gap:0.5rem;z-index:9999;';
    document.body.appendChild(container);
  }
  const bgColor   = type === 'success' ? 'var(--primary)' : type === 'error' ? '#c0392b' : 'var(--bg-surface-elevated)';
  const textColor = (type === 'success' || type === 'error') ? '#fff' : 'var(--text-main)';
  const toast = document.createElement('div');
  toast.style.cssText = `background:${bgColor};color:${textColor};padding:1rem 1.5rem;border-radius:var(--br-sm);box-shadow:0 10px 30px rgba(0,0,0,0.3);font-size:0.875rem;font-weight:500;opacity:0;transform:translateY(20px);transition:all 0.3s ease;border:1px solid rgba(255,255,255,0.1);max-width:320px;`;
  toast.textContent = message;
  container.appendChild(toast);
  requestAnimationFrame(() => { toast.style.opacity = '1'; toast.style.transform = 'translateY(0)'; });
  setTimeout(() => { toast.style.opacity = '0'; toast.style.transform = 'translateY(20px)'; setTimeout(() => toast.remove(), 300); }, 3500);
}

/* ================================================
   PROFILE DROPDOWN
================================================ */
window.toggleProfileDropdown = function(e) {
  if (e) e.stopPropagation();
  const dropdown = document.getElementById('profile-dropdown');
  if (dropdown) dropdown.classList.toggle('active');
};

window.doLogout = function(e) {
  if (e) e.preventDefault();
  fetch('api/auth/logout', { method: 'POST' }).finally(() => {
    sessionStorage.removeItem('currentUser');
    window.location.href = 'index.html';
  });
};

// Populate dropdown with user info + close on outside click
(function() {
  function initDropdownInfo() {
    const user = getCurrentUser();
    if (user) {
      const nameEl = document.getElementById('dropdown-name');
      if (nameEl) nameEl.textContent = user.name || 'User';
      const roomEl = document.querySelector('.dropdown-room');
      if (roomEl) roomEl.textContent = 'Room ' + (user.roomNo || '--');
    }
  }
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initDropdownInfo);
  } else {
    initDropdownInfo();
  }
  document.addEventListener('click', function(e) {
    const dropdown = document.getElementById('profile-dropdown');
    if (!dropdown) return;
    if (dropdown.classList.contains('active') && !e.target.closest('.user-profile')) {
      dropdown.classList.remove('active');
    }
  });
})();
