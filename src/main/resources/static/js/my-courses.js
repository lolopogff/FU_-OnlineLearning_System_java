// My Courses specific functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('My Courses page initialized');

    // Initialize course card interactions
    initCourseCards();

    // Initialize statistics animations
    initStatsAnimations();
});

function initCourseCards() {
    const courseCards = document.querySelectorAll('.dark-course-card');

    courseCards.forEach(card => {
        // Add click effect
        card.addEventListener('click', function(e) {
            // Don't trigger if clicking on buttons or links
            if (e.target.tagName === 'A' || e.target.tagName === 'BUTTON' || e.target.closest('a') || e.target.closest('button')) {
                return;
            }

            // Find the view details link and click it
            const detailsLink = card.querySelector('a[href*="/courses/details"]');
            if (detailsLink) {
                detailsLink.click();
            }
        });

        // Add keyboard navigation
        card.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                const detailsLink = card.querySelector('a[href*="/courses/details"]');
                if (detailsLink) {
                    detailsLink.click();
                }
            }
        });
    });
}

function initStatsAnimations() {
    const statCards = document.querySelectorAll('.dark-stat-card');

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, { threshold: 0.1 });

    statCards.forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(card);
    });
}

// Export delete confirmation function
function confirmDelete(courseTitle) {
    return confirm(`Are you sure you want to delete the course "${courseTitle}"? This action cannot be undone.`);
}