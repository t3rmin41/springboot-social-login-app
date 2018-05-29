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
    service.loginGoogle = loginGoogle;
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
      $http({
        url: '/facebook/login',
        method: 'GET',
        headers : {
//          "Content-Type" : "application/x-www-form-urlencoded",
//          "Access-Control-Allow-Origin" : "*",
//          "Access-Control-Allow-Methods" : "OPTIONS, HEAD, GET, POST, PUT, DELETE",
//          "Access-Control-Allow-Headers" : "X-Requested-With, X-Auth-Token, Content-Type, Content-Length, Authorization",
          "Access-Control-Allow-Credentials" : "true"
        }
      }).success(success).error(error);
    }
    
    function loginGoogle(success, error) {
    $http({
      url: '/google/login',
      method: 'GET',
      headers : {
        "Access-Control-Allow-Credentials" : "true"
      }
    }).success(success).error(error);
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