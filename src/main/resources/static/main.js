(function() {
  'use strict';

  // Angular application module
  const app = angular.module('todoApp', ['ngRoute']);

  // Configure routes
  app.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'app/home/home.html',
        controller: 'HomeController'
      })
      .when('/login', {
        templateUrl: 'app/auth/login.html',
        controller: 'LoginController'
      })
      .when('/signup', {
        templateUrl: 'app/auth/signup.html',
        controller: 'SignupController'
      })
      .when('/todos', {
        templateUrl: 'app/todos/list.html',
        controller: 'TodoListController',
        resolve: {
          auth: ['AuthService', function(AuthService) {
            return AuthService.checkAuth();
          }]
        }
      })
      .when('/todos/create', {
        templateUrl: 'app/todos/create.html',
        controller: 'TodoCreateController',
        resolve: {
          auth: ['AuthService', function(AuthService) {
            return AuthService.checkAuth();
          }]
        }
      })
      .when('/todos/:id/edit', {
        templateUrl: 'app/todos/edit.html',
        controller: 'TodoEditController',
        resolve: {
          auth: ['AuthService', function(AuthService) {
            return AuthService.checkAuth();
          }]
        }
      })
      .when('/admin/users', {
        templateUrl: 'app/admin/users.html',
        controller: 'AdminUsersController',
        resolve: {
          auth: ['AuthService', function(AuthService) {
            return AuthService.checkAdmin();
          }]
        }
      })
      .otherwise({
        redirectTo: '/'
      });

    // Use HTML5 history API
    $locationProvider.html5Mode(true);
  }]);

  // Token Storage service to break circular dependency
  app.service('TokenStorage', ['$window', function($window) {
    this.setToken = function(token) {
      $window.localStorage.setItem('token', token);
    };

    this.getToken = function() {
      return $window.localStorage.getItem('token');
    };

    this.removeToken = function() {
      $window.localStorage.removeItem('token');
    };

    this.setUserId = function(userId) {
      $window.localStorage.setItem('userId', userId);
    };

    this.getUserId = function() {
      return $window.localStorage.getItem('userId');
    };

    this.removeUserId = function() {
      $window.localStorage.removeItem('userId');
    };

    this.setNickname = function(nickname) {
      $window.localStorage.setItem('nickname', nickname);
    };

    this.getNickname = function() {
      return $window.localStorage.getItem('nickname');
    };

    this.removeNickname = function() {
      $window.localStorage.removeItem('nickname');
    };

    this.setRoles = function(roles) {
      $window.localStorage.setItem('roles', JSON.stringify(roles));
    };

    this.getRoles = function() {
      const roles = $window.localStorage.getItem('roles');
      return roles ? JSON.parse(roles) : [];
    };

    this.removeRoles = function() {
      $window.localStorage.removeItem('roles');
    };
  }]);

  // Authentication service
  app.service('AuthService', ['$http', '$q', '$window', '$location', 'TokenStorage', function($http, $q, $window, $location, TokenStorage) {
    this.login = function(credentials) {
      return $http.post('/api/v1/auth/login', credentials)
        .then(function(response) {
          if (response.data && response.data.token) {
            TokenStorage.setToken(response.data.token);
            TokenStorage.setUserId(response.data.userId);
            TokenStorage.setNickname(response.data.nickname);
            TokenStorage.setRoles(response.data.roles || []);
            return response.data;
          }
          return $q.reject('Authentication failed');
        });
    };

    this.signup = function(user) {
      return $http.post('/api/v1/auth/register', user);
    };

    this.logout = function() {
      TokenStorage.removeToken();
      TokenStorage.removeUserId();
      TokenStorage.removeNickname();
      TokenStorage.removeRoles();
    };

    this.isAuthenticated = function() {
      return !!TokenStorage.getToken();
    };

    this.isAdmin = function() {
      const roles = TokenStorage.getRoles();
      return roles.includes('ADMIN');
    };

    this.checkAuth = function() {
      const deferred = $q.defer();
      if (this.isAuthenticated()) {
        deferred.resolve();
      } else {
        deferred.reject('Not authenticated');
        $location.path('/login');
      }
      return deferred.promise;
    };

    this.checkAdmin = function() {
      const deferred = $q.defer();
      if (this.isAuthenticated() && this.isAdmin()) {
        deferred.resolve();
      } else {
        deferred.reject('Not authorized');
        $location.path('/login');
      }
      return deferred.promise;
    };

    this.getUserId = function() {
      return TokenStorage.getUserId();
    };

    this.getNickname = function() {
      return TokenStorage.getNickname();
    };

    this.getRoles = function() {
      return TokenStorage.getRoles();
    };
  }]);

  // HTTP interceptor for adding auth token
  app.factory('AuthInterceptor', ['TokenStorage', function(TokenStorage) {
    return {
      request: function(config) {
        const token = TokenStorage.getToken();
        if (token) {
          config.headers.Authorization = 'Bearer ' + token;
        }
        return config;
      }
    };
  }]);

  app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('AuthInterceptor');
  }]);

  // Todo service
  app.service('TodoService', ['$http', function($http) {
    this.getAllTodos = function() {
      return $http.get('/api/v1/todos');
    };

    this.getTodoById = function(id) {
      return $http.get('/api/v1/todos/' + id);
    };

    this.createTodo = function(todo) {
      return $http.post('/api/v1/todos', todo);
    };

    this.updateTodo = function(id, todo) {
      return $http.put('/api/v1/todos/' + id, todo);
    };

    this.deleteTodo = function(id) {
      return $http.delete('/api/v1/todos/' + id);
    };

    this.toggleTodoStatus = function(id, isDone) {
      return $http.put('/api/v1/todos/' + id, { isDone: isDone });
    };
  }]);

  // Admin service
  app.service('AdminService', ['$http', function($http) {
    this.getAllUsers = function() {
      return $http.get('/api/v1/admin/users');
    };

    this.unlockUser = function(userId) {
      return $http.post('/api/v1/admin/users/' + userId + '/unlock');
    };
  }]);

  // Controllers
  app.controller('HomeController', ['$scope', 'AuthService', function($scope, AuthService) {
    $scope.isAuthenticated = AuthService.isAuthenticated();
    $scope.nickname = AuthService.getNickname();
    $scope.isAdmin = AuthService.isAdmin();
  }]);

  app.controller('LoginController', ['$scope', 'AuthService', '$location', function($scope, AuthService, $location) {
    $scope.user = {
      email: '',
      password: ''
    };
    $scope.errorMessage = '';

    $scope.login = function() {
      AuthService.login($scope.user)
        .then(function() {
          $location.path('/todos');
        })
        .catch(function(error) {
          $scope.errorMessage = error.data && error.data.error ? error.data.error : 'Login failed';
        });
    };
  }]);

  app.controller('SignupController', ['$scope', 'AuthService', '$location', function($scope, AuthService, $location) {
    $scope.user = {
      email: '',
      password: '',
      nickname: ''
    };
    $scope.errorMessage = '';

    $scope.signup = function() {
      AuthService.signup($scope.user)
        .then(function() {
          $location.path('/login');
        })
        .catch(function(error) {
          $scope.errorMessage = error.data && error.data.error ? error.data.error : 'Registration failed';
        });
    };
  }]);

  app.controller('TodoListController', ['$scope', 'TodoService', 'AuthService', '$location', function($scope, TodoService, AuthService, $location) {
    $scope.todos = [];
    $scope.nickname = AuthService.getNickname();
    $scope.isAdmin = AuthService.isAdmin();

    $scope.loadTodos = function() {
      TodoService.getAllTodos()
        .then(function(response) {
          $scope.todos = response.data;
        })
        .catch(function(error) {
          console.error('Error loading todos:', error);
        });
    };

    $scope.toggleTodoStatus = function(todo) {
      TodoService.toggleTodoStatus(todo.id, !todo.isDone)
        .then(function() {
          todo.isDone = !todo.isDone;
        })
        .catch(function(error) {
          console.error('Error toggling todo status:', error);
        });
    };

    $scope.deleteTodo = function(id) {
      if (confirm('Are you sure you want to delete this todo?')) {
        TodoService.deleteTodo(id)
          .then(function() {
            $scope.loadTodos();
          })
          .catch(function(error) {
            console.error('Error deleting todo:', error);
          });
      }
    };

    $scope.logout = function() {
      AuthService.logout();
      $location.path('/login');
    };

    // Load todos when controller initializes
    $scope.loadTodos();
  }]);

  app.controller('TodoCreateController', ['$scope', 'TodoService', 'AuthService', '$location', function($scope, TodoService, AuthService, $location) {
    $scope.todo = {
      title: '',
      description: ''
    };
    $scope.nickname = AuthService.getNickname();
    $scope.isAdmin = AuthService.isAdmin();

    $scope.createTodo = function() {
      TodoService.createTodo($scope.todo)
        .then(function() {
          $location.path('/todos');
        })
        .catch(function(error) {
          console.error('Error creating todo:', error);
        });
    };
  }]);

  app.controller('TodoEditController', ['$scope', 'TodoService', 'AuthService', '$routeParams', '$location', function($scope, TodoService, AuthService, $routeParams, $location) {
    $scope.todo = {
      title: '',
      description: ''
    };
    $scope.nickname = AuthService.getNickname();
    $scope.isAdmin = AuthService.isAdmin();

    TodoService.getTodoById($routeParams.id)
      .then(function(response) {
        $scope.todo = response.data;
      })
      .catch(function(error) {
        console.error('Error loading todo:', error);
      });

    $scope.updateTodo = function() {
      TodoService.updateTodo($routeParams.id, $scope.todo)
        .then(function() {
          $location.path('/todos');
        })
        .catch(function(error) {
          console.error('Error updating todo:', error);
        });
    };
  }]);

  app.controller('AdminUsersController', ['$scope', 'AdminService', 'AuthService', '$location', function($scope, AdminService, AuthService, $location) {
    $scope.users = [];
    $scope.nickname = AuthService.getNickname();
    $scope.isAdmin = AuthService.isAdmin();
    $scope.successMessage = '';
    $scope.errorMessage = '';

    $scope.loadUsers = function() {
      AdminService.getAllUsers()
        .then(function(response) {
          $scope.users = response.data;
        })
        .catch(function(error) {
          console.error('Error loading users:', error);
          $scope.errorMessage = 'Failed to load users. Please try again.';
        });
    };

    $scope.unlockUser = function(userId) {
      AdminService.unlockUser(userId)
        .then(function(response) {
          $scope.successMessage = 'User account unlocked successfully.';
          $scope.loadUsers(); // Reload the user list
        })
        .catch(function(error) {
          console.error('Error unlocking user:', error);
          $scope.errorMessage = 'Failed to unlock user account. Please try again.';
        });
    };

    $scope.logout = function() {
      AuthService.logout();
      $location.path('/login');
    };

    // Load users when controller initializes
    $scope.loadUsers();
  }]);

})();
