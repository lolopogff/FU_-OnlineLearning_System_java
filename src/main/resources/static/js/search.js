// Search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.querySelector('.dark-search-input');
    const searchClear = document.querySelector('.dark-search-clear');
    const searchForm = document.querySelector('.dark-search-form');
    const searchSubmit = document.querySelector('.dark-search-submit');

    if (searchInput) {
        // Auto-focus search input on page load if it has value
        if (searchInput.value) {
            searchInput.focus();
        }

        // Show/hide clear button based on input
        searchInput.addEventListener('input', function() {
            toggleClearButton();
        });

        // Clear search on clear button click
        if (searchClear) {
            searchClear.addEventListener('click', function() {
                searchInput.value = '';
                searchInput.focus();
                toggleClearButton();
                // Optionally submit form to show all results
                searchForm.submit();
            });
        }

        // Add loading state to submit button
        if (searchForm && searchSubmit) {
            searchForm.addEventListener('submit', function() {
                searchSubmit.classList.add('loading');
                searchSubmit.innerHTML = `
                    <svg width="16" height="16" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v2.586L7.707 9.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 9.586V7z" clip-rule="evenodd" />
                    </svg>
                `;
            });
        }

        // Keyboard shortcuts
        searchInput.addEventListener('keydown', function(e) {
            // Clear on Escape key
            if (e.key === 'Escape' && searchInput.value) {
                searchInput.value = '';
                toggleClearButton();
                e.preventDefault();
            }

            // Submit on Enter (already default)
            if (e.key === 'Enter' && searchSubmit) {
                // Add subtle animation
                searchSubmit.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    searchSubmit.style.transform = 'scale(1)';
                }, 150);
            }
        });
    }

    function toggleClearButton() {
        const searchClear = document.querySelector('.dark-search-clear');
        const searchDivider = document.querySelector('.dark-search-divider');

        if (searchInput && searchInput.value) {
            if (searchClear) searchClear.style.display = 'flex';
            if (searchDivider) searchDivider.style.display = 'block';
        } else {
            if (searchClear) searchClear.style.display = 'none';
            if (searchDivider) searchDivider.style.display = 'none';
        }
    }

    // Initialize clear button state
    toggleClearButton();
});

// Global function for clear button (if needed in Thymeleaf)
function clearSearch() {
    const searchInput = document.querySelector('.dark-search-input');
    const searchForm = document.querySelector('.dark-search-form');

    if (searchInput && searchForm) {
        searchInput.value = '';
        searchForm.submit();
    }
}