(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$rootScope', '$scope', '$cookies', '$location', '$window', 'LoginService', 'UserService'];

    function LoginController($rootScope, $scope, $cookies, $location, $window, LoginService, UserService) {
      
      var ctrl = this;

      $scope.dataLoaded = false;
      $scope.googleLoginClicked = false;
      $scope.facebookLoginClicked = false;
      //$scope.userLoggeOut = false;
      
      ctrl.$onInit = function() {
        //console.log('LoginController initialized');
        ctrl.getFacebookLoginSettings(); // sequence matters - $scope.dataLoaded is not set in getFacebookLoginSettings()
        ctrl.getGoogleLoginSettings();
      };
      
      $scope.login = function() {
        $scope.dataLoaded = false;
        LoginService.login($scope.credentials, loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginFacebook = function() {
        $scope.dataLoaded = false;
        $cookies.put('facebookLoginClicked', true);
        LoginService.loginFacebook(loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginGoogle = function() {
        $scope.dataLoaded = false;
        $cookies.put('googleLoginClicked', true);
        LoginService.loginGoogle(loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginFacebookWithoutClick = function() {
        LoginService.loginFacebook(loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginGoogleWithoutClick = function() {
        LoginService.loginGoogle(loginSuccessCallback, loginErrorCallback);
      };

      $scope.showPrivacyPolicy = function() {
        $location.path("/privacypolicy");
      }
      
      ctrl.getFacebookLoginSettings = function() {
        $scope.facebookLoginClicked = $cookies.get('facebookLoginClicked');
        $scope.authenticated = $cookies.get('authenticated');
        //$scope.userLoggedOut = $cookies.get('userLoggedOut');
        //if ($scope.googleLoginClicked != undefined && $scope.googleLoginClicked && ) {
        //if ("true" == $scope.facebookLoginClicked && "true" !=  $scope.userLoggedOut) {
        if ("true" == $scope.facebookLoginClicked) {
          $scope.loginFacebookWithoutClick();
        }
      }
      
      ctrl.getGoogleLoginSettings = function() {
        $scope.googleLoginClicked = $cookies.get('googleLoginClicked');
        $scope.facebookLoginClicked = $cookies.get('facebookLoginClicked');
        $scope.authenticated = $cookies.get('authenticated');
        //$scope.userLoggedOut = $cookies.get('userLoggedOut');
        //if ($scope.googleLoginClicked != undefined && $scope.googleLoginClicked && ) {
        //if ("true" == $scope.googleLoginClicked && "true" !=  $scope.userLoggedOut) {
        if ("true" == $scope.googleLoginClicked) {
          $scope.loginGoogleWithoutClick();
        } else if ("true" != $scope.facebookLoginClicked || undefined == $scope.facebookLoginClicked) {
          $scope.dataLoaded = true;
        }
      }
      
      var loginSuccessCallback = function(data, status, headers) {
        $cookies.put('token', headers('Authorization'));
        $cookies.put('authenticated', true);
        //$cookies.put('userLoggedOut', false);
        $scope.$root.$broadcast('UserlistReload', 'event data');
        delete $scope.userLoggedOut;
        delete $scope.$root.httpErrorMessage;
        LoginService.loginSuccessful(function(data, status, headers){
          //console.log(data);
          //$scope.$root.$broadcast('UserlistReload', 'event data');
          delete $scope.$root.httpErrorMessage;
          $cookies.putObject('user', data);
          $location.path("/");
          $scope.dataLoaded = true;
        }, function(){});
      };
      
      var loginErrorCallback = function(data, status, headers) {
        var error = {};
        if (headers('GoogleLoginRequired') == "true") {
          //$cookies.put('GoogleLoginRequired', true);
          window.location.href = "/google/obtaintoken";
          //$scope.loginGoogle();
        } else if (headers('FacebookLoginRequired') == "true") {
          window.location.href = "/facebook/obtaintoken";
          //$scope.loginFacebook();
        } else {
          error.message = data.message;
          $scope.error = error;
          $scope.dataLoaded = true;
        }
      };
    }
})();