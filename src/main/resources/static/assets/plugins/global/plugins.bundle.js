// Minimal implementation of plugins.bundle.js
(function() {
  'use strict';

  // Initialize Bootstrap tooltips and popovers
  document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips if Bootstrap is available
    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
      var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
      tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
      });
    }

    // Initialize popovers if Bootstrap is available
    if (typeof bootstrap !== 'undefined' && bootstrap.Popover) {
      var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
      popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
      });
    }
  });

  // Drawer initialization for mobile sidebar
  window.KTDrawer = function(element, options) {
    var the = this;
    var body = document.getElementsByTagName("BODY")[0];

    if (!element) {
      return;
    }

    // Default options
    var defaultOptions = {
      overlay: true,
      direction: 'left',
      baseClass: 'drawer',
      overlayClass: 'drawer-overlay'
    };

    // Merge options
    the.options = Object.assign(defaultOptions, options);
    the.element = element;
    the.overlayElement = null;
    the.name = the.element.getAttribute('data-kt-drawer-name');
    the.shown = false;

    // Event Handlers
    the.toggleElement = document.querySelector('[data-kt-drawer-toggle="' + the.name + '"]');

    if (the.toggleElement) {
      the.toggleElement.addEventListener('click', function() {
        the.toggle();
      });
    }

    // Methods
    the.toggle = function() {
      if (the.shown) {
        the.hide();
      } else {
        the.show();
      }
    };

    the.show = function() {
      if (the.shown) {
        return;
      }

      the.shown = true;
      the.element.classList.add('drawer-on');

      if (the.options.overlay) {
        the.overlayElement = document.createElement('DIV');
        the.overlayElement.classList.add(the.options.overlayClass);
        document.body.appendChild(the.overlayElement);

        the.overlayElement.addEventListener('click', function() {
          the.hide();
        });
      }

      body.classList.add('drawer-on');
    };

    the.hide = function() {
      if (!the.shown) {
        return;
      }

      the.shown = false;
      the.element.classList.remove('drawer-on');

      if (the.overlayElement) {
        document.body.removeChild(the.overlayElement);
        the.overlayElement = null;
      }

      body.classList.remove('drawer-on');
    };
  };

  // Initialize drawers
  document.addEventListener('DOMContentLoaded', function() {
    var drawerElements = document.querySelectorAll('[data-kt-drawer="true"]');

    drawerElements.forEach(function(drawerElement) {
      var drawer = new KTDrawer(drawerElement, {
        overlay: drawerElement.getAttribute('data-kt-drawer-overlay') === 'true',
        direction: drawerElement.getAttribute('data-kt-drawer-direction')
      });
    });
  });

  // Scroll utilities
  window.KTScroll = function(element, options) {
    if (!element) {
      return;
    }

    // Default options
    var defaultOptions = {
      height: 'auto'
    };

    // Merge options
    options = Object.assign(defaultOptions, options);

    // Set height
    if (options.height !== 'auto') {
      element.style.height = options.height;
      element.style.overflow = 'auto';
    }
  };

  // Initialize scrolls
  document.addEventListener('DOMContentLoaded', function() {
    var scrollElements = document.querySelectorAll('[data-kt-scroll="true"]');

    scrollElements.forEach(function(scrollElement) {
      new KTScroll(scrollElement, {
        height: scrollElement.getAttribute('data-kt-scroll-height')
      });
    });
  });

  // Menu utilities
  window.KTMenu = function(element) {
    var the = this;

    if (!element) {
      return;
    }

    the.element = element;
    the.menuItems = element.querySelectorAll('.menu-item');

    // Event handlers
    the.menuItems.forEach(function(menuItem) {
      var menuLink = menuItem.querySelector('.menu-link');

      if (menuLink) {
        menuLink.addEventListener('click', function(e) {
          if (menuItem.classList.contains('menu-item-submenu')) {
            e.preventDefault();
            menuItem.classList.toggle('menu-item-open');
          }
        });
      }
    });
  };

  // Initialize menus
  document.addEventListener('DOMContentLoaded', function() {
    var menuElements = document.querySelectorAll('[data-kt-menu="true"]');

    menuElements.forEach(function(menuElement) {
      new KTMenu(menuElement);
    });
  });
})();
