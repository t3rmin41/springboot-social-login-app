(function () {
  'use strict';

  angular
    .module('app')
    .controller('HomeController', HomeController);

  HomeController.$inject = ['$rootScope', '$scope', '$cookies', '$route', '$routeParams', '$location', 'LoginService', 'ErrorController'];

  function HomeController($rootScope, $scope, $cookies, $route, $routeParams, $location, LoginService, ErrorController) {

    var ctrl = this;

    $scope.route = $route;

    $scope.cartAcknowledged = true;
    
    $scope.authenticated = ('true' == $cookies.get('authenticated'));

    if ($scope.authenticated) {
      $rootScope.user = $cookies.getObject('user');
    }

    if ($scope.authenticated) {
      $rootScope.hasManager = $scope.user.roles.filter(function(role){ return role.code == "MANAGER"}).length > 0;
      $rootScope.hasAdmin = $scope.user.roles.filter(function(role){ return role.code == "ADMIN"}).length > 0;
    }

    ctrl.$onInit = function() {
      if (undefined != $cookies.get('token') && null != $cookies.get('token')) {
      }
    };

    if (!$scope.authenticated) {
      $location.path("/login");
    }

    $scope.logout = function() {
      LoginService.logout(logoutCallback);
    };
    
    $scope.showPrivacyPolicy = function() {
      $location.path("/privacypolicy");
    };

    var logoutCallback = function(data, status, headers) {
      $cookies.put('authenticated', false);
      $cookies.put('userLoggedOut', true);
      $scope.authenticated = false;
      $scope.userLoggedOut = true;
      $location.path("/login");
    };

  }
})();