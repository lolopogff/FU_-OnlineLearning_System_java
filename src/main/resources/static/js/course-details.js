// Course Details specific functionality
document.addEventListener('DOMContentLoaded', function() {
    // Add interactive elements if needed
    const enrollButton = document.querySelector('.enroll-btn');

    if (enrollButton) {
        enrollButton.addEventListener('click', function(e) {
            // Add loading state to enroll button
            const originalText = this.innerHTML;
            this.innerHTML = `
                <svg width="16" height="16" viewBox="0 0 20 20" fill="currentColor" class="animate-spin">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v2.586L7.707 9.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L11 9.586V7z" clip-rule="evenodd" />
                </svg>
                Enrolling...
            `;
            this.disabled = true;

            // Fallback to restore button
            setTimeout(() => {
                this.innerHTML = originalText;
                this.disabled = false;
            }, 3000);
        });
    }

    // Add scroll animations for course elements
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);

    // Observe course cards for scroll animations
    document.querySelectorAll('.dark-card').forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'all 0.6s ease';
        observer.observe(card);
    });
});

// Utility function for course enrollment
function enrollInCourse(courseId) {
    // This function can be used for AJAX enrollment
    console.log('Enrolling in course:', courseId);

    // Example AJAX implementation:
    /*
    fetch(`/myCourses/enroll?courseId=${courseId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Show success message
            showNotification('Successfully enrolled in course!', 'success');
        } else {
            // Show error message
            showNotification('Enrollment failed: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Enrollment failed. Please try again.', 'error');
    });
    */
}

// Notification function (can be used for AJAX responses)
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `dark-alert dark-alert-${type} slide-in`;
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.zIndex = '10000';
    notification.style.minWidth = '300px';

    notification.innerHTML = `
        <div class="dark-alert-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
            </svg>
        </div>
        <div class="dark-alert-content">${message}</div>
    `;

    document.body.appendChild(notification);

    // Auto-remove after 5 seconds
    setTimeout(() => {
        notification.remove();
    }, 5000);
}