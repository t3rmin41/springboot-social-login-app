(function () {
  'use strict';

  angular
    .module('app')
    .controller('PrivacypolicyController', PrivacypolicyController);

  PrivacypolicyController.$inject = ['$rootScope', '$scope', '$cookies', '$route', '$routeParams', '$location'];

  function PrivacypolicyController($rootScope, $scope, $cookies, $route, $routeParams, $location) {

    var ctrl = this;

    ctrl.$onInit = function() {
      console.log("Privacy policy controller initialized");
    };

  }
})();