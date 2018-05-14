(function() {
  'use strict';

  angular
    .module('app')
    .factory('LoginService', LoginService);
  
  LoginService.$inject = ['$http', '$location'];
  
  function LoginService($http, $location) {

    var service = {};

    service.login = login;
    service.loginFacebook = loginFacebook;
    service.logout = logout;
    service.loginSuccessful = loginSuccessful;

    function login(credentials, success, error) {
      $http({
        url: '/users/login',
        method: 'POST',
        data: credentials,
        headers : {
          "Content-Type" : "application/json;charset=UTF-8"
        }
      }).success(success).error(error);
    }
    
    function loginFacebook(success, error) {
//      $http({
//        url: '/signin/facebook',
//        method: 'POST',
//        data: {scope: "public_profile"},
//        headers : {
//          "Content-Type" : "application/x-www-form-urlencoded",
//          "Access-Control-Allow-Origin" : "*",
//          "Access-Control-Allow-Methods" : "OPTIONS, HEAD, GET, POST, PUT, DELETE",
//          "Access-Control-Allow-Headers" : "X-Requested-With, X-Auth-Token, Content-Type, Content-Length, Authorization"
//          
//        }
//      }).success(success).error(error);
      $.ajaxSetup({
        beforeSend : function(xhr, settings) {
          if (settings.type == 'POST' || settings.type == 'PUT' || settings.type == 'DELETE') {
            if (!(/^http:.*/.test(settings.url) || /^https:.*/
                .test(settings.url))) {
              // Only send the token to relative URLs i.e. locally.
              xhr.setRequestHeader("X-XSRF-TOKEN", Cookies.get('XSRF-TOKEN'));
            }
          }
        }
        });
    }
    
    function loginSuccessful(success, error) {
      $http({
        url: '/users/login/success',
        method: 'POST',
        headers : {
          "Content-Type" : "application/json;charset=UTF-8",
          "Accept" : "application/json;charset=UTF-8"
        }
      }).success(success).error(error);
    }
    
    function logout(callback) {
      $http.post('/users/logout').success(callback).error(callback);
    }
    
    return service;
  }
  
})();