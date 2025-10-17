// Main JavaScript file - imports all functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('EduFlow CMS initialized');

    // Initialize all components
    initSearch();
    initFilters();
    initCustomSelects();

    // Add global CSS for animations
    addGlobalStyles();
});

function initSearch() {
    // Search functionality is handled in search.js
    // This function can be used for additional search-related initialization
}

function initFilters() {
    // Filters functionality is handled in filters.js
    // This function can be used for additional filter-related initialization
}

function initCustomSelects() {
    // Custom select functionality is handled in custom-select.js
    // This function can be used for additional select-related initialization
}

function addGlobalStyles() {
    const globalStyles = `
        .animate-spin {
            animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
        
        .slide-in {
            animation: slideIn 0.6s ease;
        }
        
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        /* Smooth transitions for all interactive elements */
        .dark-button, .dark-filter-chip, .dark-select-compact, .dark-search-input {
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }
    `;

    const styleSheet = document.createElement('style');
    styleSheet.textContent = globalStyles;
    document.head.appendChild(styleSheet);
}

// Utility functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function throttle(func, limit) {
    let inThrottle;
    return function() {
        const args = arguments;
        const context = this;
        if (!inThrottle) {
            func.apply(context, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    }
}