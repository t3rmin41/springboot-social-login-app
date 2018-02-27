(function () {
  'use strict';

  angular
    .module('app')
    .controller('HomeController', HomeController);

  HomeController.$inject = ['$rootScope', '$scope', '$cookies', '$route', '$routeParams', '$location', 'LoginService', 'ErrorController'];

  function HomeController($rootScope, $scope, $cookies, $route, $routeParams, $location, LoginService, ErrorController) {

    var ctrl = this;

    $scope.authenticated = ('true' == $cookies.get('authenticated'));
    
    //$rootScope.httpErrorMessage = "Some error";

    ctrl.$onInit = function() {
      if (undefined != $cookies.get('token') && null != $cookies.get('token')) {
        console.log("Home controller initialized with authorization token : " + $cookies.get('token'));
      }
    };

    if (!$scope.authenticated) {
      $location.path("/login");
    }
    
    $scope.user = $cookies.getObject('user');
    
    $scope.logout = function() {
      LoginService.logout(logoutCallback);
    };

    var logoutCallback = function(data, status, headers) {
      $cookies.put('authenticated', false);
      $scope.authenticated = false;
      $scope.userLoggedOut = true;
      $location.path("/login");
    };

  }
})();