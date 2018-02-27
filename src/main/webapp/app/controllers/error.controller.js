(function () {
  'use strict';

  angular
  .module('app')
  .factory('ErrorController', ErrorController);

  ErrorController.$inject = ['$rootScope', '$cookies', '$routeParams', '$location'];

  function ErrorController($rootScope, $cookies, $routeParams, $location) {

    var ctrl = {};

    ctrl.httpGetErroCb = function(data, status, headers) {
      if (status != 200 && status != -1 && undefined != status) {
        if ("io.jsonwebtoken.ExpiredJwtException" == data.exception) {
          $rootScope.httpErrorMessage = "Your session expired. Please re-login";
        } else {
          $rootScope.httpErrorMessage = "Error while processing path "+data.path+" : "+data.error;
        }
      }
    };

    return ctrl;
  }
})();