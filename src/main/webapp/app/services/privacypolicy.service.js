(function() {
  'use strict';

  angular
    .module('app')
    .factory('PrivacypolicyService', PrivacypolicyService);
  
  PrivacypolicyService.$inject = ['$http', '$location', '$sce'];
  
  function PrivacypolicyService($http, $location, $sce) {

    var service = {};

    service.loadExternalPrivacyPolicy = loadExternalPrivacyPolicy;

    function loadExternalPrivacyPolicy(success, error) {
      $http({
        url: 'https://www.iubenda.com/privacy-policy/42083542',
        method: 'GET'
      }).success(success).error(error);
    }
    
    return service;
  }
  
})();