(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$rootScope', '$scope', '$cookies', '$location', '$uibModal', '$window', '$sce', 'LoginService', 'UserService'];

    function LoginController($rootScope, $scope, $cookies, $location, $uibModal, $window, $sce, LoginService, UserService) {
      
        $window.inviteCallback = function() {
            console.log('hi');
        };
    	
      $scope.login = function() {
        LoginService.login($scope.credentials, loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginFacebook = function() {
        LoginService.loginFacebook(loginSuccessCallback, loginErrorCallback);
      };
      
      $scope.loginGoogle = function() {
        LoginService.loginGoogle(loginSuccessCallback, loginErrorCallback);
      };

//      $scope.loginGoogle = function() {
//        //$window.open("/googlelogin", "popup", "width=300,height=200,left=10,top=150");
//        var modal = $uibModal.open({
//            resolve: {
//              LoginService: LoginService
//            },
//            size: 'md',
//            templateUrl: '/googlelogin',
//            controller: SocialLoginModalController
//          });
//          modal.result.then(function(){
//            console.log("SocialLogin then");
//          }, function(){});
//      };

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
    
    angular.module('app').controller('SocialLoginModalController', SocialLoginModalController);
    SocialLoginModalController.$inject = ['$scope', '$uibModalInstance', '$cookies', '$routeParams', '$location', 'LoginService'];
    function SocialLoginModalController($scope, $uibModalInstance, $cookies, $routeParams, $location, LoginService) {
    
    }
})();