(function () {
  'use strict';

  angular
    .module('app')
    .controller('ProductController', ProductController);

  ProductController.$inject = ['$rootScope', '$scope', '$cookies', '$routeParams', '$uibModal', '$location', 'ErrorController'];
  function ProductController($rootScope, $scope, $cookies, $routeParams, $uibModal, $location, ErrorController) {

    var ctrl = this;

    $scope.products = [];

    $rootScope.$on('ProductReload', function (event, message){
      ctrl.getProducts();
    });
    
    ctrl.$onInit = function() {
      ctrl.getProducts();
    };

    ctrl.getProducts = function() {
      var data = [
                  { id: '1', title: 'York 70660 blue polarized sunglasses', shortDescription: 'YORK_70660' , price: '11.29' },
                  { id: '2', title: 'Aviator Blue polarized sunglasses', shortDescription: '272817599048' , price: '7.29' },
                  { id: '3', title: 'York 70684 black polarized sunglasses', shortDescription: 'YORK_70684' , price: '9.25' }
                 ];
      $scope.products = data;
    }

    var getProductsSuccessCb = function(data, status, headers) {
      $scope.products = data;
    }
    
    var getProductsErrorCb = function(data, status, headers) {
      //console.log(status);
    }
  }
})();