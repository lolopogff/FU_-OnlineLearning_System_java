// Custom select functionality (for future use)
document.addEventListener('DOMContentLoaded', function() {
    // Initialize all custom selects if they exist
    const selectContainers = document.querySelectorAll('.dark-select-container');

    selectContainers.forEach(container => {
        const trigger = container.querySelector('.dark-select-trigger');
        const dropdown = container.querySelector('.dark-select-dropdown');
        const hiddenInput = container.querySelector('input[type="hidden"]');
        const valueDisplay = container.querySelector('.dark-select-value');

        if (!trigger || !dropdown) return;

        // Toggle dropdown
        trigger.addEventListener('click', (e) => {
            e.stopPropagation();
            const isOpen = container.classList.contains('open');

            // Close all other selects
            document.querySelectorAll('.dark-select-container.open').forEach(other => {
                if (other !== container) {
                    other.classList.remove('open');
                }
            });

            // Toggle current select
            container.classList.toggle('open', !isOpen);
        });

        // Handle option selection
        dropdown.querySelectorAll('.dark-select-option').forEach(option => {
            option.addEventListener('click', () => {
                const value = option.getAttribute('data-value');
                const text = option.textContent;

                // Update display
                valueDisplay.textContent = text;

                // Update hidden input
                hiddenInput.value = value;

                // Update selected state
                dropdown.querySelectorAll('.dark-select-option').forEach(opt => {
                    opt.classList.remove('selected');
                });
                option.classList.add('selected');

                // Close dropdown
                container.classList.remove('open');

                // Auto-submit form if needed
                const form = document.getElementById('filterForm');
                if (form) form.submit();
            });
        });
    });

    // Close dropdowns when clicking outside
    document.addEventListener('click', () => {
        document.querySelectorAll('.dark-select-container.open').forEach(container => {
            container.classList.remove('open');
        });
    });

    // Prevent dropdown close when clicking inside dropdown
    document.querySelectorAll('.dark-select-dropdown').forEach(dropdown => {
        dropdown.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    });
});