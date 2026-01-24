// Custom application JavaScript
console.log('Lexpage app initialized');

// Accordion functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing accordion...');

    // Find all buttons with data-collapse-toggle attribute
    const accordionButtons = document.querySelectorAll('[data-collapse-toggle]');
    console.log('Found accordion buttons:', accordionButtons.length);

    accordionButtons.forEach((button, index) => {
        console.log('Initializing button', index, button);

        button.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Accordion button clicked');

            const targetId = this.getAttribute('data-collapse-toggle');
            console.log('Target ID:', targetId);

            const targetElement = document.getElementById(targetId);
            console.log('Target element:', targetElement);

            const isExpanded = this.getAttribute('aria-expanded') === 'true';
            console.log('Is expanded:', isExpanded);

            // Toggle the target element
            if (targetElement) {
                targetElement.classList.toggle('hidden');
                console.log('Toggled hidden class');
            }

            // Update aria-expanded
            this.setAttribute('aria-expanded', !isExpanded);

            // Rotate the chevron icon
            const svg = this.querySelector('svg');
            if (svg) {
                svg.classList.toggle('rotate-180');
                console.log('Toggled chevron rotation');
            }
        });
    });
});
