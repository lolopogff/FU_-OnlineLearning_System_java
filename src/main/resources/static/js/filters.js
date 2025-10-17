// Filters functionality
document.addEventListener('DOMContentLoaded', function() {
    // Auto-submit form when chip is selected (for compact filters)
    const filterChips = document.querySelectorAll('.dark-filter-chip input[type="radio"]');

    filterChips.forEach(chip => {
        chip.addEventListener('change', function() {
            // Small delay to show visual feedback before submit
            setTimeout(() => {
                const form = document.getElementById('filterForm');
                if (form) form.submit();
            }, 300);
        });
    });

    // Auto-submit for compact select filters
    const compactSelects = document.querySelectorAll('.dark-select-compact');
    compactSelects.forEach(select => {
        select.addEventListener('change', function() {
            addLoadingState();
            // Submit form with small delay for visual feedback
            setTimeout(() => {
                const form = document.getElementById('filterForm');
                if (form) form.submit();
            }, 100);
        });
    });

    // Price input validation and auto-submit
    const priceInputs = document.querySelectorAll('.dark-price-input-compact, .dark-price-input');
    let priceTimeout;

    priceInputs.forEach(input => {
        input.addEventListener('input', function() {
            clearTimeout(priceTimeout);
            priceTimeout = setTimeout(() => {
                validatePriceRange();
                // Auto-submit after 1 second of inactivity
                const form = document.getElementById('filterForm');
                if (form) form.submit();
            }, 1000);
        });

        input.addEventListener('blur', function() {
            validatePriceRange();
        });
    });

    function validatePriceRange() {
        const minInput = document.querySelector('input[name="minPrice"]');
        const maxInput = document.querySelector('input[name="maxPrice"]');

        if (!minInput || !maxInput) return;

        const min = parseFloat(minInput.value) || 0;
        const max = parseFloat(maxInput.value) || 0;

        if (max > 0 && min > max) {
            // Swap values
            [minInput.value, maxInput.value] = [max, min];

            // Show visual feedback
            minInput.style.borderColor = 'var(--primary)';
            maxInput.style.borderColor = 'var(--primary)';

            setTimeout(() => {
                minInput.style.borderColor = '';
                maxInput.style.borderColor = '';
            }, 1000);
        }
    }

    function addLoadingState() {
        const applyButton = document.querySelector('.dark-filter-apply, .dark-select-compact');
        if (applyButton && applyButton.tagName === 'BUTTON') {
            const originalText = applyButton.innerHTML;
            applyButton.innerHTML = `
                <svg width="14" height="14" viewBox="0 0 20 20" fill="currentColor" class="animate-spin">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v2.586L7.707 9.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 9.586V7z" clip-rule="evenodd" />
                </svg>
                Applying...
            `;
            applyButton.disabled = true;

            // Restore button after submit (timeout fallback)
            setTimeout(() => {
                applyButton.innerHTML = originalText;
                applyButton.disabled = false;
            }, 2000);
        }
    }
});

// Add CSS for loading animation
const filterStyle = document.createElement('style');
filterStyle.textContent = `
    .animate-spin {
        animation: spin 1s linear infinite;
    }
    
    @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
`;
document.head.appendChild(filterStyle);