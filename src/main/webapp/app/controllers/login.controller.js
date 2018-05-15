(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$rootScope', '$scope', '$cookies', '$location', '$window', 'LoginService', 'UserService'];

    function LoginController($rootScope, $scope, $cookies, $location, $window, LoginService, UserService) {
      
      $scope.login = function() {
        LoginService.login($scope.credentials, loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginFacebook = function() {
        LoginService.loginFacebook(loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginGoogle = function() {
        LoginService.loginGoogle(loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.processForm = function() {
        console.log("Form processed");
        //$location.path("/");
      };
      
      var changeLocation = function(url, forceReload) {
        $scope = $scope || angular.element(document).scope();
        if(forceReload || $scope.$$phase) {
          $window.location = url;
        }
        else {
          //only use this if you want to replace the history stack
          //$location.path(url).replace();

          //this this if you want to change the URL and add it to the history stack
          $location.path(url);
          $scope.$apply();
        }
      };
      
      var loginSuccessCallback = function(data, status, headers) {
        $cookies.put('token', headers('Authorization'));
        $cookies.put('authenticated', true);
        $scope.$root.$broadcast('UserlistReload', 'event data');
        delete $scope.userLoggedOut;
        delete $scope.$root.httpErrorMessage;
        LoginService.loginSuccessful(function(data, status, headers){
          //console.log(data);
          //$scope.$root.$broadcast('UserlistReload', 'event data');
          delete $scope.$root.httpErrorMessage;
          $cookies.putObject('user', data);
          $location.path("/");
        }, function(){});
//        UserService.getUserInfo(function(data, status, headers){
//          console.log("UserInfo:"+data);
//        },
//          function(data, status, headers){
//          console.log("Error:"+data);
//        });
      };
      var loginErrorCallback = function(data, status, headers) {
        var error = {};
        error.message = data.message;
        $scope.error = error;
      };
    }
})();