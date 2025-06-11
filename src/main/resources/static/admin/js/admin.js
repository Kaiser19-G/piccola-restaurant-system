// Admin Dashboard JavaScript
class AdminDashboard {
    constructor() {
        this.initSidebar();
        this.initUserMenu();
        this.initModals();
        this.initTables();
        this.initCharts();
    }

    // Sidebar functionality
    initSidebar() {
        const sidebarToggle = document.querySelector('.sidebar-toggle');
        const sidebar = document.querySelector('.admin-sidebar');
        
        if (sidebarToggle && sidebar) {
            sidebarToggle.addEventListener('click', () => {
                sidebar.classList.toggle('open');
            });
        }

        // Set active menu item
        const currentPath = window.location.pathname;
        const menuItems = document.querySelectorAll('.admin-sidebar-menu a');
        
        menuItems.forEach(item => {
            if (item.getAttribute('href') === currentPath) {
                item.classList.add('active');
            }
        });
    }

    // User menu dropdown
    initUserMenu() {
        const userMenuBtn = document.querySelector('.admin-user-btn');
        const userMenuDropdown = document.querySelector('.admin-user-dropdown');
        
        if (userMenuBtn && userMenuDropdown) {
            userMenuBtn.addEventListener('click', () => {
                userMenuDropdown.classList.toggle('show');
            });

            // Close dropdown when clicking outside
            document.addEventListener('click', (e) => {
                if (!userMenuBtn.contains(e.target) && !userMenuDropdown.contains(e.target)) {
                    userMenuDropdown.classList.remove('show');
                }
            });
        }
    }

    // Modal functionality
    initModals() {
        const modalTriggers = document.querySelectorAll('[data-modal-target]');
        const modalCloses = document.querySelectorAll('[data-modal-close]');
        
        modalTriggers.forEach(trigger => {
            trigger.addEventListener('click', (e) => {
                e.preventDefault();
                const modalId = trigger.getAttribute('data-modal-target');
                const modal = document.getElementById(modalId);
                if (modal) {
                    modal.classList.add('show');
                    document.body.style.overflow = 'hidden';
                }
            });
        });

        modalCloses.forEach(close => {
            close.addEventListener('click', () => {
                const modal = close.closest('.modal');
                if (modal) {
                    modal.classList.remove('show');
                    document.body.style.overflow = '';
                }
            });
        });

        // Close modal on overlay click
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.classList.remove('show');
                document.body.style.overflow = '';
            }
        });
    }

    // Table functionality
    initTables() {
        // Sort functionality
        const sortableHeaders = document.querySelectorAll('.sortable');
        sortableHeaders.forEach(header => {
            header.addEventListener('click', () => {
                this.sortTable(header);
            });
        });

        // Search functionality
        const searchInputs = document.querySelectorAll('.table-search');
        searchInputs.forEach(input => {
            input.addEventListener('input', (e) => {
                this.searchTable(e.target);
            });
        });
    }

    // Chart initialization
    initCharts() {
        // Initialize Chart.js charts if present
        if (typeof Chart !== 'undefined') {
            this.initSalesChart();
            this.initOrdersChart();
        }
    }

    // Sort table columns
    sortTable(header) {
        const table = header.closest('table');
        const tbody = table.querySelector('tbody');
        const rows = Array.from(tbody.querySelectorAll('tr'));
        const columnIndex = Array.from(header.parentNode.children).indexOf(header);
        const isAscending = header.classList.contains('sort-asc');

        rows.sort((a, b) => {
            const aText = a.children[columnIndex].textContent.trim();
            const bText = b.children[columnIndex].textContent.trim();
            
            // Try to parse as numbers
            const aNum = parseFloat(aText.replace(/[^\d.-]/g, ''));
            const bNum = parseFloat(bText.replace(/[^\d.-]/g, ''));
            
            if (!isNaN(aNum) && !isNaN(bNum)) {
                return isAscending ? bNum - aNum : aNum - bNum;
            } else {
                return isAscending ? 
                    bText.localeCompare(aText) : 
                    aText.localeCompare(bText);
            }
        });

        // Clear all sort classes
        header.parentNode.querySelectorAll('th').forEach(th => {
            th.classList.remove('sort-asc', 'sort-desc');
        });

        // Add appropriate sort class
        header.classList.add(isAscending ? 'sort-desc' : 'sort-asc');

        // Rebuild table
        rows.forEach(row => tbody.appendChild(row));
    }

    // Search table
    searchTable(input) {
        const table = input.closest('.admin-card').querySelector('table');
        const tbody = table.querySelector('tbody');
        const rows = tbody.querySelectorAll('tr');
        const searchTerm = input.value.toLowerCase();

        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(searchTerm) ? '' : 'none';
        });
    }

    // Initialize sales chart
    initSalesChart() {
        const salesChartCanvas = document.getElementById('salesChart');
        if (!salesChartCanvas) return;

        new Chart(salesChartCanvas, {
            type: 'line',
            data: {
                labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
                datasets: [{
                    label: 'Ventas',
                    data: [12000, 19000, 15000, 25000, 22000, 30000],
                    borderColor: '#2563eb',
                    backgroundColor: 'rgba(37, 99, 235, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return 'S/ ' + value.toLocaleString();
                            }
                        }
                    }
                }
            }
        });
    }

    // Initialize orders chart
    initOrdersChart() {
        const ordersChartCanvas = document.getElementById('ordersChart');
        if (!ordersChartCanvas) return;

        new Chart(ordersChartCanvas, {
            type: 'doughnut',
            data: {
                labels: ['Completadas', 'En Proceso', 'Canceladas'],
                datasets: [{
                    data: [75, 20, 5],
                    backgroundColor: ['#10b981', '#f59e0b', '#ef4444']
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }
}

// Utility functions
const AdminUtils = {
    // Format currency
    formatCurrency(amount) {
        return new Intl.NumberFormat('es-PE', {
            style: 'currency',
            currency: 'PEN'
        }).format(amount);
    },

    // Format date
    formatDate(date) {
        return new Intl.DateTimeFormat('es-PE').format(new Date(date));
    },

    // Show notification
    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.classList.add('show');
        }, 100);
        
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
                document.body.removeChild(notification);
            }, 300);
        }, 3000);
    },

    // Confirm dialog
    confirm(message, callback) {
        if (window.confirm(message)) {
            callback();
        }
    },

    // AJAX helper
    async request(url, options = {}) {
        try {
            const response = await fetch(url, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    ...options.headers
                },
                ...options
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Request failed:', error);
            this.showNotification('Error en la solicitud', 'error');
            throw error;
        }
    }
};

// Order management
class OrderManager {
    constructor() {
        this.initOrderActions();
        this.refreshInterval = setInterval(() => {
            this.refreshOrders();
        }, 30000); // Refresh every 30 seconds
    }

    initOrderActions() {
        // Status update buttons
        document.addEventListener('click', async (e) => {
            if (e.target.classList.contains('update-status-btn')) {
                const orderId = e.target.dataset.orderId;
                const newStatus = e.target.dataset.status;
                await this.updateOrderStatus(orderId, newStatus);
            }
        });
    }

    async updateOrderStatus(orderId, status) {
        try {
            const response = await AdminUtils.request(`/api/admin/orders/${orderId}/status`, {
                method: 'PATCH',
                body: JSON.stringify({ status })
            });

            if (response.success) {
                AdminUtils.showNotification('Estado actualizado exitosamente', 'success');
                this.refreshOrders();
            }
        } catch (error) {
            AdminUtils.showNotification('Error al actualizar el estado', 'error');
        }
    }

    async refreshOrders() {
        try {
            const response = await AdminUtils.request('/api/admin/orders/active');
            if (response.success) {
                this.updateOrdersTable(response.data);
            }
        } catch (error) {
            console.error('Error refreshing orders:', error);
        }
    }

    updateOrdersTable(orders) {
        const tbody = document.querySelector('#ordersTable tbody');
        if (!tbody) return;

        tbody.innerHTML = orders.map(order => `
            <tr>
                <td>${order.numeroOrden}</td>
                <td>${order.nombreCliente}</td>
                <td>${AdminUtils.formatCurrency(order.total)}</td>
                <td>
                    <span class="badge badge-${this.getStatusBadgeClass(order.estado)}">
                        ${order.estado}
                    </span>
                </td>
                <td>
                    ${this.getStatusButtons(order.id, order.estado)}
                </td>
            </tr>
        `).join('');
    }

    getStatusBadgeClass(status) {
        const classes = {
            'PENDIENTE': 'warning',
            'CONFIRMADA': 'info',
            'EN_PREPARACION': 'primary',
            'LISTA': 'success',
            'EN_CAMINO': 'info',
            'ENTREGADA': 'success',
            'CANCELADA': 'danger'
        };
        return classes[status] || 'secondary';
    }

    getStatusButtons(orderId, currentStatus) {
        const transitions = {
            'PENDIENTE': ['CONFIRMADA', 'CANCELADA'],
            'CONFIRMADA': ['EN_PREPARACION', 'CANCELADA'],
            'EN_PREPARACION': ['LISTA'],
            'LISTA': ['EN_CAMINO', 'ENTREGADA'],
            'EN_CAMINO': ['ENTREGADA']
        };

        const availableTransitions = transitions[currentStatus] || [];
        
        return availableTransitions.map(status => `
            <button class="admin-btn admin-btn-sm update-status-btn" 
                    data-order-id="${orderId}" 
                    data-status="${status}">
                ${status}
            </button>
        `).join(' ');
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new AdminDashboard();
    
    // Initialize order manager on order pages
    if (document.querySelector('#ordersTable')) {
        new OrderManager();
    }
});
