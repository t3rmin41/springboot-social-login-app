(function() {
  'use strict';

  angular
    .module('app')
    .factory('UserService', UserService);
  
  UserService.$inject = ['$http', '$location'];
  
  function UserService($http, $location) {
    
    var service = {};

    service.getAllUsers = function(success, error) {
      $http({
        url: '/users/all',
        method: 'GET',
        headers : {
          "Content-Type" : "application/json;charset=UTF-8",
          "Accept" : "application/json;charset=UTF-8"
        }
      }).success(success).error(error);
    };

    service.getUserById = function(id, success, error) {
      $http({
        url: '/users/'+id,
        method: 'GET',
        headers : {
          "Content-Type" : "application/json;charset=UTF-8",
          "Accept" : "application/json;charset=UTF-8"
        }
      }).success(success).error(error);
    };

    service.saveUser = function(user, success, error) {
      $http({
        url: '/users/create',
        method: 'POST',
        data: user,
        headers : {
          "Content-Type" : "application/json;charset=UTF-8",
          "Accept" : "application/json;charset=UTF-8"
        }
      }).success(success).error(error);
    };
    
    service.editUser = function(user, success, error) {
      $http({
        url: '/users/update',
        method: 'PUT',
        data: user,
        headers : {
          "Content-Type" : "application/json;charset=UTF-8",
          "Accept" : "application/json;charset=UTF-8"
        }
      }).success(success).error(error);
    };
    
    service.deleteUserById = function(id, success, error) {
      $http({
        url: '/users/delete/'+id,
        method: 'DELETE',
        headers : {
          "Content-Type" : "application/json;charset=UTF-8",
          "Accept" : "application/json;charset=UTF-8"
        }
      }).success(success).error(error);
    };

    service.getRoles = function(success, error) {
      $http({
        url: '/users/roles',
        method: 'GET',
        headers : {
          "Content-Type" : "application/json;charset=UTF-8",
          "Accept" : "application/json;charset=UTF-8"
        }
      }).success(success).error(error);
    };
    
    return service;
  }
  
})();