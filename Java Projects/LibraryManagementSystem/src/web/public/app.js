/* ==========================================================================
   LIBRARY MANAGEMENT SYSTEM - INTERACTIVE APPLICATION CONTROLLER
   ========================================================================== */

const API_BASE = '/api';

// Application State
const state = {
  books: [],
  categories: [],
  students: [],
  librarians: [],
  issues: [],
  fines: [],
  reservations: [],
  dashboardStats: {},
  auditLogs: [],
  activeTab: 'dashboard',
  bookViewMode: 'grid',
  activeIssueFilter: 'ALL',
  activeFineFilter: 'ALL',
  activeReport: 'view-availability'
};

// Initialize App on DOM Load
document.addEventListener('DOMContentLoaded', () => {
  initNavigation();
  initTheme();
  initSearchAndFilters();
  initFormListeners();
  initReports();
  loadAllData();
});

// Load All Data from Java REST API
async function loadAllData() {
  await Promise.all([
    fetchDashboardStats(),
    fetchCategories(),
    fetchBooks(),
    fetchStudents(),
    fetchLibrarians(),
    fetchIssues(),
    fetchFines(),
    fetchReservations(),
    fetchAuditLogs()
  ]);
}

// Navigation & Tab Switching
function initNavigation() {
  const navItems = document.querySelectorAll('.sidebar-nav .nav-item');
  navItems.forEach(item => {
    item.addEventListener('click', (e) => {
      e.preventDefault();
      const targetTab = item.getAttribute('data-tab');
      switchTab(targetTab);
    });
  });

  // Toggle Sidebar for mobile
  const toggleBtn = document.getElementById('toggle-sidebar');
  const sidebar = document.querySelector('.sidebar');
  if (toggleBtn && sidebar) {
    toggleBtn.addEventListener('click', () => {
      sidebar.classList.toggle('open');
    });
  }
}

function switchTab(tabId) {
  state.activeTab = tabId;
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  const activeNavItem = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
  if (activeNavItem) activeNavItem.classList.add('active');

  document.querySelectorAll('.tab-view').forEach(el => el.classList.remove('active'));
  const activeView = document.getElementById(tabId);
  if (activeView) activeView.classList.add('active');

  // Trigger view refresh
  if (tabId === 'dashboard') refreshDashboardView();
  else if (tabId === 'books') renderBooks();
  else if (tabId === 'students') renderStudents();
  else if (tabId === 'issues') renderIssues();
  else if (tabId === 'fines') renderFines();
  else if (tabId === 'reservations') renderReservations();
  else if (tabId === 'reports') loadReportData(state.activeReport);
}

// Theme Switcher (Dark / Light)
function initTheme() {
  const themeBtn = document.getElementById('theme-btn');
  const themeIcon = document.getElementById('theme-icon');
  const html = document.documentElement;

  themeBtn.addEventListener('click', () => {
    const currentTheme = html.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    html.setAttribute('data-theme', newTheme);
    themeIcon.className = newTheme === 'dark' ? 'ri-moon-line' : 'ri-sun-line';
    showToast(`Switched to ${newTheme} mode`, 'info');
  });
}

// Global & Tab Specific Search/Filters
function initSearchAndFilters() {
  const globalSearch = document.getElementById('global-search-input');
  if (globalSearch) {
    globalSearch.addEventListener('input', (e) => {
      const q = e.target.value.toLowerCase().trim();
      if (q.length > 0) {
        if (state.activeTab !== 'books') switchTab('books');
        filterBooks(q);
      } else {
        renderBooks();
      }
    });
  }

  // Book search & Category filter
  const bookSearch = document.getElementById('book-search-input');
  if (bookSearch) {
    bookSearch.addEventListener('input', (e) => filterBooks(e.target.value));
  }

  const categoryFilter = document.getElementById('book-category-filter');
  if (categoryFilter) {
    categoryFilter.addEventListener('change', () => filterBooks(bookSearch ? bookSearch.value : ''));
  }

  // Book View Toggle (Grid / Table)
  const gridBtn = document.getElementById('view-grid-btn');
  const tableBtn = document.getElementById('view-table-btn');
  const gridWrapper = document.getElementById('books-grid');
  const tableWrapper = document.getElementById('books-table-wrapper');

  gridBtn.addEventListener('click', () => {
    state.bookViewMode = 'grid';
    gridBtn.classList.add('active');
    tableBtn.classList.remove('active');
    gridWrapper.classList.remove('hidden');
    tableWrapper.classList.add('hidden');
  });

  tableBtn.addEventListener('click', () => {
    state.bookViewMode = 'table';
    tableBtn.classList.add('active');
    gridBtn.classList.remove('active');
    tableWrapper.classList.remove('hidden');
    gridWrapper.classList.add('hidden');
  });

  // Student Search
  const studentSearch = document.getElementById('student-search-input');
  if (studentSearch) {
    studentSearch.addEventListener('input', (e) => filterStudents(e.target.value));
  }

  // Issue Search & Pills
  const issueSearch = document.getElementById('issue-search-input');
  if (issueSearch) {
    issueSearch.addEventListener('input', () => filterIssues());
  }

  document.querySelectorAll('[data-issue-filter]').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('[data-issue-filter]').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      state.activeIssueFilter = btn.getAttribute('data-issue-filter');
      filterIssues();
    });
  });

  // Fine Pills
  document.querySelectorAll('[data-fine-filter]').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('[data-fine-filter]').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      state.activeFineFilter = btn.getAttribute('data-fine-filter');
      renderFines();
    });
  });
}

/* ==========================================================================
   API FETCHERS
   ========================================================================== */

async function fetchDashboardStats() {
  try {
    const res = await fetch(`${API_BASE}/dashboard`);
    if (res.ok) {
      state.dashboardStats = await res.json();
      refreshDashboardMetrics();
    }
  } catch (e) {
    console.error('Failed to fetch dashboard stats', e);
  }
}

async function fetchCategories() {
  try {
    const res = await fetch(`${API_BASE}/categories`);
    if (res.ok) {
      state.categories = await res.json();
      populateCategoryDropdowns();
    }
  } catch (e) {
    console.error('Failed to fetch categories', e);
  }
}

async function fetchBooks() {
  try {
    const res = await fetch(`${API_BASE}/books`);
    if (res.ok) {
      state.books = await res.json();
      renderBooks();
      populateBookDropdowns();
    }
  } catch (e) {
    console.error('Failed to fetch books', e);
  }
}

async function fetchStudents() {
  try {
    const res = await fetch(`${API_BASE}/students`);
    if (res.ok) {
      state.students = await res.json();
      renderStudents();
      populateStudentDropdowns();
    }
  } catch (e) {
    console.error('Failed to fetch students', e);
  }
}

async function fetchLibrarians() {
  try {
    const res = await fetch(`${API_BASE}/librarians`);
    if (res.ok) {
      state.librarians = await res.json();
      populateLibrarianDropdowns();
    }
  } catch (e) {
    console.error('Failed to fetch librarians', e);
  }
}

async function fetchIssues() {
  try {
    const res = await fetch(`${API_BASE}/issues`);
    if (res.ok) {
      state.issues = await res.json();
      renderIssues();
      populateIssueDropdowns();
    }
  } catch (e) {
    console.error('Failed to fetch issues', e);
  }
}

async function fetchFines() {
  try {
    const res = await fetch(`${API_BASE}/fines`);
    if (res.ok) {
      state.fines = await res.json();
      renderFines();
    }
  } catch (e) {
    console.error('Failed to fetch fines', e);
  }
}

async function fetchReservations() {
  try {
    const res = await fetch(`${API_BASE}/reservations`);
    if (res.ok) {
      state.reservations = await res.json();
      renderReservations();
    }
  } catch (e) {
    console.error('Failed to fetch reservations', e);
  }
}

async function fetchAuditLogs() {
  try {
    const res = await fetch(`${API_BASE}/audit-logs`);
    if (res.ok) {
      state.auditLogs = await res.json();
      renderAuditLogs();
    }
  } catch (e) {
    console.error('Failed to fetch audit logs', e);
  }
}

/* ==========================================================================
   RENDERERS & POPULATORS
   ========================================================================== */

function refreshDashboardMetrics() {
  const stats = state.dashboardStats;
  document.getElementById('stat-total-books').textContent = stats.totalBooks || 0;
  document.getElementById('stat-total-copies').textContent = `${stats.totalCopies || 0} total copies`;
  document.getElementById('stat-available-copies').textContent = stats.availableCopies || 0;
  document.getElementById('stat-total-students').textContent = stats.totalStudents || 0;
  document.getElementById('stat-active-issues').textContent = stats.activeIssued || 0;
  document.getElementById('stat-pending-fine').textContent = `$${(stats.pendingFine || 0).toFixed(2)}`;
  document.getElementById('stat-paid-fine').textContent = `$${(stats.paidFine || 0).toFixed(2)} collected`;
  document.getElementById('stat-active-reservations').textContent = stats.activeReservations || 0;
}

function refreshDashboardView() {
  fetchDashboardStats();
  renderCategoryDistribution();
  fetchAuditLogs();
}

async function renderCategoryDistribution() {
  const container = document.getElementById('dashboard-category-list');
  if (!container) return;

  try {
    const res = await fetch(`${API_BASE}/reports/category-distribution`);
    if (res.ok) {
      const data = await res.json();
      container.innerHTML = data.map(item => `
        <div class="category-item">
          <div>
            <div class="category-name">${escapeHtml(item.categoryName)}</div>
            <span style="font-size: 0.75rem; color: var(--color-text-dim);">${item.totalCopies} total copies</span>
          </div>
          <span class="category-count">${item.totalBooks} titles</span>
        </div>
      `).join('');
    }
  } catch (e) {
    console.error('Category distribution error', e);
  }
}

function renderAuditLogs() {
  const container = document.getElementById('dashboard-audit-stream');
  if (!container) return;

  if (state.auditLogs.length === 0) {
    container.innerHTML = `<div class="audit-item" style="color: var(--color-text-muted);">No book updates logged yet.</div>`;
    return;
  }

  container.innerHTML = state.auditLogs.map(log => `
    <div class="audit-item">
      <div class="audit-meta">
        <span>Book ID: #${log.bookId} (${escapeHtml(log.title)})</span>
        <span>${log.updatedOn}</span>
      </div>
      <div>Copies changed: <strong style="color: var(--color-danger);">${log.oldCopies}</strong> &rarr; <strong style="color: var(--color-success);">${log.newCopies}</strong></div>
    </div>
  `).join('');
}

function renderBooks(filteredList = null) {
  const list = filteredList || state.books;
  const gridContainer = document.getElementById('books-grid');
  const tableContainer = document.getElementById('books-table-body');

  // Populate Grid View
  gridContainer.innerHTML = list.map(b => {
    const category = state.categories.find(c => c.categoryId === b.categoryId);
    const categoryName = category ? category.categoryName : 'General';
    const isAvailable = b.availableCopies > 0;
    const stockClass = b.availableCopies > 3 ? 'available' : (b.availableCopies > 0 ? 'low' : 'out');
    const stockLabel = isAvailable ? `${b.availableCopies} available` : 'Out of Stock';

    return `
      <div class="book-card">
        <div>
          <div class="book-card-header">
            <span class="book-tag">${escapeHtml(categoryName)}</span>
            <span class="stock-badge ${stockClass}">
              <i class="ri-checkbox-circle-fill"></i> ${stockLabel}
            </span>
          </div>
          <h3>${escapeHtml(b.title)}</h3>
          <div class="book-author">by ${escapeHtml(b.author)}</div>
          <div class="book-details-list">
            <div><strong>ISBN:</strong> ${escapeHtml(b.isbn)}</div>
            <div><strong>Publisher:</strong> ${escapeHtml(b.publisher || 'N/A')} (${b.publishYear || 'N/A'})</div>
            <div><strong>Shelf:</strong> ${escapeHtml(b.shelfNo || 'N/A')} | <strong>Total Copies:</strong> ${b.totalCopies}</div>
          </div>
        </div>
        <div class="book-card-footer">
          <div style="display: flex; gap: 0.5rem;">
            <button class="btn btn-sm btn-secondary" onclick="editBook(${b.bookId})"><i class="ri-edit-line"></i> Edit</button>
            <button class="btn btn-sm btn-danger" onclick="deleteBook(${b.bookId})"><i class="ri-delete-bin-line"></i></button>
          </div>
          <button class="btn btn-sm btn-primary" onclick="quickIssueBook(${b.bookId})" ${!isAvailable ? 'disabled' : ''}>
            <i class="ri-external-link-line"></i> Issue
          </button>
        </div>
      </div>
    `;
  }).join('');

  // Populate Table View
  tableContainer.innerHTML = list.map(b => {
    const category = state.categories.find(c => c.categoryId === b.categoryId);
    const categoryName = category ? category.categoryName : 'General';
    return `
      <tr>
        <td>#${b.bookId}</td>
        <td><code>${escapeHtml(b.isbn)}</code></td>
        <td><strong>${escapeHtml(b.title)}</strong></td>
        <td>${escapeHtml(b.author)}</td>
        <td>${escapeHtml(b.publisher || 'N/A')} (${b.publishYear || '-'})</td>
        <td><span class="badge badge-info">${escapeHtml(categoryName)}</span></td>
        <td>${escapeHtml(b.shelfNo || '-')}</td>
        <td><strong>${b.availableCopies}</strong> / ${b.totalCopies}</td>
        <td>
          <div style="display: flex; gap: 0.4rem;">
            <button class="btn btn-sm btn-secondary" onclick="editBook(${b.bookId})"><i class="ri-edit-line"></i></button>
            <button class="btn btn-sm btn-danger" onclick="deleteBook(${b.bookId})"><i class="ri-delete-bin-line"></i></button>
          </div>
        </td>
      </tr>
    `;
  }).join('');
}

function filterBooks(query = '') {
  const catFilter = document.getElementById('book-category-filter').value;
  const q = query.toLowerCase().trim();

  const filtered = state.books.filter(b => {
    const matchesQuery = !q || b.title.toLowerCase().includes(q) || b.author.toLowerCase().includes(q) || b.isbn.toLowerCase().includes(q);
    const matchesCat = !catFilter || b.categoryId == catFilter;
    return matchesQuery && matchesCat;
  });

  renderBooks(filtered);
}

function renderStudents(filteredList = null) {
  const list = filteredList || state.students;
  const tableContainer = document.getElementById('students-table-body');

  tableContainer.innerHTML = list.map(s => `
    <tr>
      <td>#${s.studentId}</td>
      <td><code>${escapeHtml(s.rollNo)}</code></td>
      <td><strong>${escapeHtml(s.firstName)} ${escapeHtml(s.lastName)}</strong></td>
      <td>${escapeHtml(s.gender || 'N/A')}</td>
      <td><span class="badge badge-info">${escapeHtml(s.department || 'N/A')}</span></td>
      <td>Sem ${s.semester || '-'}</td>
      <td>
        <div><i class="ri-phone-line"></i> ${escapeHtml(s.phone || 'N/A')}</div>
        <div style="font-size: 0.75rem; color: var(--color-text-muted);"><i class="ri-mail-line"></i> ${escapeHtml(s.email || 'N/A')}</div>
      </td>
      <td>${escapeHtml(s.address || 'N/A')}</td>
      <td>${s.joinDate || 'N/A'}</td>
      <td>
        <div style="display: flex; gap: 0.4rem;">
          <button class="btn btn-sm btn-secondary" onclick="editStudent(${s.studentId})"><i class="ri-edit-line"></i></button>
          <button class="btn btn-sm btn-danger" onclick="deleteStudent(${s.studentId})"><i class="ri-delete-bin-line"></i></button>
          <button class="btn btn-sm btn-primary" onclick="quickIssueToStudent(${s.studentId})"><i class="ri-book-open-line"></i> Issue</button>
        </div>
      </td>
    </tr>
  `).join('');
}

function filterStudents(query = '') {
  const q = query.toLowerCase().trim();
  const filtered = state.students.filter(s =>
    s.firstName.toLowerCase().includes(q) ||
    s.lastName.toLowerCase().includes(q) ||
    s.rollNo.toLowerCase().includes(q) ||
    (s.department && s.department.toLowerCase().includes(q))
  );
  renderStudents(filtered);
}

function renderIssues() {
  const tableContainer = document.getElementById('issues-table-body');
  filterIssues();
}

function filterIssues() {
  const q = (document.getElementById('issue-search-input')?.value || '').toLowerCase().trim();
  const filter = state.activeIssueFilter;

  const filtered = state.issues.filter(issue => {
    const matchesFilter = filter === 'ALL' || issue.status === filter;
    const student = state.students.find(s => s.studentId === issue.studentId);
    const book = state.books.find(b => b.bookId === issue.bookId);
    const studentName = student ? `${student.firstName} ${student.lastName}` : `Student #${issue.studentId}`;
    const bookTitle = book ? book.title : `Book #${issue.bookId}`;

    const matchesQuery = !q || studentName.toLowerCase().includes(q) || bookTitle.toLowerCase().includes(q) || issue.status.toLowerCase().includes(q);
    return matchesFilter && matchesQuery;
  });

  const tableContainer = document.getElementById('issues-table-body');
  tableContainer.innerHTML = filtered.map(issue => {
    const student = state.students.find(s => s.studentId === issue.studentId);
    const book = state.books.find(b => b.bookId === issue.bookId);
    const librarian = state.librarians.find(l => l.librarianId === issue.librarianId);

    const studentStr = student ? `<strong>${escapeHtml(student.firstName)} ${escapeHtml(student.lastName)}</strong> (${escapeHtml(student.rollNo)})` : `#${issue.studentId}`;
    const bookStr = book ? escapeHtml(book.title) : `#${issue.bookId}`;
    const libStr = librarian ? `${escapeHtml(librarian.firstName)} ${escapeHtml(librarian.lastName)}` : 'System';

    const isReturned = issue.status === 'Returned';
    const statusBadge = isReturned ?
      `<span class="badge badge-success"><i class="ri-checkbox-circle-line"></i> Returned</span>` :
      `<span class="badge badge-warning"><i class="ri-time-line"></i> Issued</span>`;

    return `
      <tr>
        <td>#${issue.issueId}</td>
        <td>${studentStr}</td>
        <td><strong>${bookStr}</strong></td>
        <td>${libStr}</td>
        <td>${issue.issueDate}</td>
        <td>${issue.dueDate}</td>
        <td>${statusBadge}</td>
        <td>
          ${!isReturned ? `<button class="btn btn-sm btn-primary" onclick="openReturnModal(${issue.issueId})"><i class="ri-check-double-line"></i> Return</button>` : '-'}
        </td>
      </tr>
    `;
  }).join('');
}

function renderFines() {
  const tableContainer = document.getElementById('fines-table-body');
  const filter = state.activeFineFilter;

  const filtered = state.fines.filter(f => filter === 'ALL' || f.paidStatus === filter);

  tableContainer.innerHTML = filtered.map(f => {
    const issue = state.issues.find(i => i.issueId === f.issueId);
    let studentStr = 'Unknown';
    if (issue) {
      const student = state.students.find(s => s.studentId === issue.studentId);
      if (student) studentStr = `${student.firstName} ${student.lastName} (${student.rollNo})`;
    }

    const isPaid = f.paidStatus === 'Paid';
    const badge = isPaid ?
      `<span class="badge badge-success">Paid</span>` :
      `<span class="badge badge-danger">Pending</span>`;

    return `
      <tr>
        <td>#${f.fineId}</td>
        <td>#${f.issueId}</td>
        <td><strong>${escapeHtml(studentStr)}</strong></td>
        <td><strong style="color: var(--color-danger);">$${f.fineAmount.toFixed(2)}</strong></td>
        <td>${badge}</td>
        <td>
          ${!isPaid ? `<button class="btn btn-sm btn-primary" onclick="payFine(${f.fineId})"><i class="ri-money-dollar-circle-line"></i> Mark as Paid</button>` : '-'}
        </td>
      </tr>
    `;
  }).join('');
}

function renderReservations() {
  const tableContainer = document.getElementById('reservations-table-body');

  tableContainer.innerHTML = state.reservations.map(r => {
    const student = state.students.find(s => s.studentId === r.studentId);
    const book = state.books.find(b => b.bookId === r.bookId);

    const studentStr = student ? `${student.firstName} ${student.lastName} (${student.rollNo})` : `#${r.studentId}`;
    const bookStr = book ? book.title : `#${r.bookId}`;

    let badgeClass = 'badge-info';
    if (r.status === 'Completed') badgeClass = 'badge-success';
    if (r.status === 'Cancelled') badgeClass = 'badge-danger';

    return `
      <tr>
        <td>#${r.reservationId}</td>
        <td><strong>${escapeHtml(studentStr)}</strong></td>
        <td>${escapeHtml(bookStr)}</td>
        <td>${r.reservationDate || '-'}</td>
        <td><span class="badge ${badgeClass}">${escapeHtml(r.status)}</span></td>
        <td>
          ${r.status === 'Reserved' ? `
            <button class="btn btn-sm btn-primary" onclick="updateReservationStatus(${r.reservationId}, 'Completed')"><i class="ri-check-line"></i> Complete</button>
            <button class="btn btn-sm btn-danger" onclick="updateReservationStatus(${r.reservationId}, 'Cancelled')"><i class="ri-close-line"></i> Cancel</button>
          ` : '-'}
        </td>
      </tr>
    `;
  }).join('');
}

// Populate Dropdown Selects for Modals
function populateCategoryDropdowns() {
  const catFilter = document.getElementById('book-category-filter');
  const catForm = document.getElementById('form-category');
  if (!catFilter || !catForm) return;

  const options = state.categories.map(c => `<option value="${c.categoryId}">${escapeHtml(c.categoryName)}</option>`).join('');

  catFilter.innerHTML = `<option value="">All Categories</option>` + options;
  catForm.innerHTML = `<option value="">-- Select Category --</option>` + options;
}

function populateBookDropdowns() {
  const issueBook = document.getElementById('form-issue-book');
  const reserveBook = document.getElementById('form-reserve-book');
  if (!issueBook || !reserveBook) return;

  const options = state.books.map(b => `<option value="${b.bookId}">${escapeHtml(b.title)} (${b.availableCopies} available)</option>`).join('');
  issueBook.innerHTML = `<option value="">-- Select Book --</option>` + options;
  reserveBook.innerHTML = `<option value="">-- Select Book --</option>` + options;
}

function populateStudentDropdowns() {
  const issueStudent = document.getElementById('form-issue-student');
  const reserveStudent = document.getElementById('form-reserve-student');
  if (!issueStudent || !reserveStudent) return;

  const options = state.students.map(s => `<option value="${s.studentId}">${escapeHtml(s.firstName)} ${escapeHtml(s.lastName)} (${s.rollNo})</option>`).join('');
  issueStudent.innerHTML = `<option value="">-- Select Student --</option>` + options;
  reserveStudent.innerHTML = `<option value="">-- Select Student --</option>` + options;
}

function populateLibrarianDropdowns() {
  const issueLib = document.getElementById('form-issue-librarian');
  if (!issueLib) return;

  const options = state.librarians.map(l => `<option value="${l.librarianId}">${escapeHtml(l.firstName)} ${escapeHtml(l.lastName)}</option>`).join('');
  issueLib.innerHTML = `<option value="">-- Select Librarian --</option>` + options;
}

function populateIssueDropdowns() {
  const fineIssue = document.getElementById('form-fine-issue');
  if (!fineIssue) return;

  const activeIssues = state.issues.filter(i => i.status === 'Issued');
  fineIssue.innerHTML = `<option value="">-- Select Active Issue --</option>` + activeIssues.map(i => {
    const student = state.students.find(s => s.studentId === i.studentId);
    const book = state.books.find(b => b.bookId === i.bookId);
    const label = `#${i.issueId} - ${student ? student.firstName : 'Student'} (${book ? book.title : 'Book'})`;
    return `<option value="${i.issueId}">${escapeHtml(label)}</option>`;
  }).join('');
}

/* ==========================================================================
   SQL REPORTS & VIEWS CONTROLLER
   ========================================================================== */

function initReports() {
  const reportBtns = document.querySelectorAll('[data-report]');
  reportBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      reportBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      const r = btn.getAttribute('data-report');
      state.activeReport = r;
      loadReportData(r);
    });
  });
}

async function loadReportData(reportType) {
  const head = document.getElementById('report-table-head');
  const body = document.getElementById('report-table-body');
  if (!head || !body) return;

  let endpoint = '';
  if (reportType === 'view-availability') endpoint = '/reports/book-availability';
  else if (reportType === 'view-student-issues') endpoint = '/reports/student-issue-details';
  else if (reportType === 'view-fines') endpoint = '/reports/fine-report';
  else if (reportType === 'audit-log') endpoint = '/audit-logs';

  try {
    const res = await fetch(`${API_BASE}${endpoint}`);
    if (res.ok) {
      const data = await res.json();
      if (data.length === 0) {
        head.innerHTML = '';
        body.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 2rem;">No records found for this report.</td></tr>`;
        return;
      }

      // Build Headers
      const keys = Object.keys(data[0]);
      head.innerHTML = `<tr>${keys.map(k => `<th>${camelToTitle(k)}</th>`).join('')}</tr>`;

      // Build Rows
      body.innerHTML = data.map(row => `
        <tr>
          ${keys.map(k => `<td>${escapeHtml(String(row[k] ?? ''))}</td>`).join('')}
        </tr>
      `).join('');
    }
  } catch (e) {
    console.error('Report error', e);
  }
}

/* ==========================================================================
   MODALS & FORM SUBMISSIONS
   ========================================================================== */

function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.add('active');

  // Set default dates if needed
  const today = new Date().toISOString().split('T')[0];
  const nextTwoWeeks = new Date(Date.now() + 14 * 86400000).toISOString().split('T')[0];

  if (modalId === 'issue-book-modal') {
    document.getElementById('form-issue-date').value = today;
    document.getElementById('form-due-date').value = nextTwoWeeks;
  }
  if (modalId === 'return-book-modal') {
    document.getElementById('form-return-date').value = today;
  }
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.remove('active');
}

function initFormListeners() {
  // Book Form
  const bookForm = document.getElementById('book-form');
  bookForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const bookId = document.getElementById('form-book-id').value;
    const payload = {
      bookId,
      isbn: document.getElementById('form-isbn').value,
      title: document.getElementById('form-title').value,
      author: document.getElementById('form-author').value,
      publisher: document.getElementById('form-publisher').value,
      edition: document.getElementById('form-edition').value,
      publishYear: document.getElementById('form-publish-year').value,
      categoryId: document.getElementById('form-category').value,
      totalCopies: document.getElementById('form-total-copies').value,
      availableCopies: document.getElementById('form-available-copies').value,
      shelfNo: document.getElementById('form-shelf').value
    };

    const method = bookId ? 'PUT' : 'POST';
    const res = await fetch(`${API_BASE}/books`, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      showToast(bookId ? 'Book updated successfully' : 'Book added to catalog', 'success');
      closeModal('add-book-modal');
      bookForm.reset();
      document.getElementById('form-book-id').value = '';
      await fetchBooks();
      await fetchDashboardStats();
    }
  });

  // Category Form
  const categoryForm = document.getElementById('category-form');
  categoryForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
      categoryName: document.getElementById('form-category-name').value,
      description: document.getElementById('form-category-desc').value
    };
    const res = await fetch(`${API_BASE}/categories`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      showToast('New Category Created', 'success');
      closeModal('add-category-modal');
      categoryForm.reset();
      await fetchCategories();
    }
  });

  // Student Form
  const studentForm = document.getElementById('student-form');
  studentForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const studentId = document.getElementById('form-student-id').value;
    const payload = {
      rollNo: document.getElementById('form-roll-no').value,
      firstName: document.getElementById('form-first-name').value,
      lastName: document.getElementById('form-last-name').value,
      gender: document.getElementById('form-gender').value,
      department: document.getElementById('form-department').value,
      semester: document.getElementById('form-semester').value,
      phone: document.getElementById('form-student-phone').value,
      email: document.getElementById('form-student-email').value,
      address: document.getElementById('form-student-address').value
    };
    if (studentId) payload.studentId = studentId;

    const method = studentId ? 'PUT' : 'POST';
    const res = await fetch(`${API_BASE}/students`, {
      method: method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      showToast(studentId ? 'Student Updated Successfully' : 'Student Registered Successfully', 'success');
      closeModal('add-student-modal');
      studentForm.reset();
      document.getElementById('form-student-id').value = '';
      await fetchStudents();
      await fetchDashboardStats();
    }
  });

  // Issue Form
  const issueForm = document.getElementById('issue-form');
  issueForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
      studentId: document.getElementById('form-issue-student').value,
      bookId: document.getElementById('form-issue-book').value,
      librarianId: document.getElementById('form-issue-librarian').value,
      issueDate: document.getElementById('form-issue-date').value,
      dueDate: document.getElementById('form-due-date').value
    };

    try {
      const res = await fetch(`${API_BASE}/issues`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        showToast('Book Issued Successfully', 'success');
        closeModal('issue-book-modal');
        issueForm.reset();
        await fetchIssues();
        await fetchBooks();
        await fetchDashboardStats();
      } else {
        const err = await res.json();
        showToast(err.error || 'Failed to issue book', 'danger');
      }
    } catch (e) {
      showToast('Issue Transaction Failed', 'danger');
    }
  });

  // Return Form
  const returnForm = document.getElementById('return-form');
  returnForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
      issueId: document.getElementById('form-return-issue-id').value,
      returnDate: document.getElementById('form-return-date').value,
      conditionOfBook: document.getElementById('form-return-condition').value
    };
    const res = await fetch(`${API_BASE}/issues/return`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      showToast('Book Returned & Copies Restored', 'success');
      closeModal('return-book-modal');
      await fetchIssues();
      await fetchBooks();
      await fetchDashboardStats();
    }
  });

  // Fine Form
  const fineForm = document.getElementById('fine-form');
  fineForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
      issueId: document.getElementById('form-fine-issue').value,
      fineAmount: document.getElementById('form-fine-amount').value,
      paidStatus: 'Pending'
    };
    const res = await fetch(`${API_BASE}/fines`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      showToast('Fine Record Logged', 'success');
      closeModal('add-fine-modal');
      fineForm.reset();
      await fetchFines();
      await fetchDashboardStats();
    }
  });

  // Reserve Form
  const reserveForm = document.getElementById('reserve-form');
  reserveForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
      studentId: document.getElementById('form-reserve-student').value,
      bookId: document.getElementById('form-reserve-book').value,
      status: 'Reserved'
    };
    const res = await fetch(`${API_BASE}/reservations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      showToast('Book Reservation Recorded', 'success');
      closeModal('reserve-book-modal');
      reserveForm.reset();
      await fetchReservations();
      await fetchDashboardStats();
    }
  });
}

/* ==========================================================================
   ACTION HANDLERS & HELPERS
   ========================================================================== */

function editBook(bookId) {
  const book = state.books.find(b => b.bookId === bookId);
  if (!book) return;

  document.getElementById('book-modal-title').textContent = 'Edit Book Details';
  document.getElementById('form-book-id').value = book.bookId;
  document.getElementById('form-isbn').value = book.isbn;
  document.getElementById('form-title').value = book.title;
  document.getElementById('form-author').value = book.author;
  document.getElementById('form-publisher').value = book.publisher || '';
  document.getElementById('form-edition').value = book.edition || '';
  document.getElementById('form-publish-year').value = book.publishYear || '';
  document.getElementById('form-category').value = book.categoryId || '';
  document.getElementById('form-total-copies').value = book.totalCopies;
  document.getElementById('form-available-copies').value = book.availableCopies;
  document.getElementById('form-shelf').value = book.shelfNo || '';

  openModal('add-book-modal');
}

async function deleteBook(bookId) {
  if (!confirm('Are you sure you want to delete this book?')) return;

  const res = await fetch(`${API_BASE}/books/${bookId}`, { method: 'DELETE' });
  if (res.ok) {
    showToast('Book Deleted', 'success');
    await fetchBooks();
    await fetchDashboardStats();
  }
}

function editStudent(studentId) {
  const student = state.students.find(s => s.studentId === studentId);
  if (!student) return;

  document.getElementById('student-modal-title').textContent = 'Edit Student Details';
  document.getElementById('form-student-id').value = student.studentId;
  document.getElementById('form-roll-no').value = student.rollNo;
  document.getElementById('form-first-name').value = student.firstName;
  document.getElementById('form-last-name').value = student.lastName;
  document.getElementById('form-gender').value = student.gender || 'Male';
  document.getElementById('form-department').value = student.department || '';
  document.getElementById('form-semester').value = student.semester || 1;
  document.getElementById('form-student-phone').value = student.phone || '';
  document.getElementById('form-student-email').value = student.email || '';
  document.getElementById('form-student-address').value = student.address || '';

  openModal('add-student-modal');
}

async function deleteStudent(studentId) {
  if (!confirm('Are you sure you want to delete this student?')) return;

  const res = await fetch(`${API_BASE}/students/${studentId}`, { method: 'DELETE' });
  if (res.ok) {
    showToast('Student Deleted', 'success');
    await fetchStudents();
    await fetchDashboardStats();
  } else {
    showToast('Failed to delete student. They might have active issues.', 'danger');
  }
}

function quickIssueBook(bookId) {
  openModal('issue-book-modal');
  document.getElementById('form-issue-book').value = bookId;
}

function quickIssueToStudent(studentId) {
  switchTab('issues');
  openModal('issue-book-modal');
  document.getElementById('form-issue-student').value = studentId;
}

function openReturnModal(issueId) {
  document.getElementById('form-return-issue-id').value = issueId;
  document.getElementById('form-return-issue-display').value = `Issue Record #${issueId}`;
  openModal('return-book-modal');
}

async function payFine(fineId) {
  const res = await fetch(`${API_BASE}/fines/${fineId}/pay`, { method: 'POST' });
  if (res.ok) {
    showToast('Fine marked as Paid', 'success');
    await fetchFines();
    await fetchDashboardStats();
  }
}

async function updateReservationStatus(reservationId, status) {
  const res = await fetch(`${API_BASE}/reservations/${reservationId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status })
  });
  if (res.ok) {
    showToast(`Reservation marked as ${status}`, 'success');
    await fetchReservations();
    await fetchDashboardStats();
  }
}

function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <i class="${type === 'success' ? 'ri-checkbox-circle-fill' : 'ri-information-fill'}"></i>
    <span>${escapeHtml(message)}</span>
  `;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    setTimeout(() => toast.remove(), 300);
  }, 3000);
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function camelToTitle(str) {
  return str
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, s => s.toUpperCase());
}
