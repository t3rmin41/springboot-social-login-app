(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$rootScope', '$scope', '$cookies', '$location', '$window', 'LoginService', 'UserService'];

    function LoginController($rootScope, $scope, $cookies, $location, $window, LoginService, UserService) {
      
      var ctrl = this;

      $scope.googleLoginClicked = false;
      $scope.authenticated = false;
      $scope.userLoggedOut = false;
      
      ctrl.$onInit = function() {
        //console.log('LoginController initialized');
        ctrl.getInitialCookies();
      };
      
      $scope.login = function() {
        LoginService.login($scope.credentials, loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginFacebook = function() {
        LoginService.loginFacebook(loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginGoogle = function() {
        $cookies.put('googleLoginClicked', true);
        LoginService.loginGoogle(loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginGoogleWithoutClick = function() {
        LoginService.loginGoogle(loginSuccessCallback, loginErrorCallback);
      };

      $scope.showPrivacyPolicy = function() {
        $location.path("/privacypolicy");
      }
      
      ctrl.getInitialCookies = function() {
        $scope.googleLoginClicked = $cookies.get('googleLoginClicked');
        $scope.authenticated = $cookies.get('authenticated');
        $scope.userLoggedOut = $cookies.get('userLoggedOut');
        //if ($scope.googleLoginClicked != undefined && $scope.googleLoginClicked && ) {
        if ("true" == $scope.googleLoginClicked && "true" != $scope.userLoggedOut) {
          $scope.loginGoogleWithoutClick();
        }
      }
      
      var loginSuccessCallback = function(data, status, headers) {
        $cookies.put('token', headers('Authorization'));
        $cookies.put('authenticated', true);
        $cookies.put('userLoggedOut', false);
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
      };
      
      var loginErrorCallback = function(data, status, headers) {
        var error = {};
        if (null == data || headers('GoogleLoginRequired') == "true") {
          $cookies.put('GoogleLoginRequired', true);
          window.location.href = "/google/obtaintoken";
        }
        error.message = data.message;
        $scope.error = error;
      };
    }
})();