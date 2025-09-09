const API_BASE_URL = 'http://localhost:8080/api';

let currentUser = null;
let users = [];
let releases = [];
let deploymentLogs = [];

document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

function initializeApp() {
    setupNavigation();
    loadUsers();
    setupEventListeners();
}

function setupNavigation() {
    const navButtons = document.querySelectorAll('.nav-btn');
    const sections = document.querySelectorAll('.section');

    navButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetSection = this.id.replace('Tab', 'Section');
            
            navButtons.forEach(btn => btn.classList.remove('active'));
            sections.forEach(section => section.classList.remove('active'));
            
            this.classList.add('active');
            document.getElementById(targetSection).classList.add('active');
            
            if (targetSection === 'releasesSection') {
                loadReleases();
                loadUsersForSelect();
            } else if (targetSection === 'logsSection') {
                loadDeploymentLogs();
            }
        });
    });
}

function setupEventListeners() {
    document.getElementById('userForm').addEventListener('submit', createUser);
    document.getElementById('releaseForm').addEventListener('submit', createRelease);
    document.getElementById('refreshLogs').addEventListener('click', loadDeploymentLogs);
    document.getElementById('environmentFilter').addEventListener('change', filterLogs);
    document.getElementById('statusFilter').addEventListener('change', filterLogs);
}

// User Management Functions
async function loadUsers() {
    try {
        const response = await fetch(`${API_BASE_URL}/users`);
        if (response.ok) {
            users = await response.json();
            displayUsers();
        } else {
            showError('Failed to load users');
        }
    } catch (error) {
        showError('Error loading users: ' + error.message);
    }
}

async function loadUsersForSelect() {
    const ownerSelect = document.getElementById('ownerId');
    ownerSelect.innerHTML = '<option value="">Select Owner</option>';
    
    users.forEach(user => {
        const option = document.createElement('option');
        option.value = user.id;
        option.textContent = `${user.fullName} (${user.username})`;
        ownerSelect.appendChild(option);
    });
}

function displayUsers() {
    const tbody = document.querySelector('#usersTable tbody');
    tbody.innerHTML = '';
    
    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.email}</td>
            <td>${user.fullName}</td>
            <td>${user.role}</td>
            <td>
                <div class="action-buttons">
                    <button class="danger" onclick="deleteUser(${user.id})">Delete</button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

async function createUser(event) {
    event.preventDefault();
    
    const userData = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        fullName: document.getElementById('fullName').value,
        role: document.getElementById('role').value
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        
        if (response.ok) {
            showSuccess('User created successfully');
            document.getElementById('userForm').reset();
            loadUsers();
        } else {
            const errorText = await response.text();
            showError(errorText);
        }
    } catch (error) {
        showError('Error creating user: ' + error.message);
    }
}

async function deleteUser(userId) {
    if (!confirm('Are you sure you want to delete this user?')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showSuccess('User deleted successfully');
            loadUsers();
        } else {
            const errorText = await response.text();
            showError(errorText);
        }
    } catch (error) {
        showError('Error deleting user: ' + error.message);
    }
}

// Release Management Functions
async function loadReleases() {
    try {
        const response = await fetch(`${API_BASE_URL}/releases`);
        if (response.ok) {
            releases = await response.json();
            displayReleases();
        } else {
            showError('Failed to load releases');
        }
    } catch (error) {
        showError('Error loading releases: ' + error.message);
    }
}

function displayReleases() {
    const tbody = document.querySelector('#releasesTable tbody');
    tbody.innerHTML = '';
    
    releases.forEach(release => {
        const row = document.createElement('tr');
        const createdDate = new Date(release.createdAt).toLocaleDateString();
        
        row.innerHTML = `
            <td>${release.versionNumber}</td>
            <td title="${release.description}">${release.description.substring(0, 50)}${release.description.length > 50 ? '...' : ''}</td>
            <td>${release.owner.fullName}</td>
            <td><span class="environment-badge env-${release.currentEnvironment.toLowerCase()}">${release.currentEnvironment}</span></td>
            <td>${createdDate}</td>
            <td>
                <div class="action-buttons">
                    ${release.currentEnvironment !== 'PROD' ? `<button class="info" onclick="promoteRelease(${release.id})">Promote</button>` : ''}
                    ${release.currentEnvironment !== 'DEV' ? `<button class="warning" onclick="rollbackRelease(${release.id})">Rollback</button>` : ''}
                    <button class="danger" onclick="deleteRelease(${release.id})">Delete</button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

async function createRelease(event) {
    event.preventDefault();
    
    const selectedOwnerId = document.getElementById('ownerId').value;
    const selectedOwner = users.find(user => user.id == selectedOwnerId);
    
    const releaseData = {
        versionNumber: document.getElementById('versionNumber').value,
        description: document.getElementById('description').value,
        owner: selectedOwner
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/releases`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(releaseData)
        });
        
        if (response.ok) {
            showSuccess('Release created successfully');
            document.getElementById('releaseForm').reset();
            loadReleases();
        } else {
            const errorText = await response.text();
            showError(errorText);
        }
    } catch (error) {
        showError('Error creating release: ' + error.message);
    }
}

async function promoteRelease(releaseId) {
    const promotedById = prompt('Enter your User ID to promote this release:');
    if (!promotedById) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/releases/${releaseId}/promote?promotedById=${promotedById}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showSuccess('Release promoted successfully');
            loadReleases();
            if (document.getElementById('logsSection').classList.contains('active')) {
                loadDeploymentLogs();
            }
        } else {
            const errorText = await response.text();
            showError(errorText);
        }
    } catch (error) {
        showError('Error promoting release: ' + error.message);
    }
}

async function rollbackRelease(releaseId) {
    const rolledBackById = prompt('Enter your User ID to rollback this release:');
    if (!rolledBackById) return;
    
    if (!confirm('Are you sure you want to rollback this release?')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/releases/${releaseId}/rollback?rolledBackById=${rolledBackById}`, {
            method: 'POST'
        });
        
        if (response.ok) {
            showSuccess('Release rolled back successfully');
            loadReleases();
            if (document.getElementById('logsSection').classList.contains('active')) {
                loadDeploymentLogs();
            }
        } else {
            const errorText = await response.text();
            showError(errorText);
        }
    } catch (error) {
        showError('Error rolling back release: ' + error.message);
    }
}

async function deleteRelease(releaseId) {
    if (!confirm('Are you sure you want to delete this release?')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/releases/${releaseId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showSuccess('Release deleted successfully');
            loadReleases();
        } else {
            const errorText = await response.text();
            showError(errorText);
        }
    } catch (error) {
        showError('Error deleting release: ' + error.message);
    }
}

// Deployment Logs Functions
async function loadDeploymentLogs() {
    try {
        const response = await fetch(`${API_BASE_URL}/deployment-logs`);
        if (response.ok) {
            deploymentLogs = await response.json();
            displayDeploymentLogs();
        } else {
            showError('Failed to load deployment logs');
        }
    } catch (error) {
        showError('Error loading deployment logs: ' + error.message);
    }
}

function displayDeploymentLogs(logsToShow = deploymentLogs) {
    const tbody = document.querySelector('#logsTable tbody');
    tbody.innerHTML = '';
    
    logsToShow.forEach(log => {
        const row = document.createElement('tr');
        const timestamp = new Date(log.deploymentTimestamp).toLocaleString();
        const status = log.success ? 'Success' : 'Failed';
        const statusClass = log.success ? 'status-success' : 'status-failed';
        
        row.innerHTML = `
            <td>${log.release.versionNumber}</td>
            <td><span class="environment-badge env-${log.environment.toLowerCase()}">${log.environment}</span></td>
            <td>${log.deployedBy.fullName}</td>
            <td>${timestamp}</td>
            <td><span class="${statusClass}">${status}</span></td>
            <td title="${log.notes || ''}">${(log.notes || '').substring(0, 50)}${(log.notes || '').length > 50 ? '...' : ''}</td>
        `;
        tbody.appendChild(row);
    });
}

function filterLogs() {
    const environmentFilter = document.getElementById('environmentFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    let filteredLogs = deploymentLogs;
    
    if (environmentFilter) {
        filteredLogs = filteredLogs.filter(log => log.environment === environmentFilter);
    }
    
    if (statusFilter !== '') {
        const isSuccess = statusFilter === 'true';
        filteredLogs = filteredLogs.filter(log => log.success === isSuccess);
    }
    
    displayDeploymentLogs(filteredLogs);
}

// Utility Functions
function showSuccess(message) {
    alert('Success: ' + message);
}

function showError(message) {
    alert('Error: ' + message);
}