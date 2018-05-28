(function () {
  'use strict';

  angular
    .module('app')
    .controller('PrivacypolicyController', PrivacypolicyController);

  PrivacypolicyController.$inject = ['$rootScope', '$scope', '$cookies', '$route', '$routeParams', '$location', '$sce', 'PrivacypolicyService'];

  function PrivacypolicyController($rootScope, $scope, $cookies, $route, $routeParams, $location, $sce, PrivacypolicyService) {

    var ctrl = this;

    $scope.privacyPolicyExternalPage = {};
    
    ctrl.$onInit = function() {
      //console.log("Privacy policy controller initialized");
      //PrivacypolicyService.loadExternalPrivacyPolicy(loadSuccessCallback, loadErrorCallback);
      //$scope.privacyPolicyExternalPage = $sce.trustAsResourceUrl('https://www.iubenda.com/privacy-policy/42083542');
    };

    var loadSuccessCallback = function(data, status, headers) {
      $scope.privacyPolicyExternalPage = data;
    };
    
    var loadErrorCallback = function(data, status, headers) {
      console.log("Error loading privacy policy page");
    };

  }
})();