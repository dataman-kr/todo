// Minimal implementation of scripts.bundle.js
(function() {
  'use strict';

  // Global initialization
  var KTApp = function() {
    var initialized = false;

    var initialize = function() {
      if (initialized) {
        return;
      }

      initialized = true;

      // Initialize components
      initializeComponents();
    };

    var initializeComponents = function() {
      // Initialize all drawers
      var drawerElements = document.querySelectorAll('[data-kt-drawer="true"]');
      drawerElements.forEach(function(drawerElement) {
        if (window.KTDrawer) {
          new KTDrawer(drawerElement);
        }
      });

      // Initialize all scrolls
      var scrollElements = document.querySelectorAll('[data-kt-scroll="true"]');
      scrollElements.forEach(function(scrollElement) {
        if (window.KTScroll) {
          new KTScroll(scrollElement);
        }
      });

      // Initialize all menus
      var menuElements = document.querySelectorAll('[data-kt-menu="true"]');
      menuElements.forEach(function(menuElement) {
        if (window.KTMenu) {
          new KTMenu(menuElement);
        }
      });

      // Initialize dropdowns
      initializeDropdowns();
    };

    var initializeDropdowns = function() {
      // Handle dropdown menu items click
      document.addEventListener('click', function(e) {
        var target = e.target;

        // Handle dropdown toggle
        if (target.hasAttribute('data-kt-menu-trigger')) {
          var menuId = target.getAttribute('data-kt-menu-trigger');
          var menu = document.querySelector('[data-kt-menu="' + menuId + '"]');

          if (menu) {
            menu.classList.toggle('show');

            // Position the menu
            var rect = target.getBoundingClientRect();
            menu.style.top = (rect.bottom + window.scrollY) + 'px';
            menu.style.left = rect.left + 'px';

            // Close when clicking outside
            document.addEventListener('click', function closeMenu(event) {
              if (!menu.contains(event.target) && event.target !== target) {
                menu.classList.remove('show');
                document.removeEventListener('click', closeMenu);
              }
            });
          }
        }
      });
    };

    return {
      init: function() {
        initialize();
      }
    };
  }();

  // Initialize on document ready
  document.addEventListener('DOMContentLoaded', function() {
    KTApp.init();
  });

  // Make KTApp available globally
  window.KTApp = KTApp;
})();
