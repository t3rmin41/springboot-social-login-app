(function () {
  'use strict';

  angular
    .module('app')
    .controller('UserController', UserController);

  UserController.$inject = ['$rootScope', '$scope', '$cookies', '$routeParams', '$uibModal', '$location', 'UserService', 'ErrorController'];

  function UserController($rootScope, $scope, $cookies, $routeParams, $uibModal, $location, UserService, ErrorController) {

    var ctrl = this;

    $scope.users = [];

    ctrl.$onInit = function() {
      console.log('UserController initialized');
      ctrl.getAllUsers();
    };

    ctrl.getAllUsers = function() {
      UserService.getAllUsers(getUsersSuccessCb, ErrorController.httpGetErroCb);
    }
    
    ctrl.formatRolesForView = function(userRoles) {
      var formattedRoles = [];
      for (var i = 0; i < userRoles.length; i++) {
        formattedRoles.push(userRoles[i].title);
      }
      return formattedRoles;
    };

    var getUsersSuccessCb = function(data, status, headers) {
        angular.forEach(data, function(user, index) {
          user.viewRoles = ctrl.formatRolesForView(user.roles);
        });
        $scope.users = data;
    }

    var getUsersErrorCb = function(data, status, headers) {
      //console.log(status);
    }

    $scope.addUser = function() {
      var modal = $uibModal.open({
        resolve: {
          currentUser: { roles: [] }
        },
        size: 'md',
        templateUrl: 'app/views/user-details.html',
        controller: UserModalController
      });
      modal.result.then(function(){
        ctrl.getAllUsers();
      }, ErrorController.httpGetErroCb);
    };

    $scope.editUser = function(user) {
      var userToEdit = user;
      var modal = $uibModal.open({
        resolve: {
          currentUser: angular.copy(userToEdit)
        },
        size: 'md',
        templateUrl: 'app/views/user-details.html',
        controller: UserModalController
      });
      modal.result.then(function(){
        ctrl.getAllUsers();
      }, ErrorController.httpGetErroCb);
    };

    $scope.deleteUser = function(user) {
      $scope.currentUser = user;
      var modal = $uibModal.open({
        scope: $scope,
        template: '<div class="modal-header"><h4 class="modal-title">Confirmation</h4></div>'+
                  '<div class="modal-body">Do you want to delete user "{{ currentUser.firstName }} {{ currentUser.lastName }}" ?</div>'+
                  '<div class="modal-footer">'+
                    '<button class="btn btn-small" ng-click="confirm(currentUser.id)">Yes</button>'+
                    '<button class="btn btn-small" ng-click="cancel()">No</button>'+
                  '</div>',
        controller: function($uibModalInstance, UserService, ErrorController) {

          $scope.confirm = function(id) {
            UserService.deleteUserById(id, function(){
              //console.log("Successfully deleted user");
              $uibModalInstance.close();
            }, ErrorController.httpGetErroCb);
          }
          
          $scope.cancel = function() {
            $uibModalInstance.dismiss('cancel');
          }
        }
      });

      modal.result.then(function(){
        ctrl.getAllUsers();
      }, ErrorController.httpGetErroCb);
    }
  }
  

  angular.module('app').controller('UserModalController', UserModalController);

  UserModalController.$inject = ['$scope', '$uibModalInstance', '$cookies', '$routeParams', '$location', 'currentUser', 'UserService'];
  function UserModalController($scope, $uibModalInstance, $cookies, $routeParams, $location, currentUser, UserService) {
    var ctrl = this;

    $scope.currentUser = currentUser;
    
    $scope.modalTitle = "";
    if (currentUser.id) {
      $scope.modalTitle = "Edit user";
    } else {
      $scope.modalTitle = "Add new user";
    }
    
    $scope.roles = [];
    
    $scope.confirmPassword = '';

    $scope.roleCheckedFilter = function(roleCode) {
      return $scope.currentUser.roles.filter(function(role) { return role.code == roleCode; }).length == 1;
    };
    
    $scope.toggleRole = function(role) {
      var idx = this.currentUser.roles.findIndex(function(r) { return r.code == role.code; })
      if ( idx === -1) {
        this.currentUser.roles.push(role);
      }else {
        this.currentUser.roles.splice(idx,1);
      }
      //console.log(role.code+" toggled");
    };
    
    ctrl.$onInit = function() {
      ctrl.getRoles();
    };
    
    ctrl.getRoles = function() {
      UserService.getRoles(function(data, status, headers){
        $scope.roles = data;
      }, function(){});
    }

    var saveUserErrorCb = function(data, status, headers) {
      $scope.errors = {};
      angular.forEach(data.errors, function(error, index){
        $scope.errors[error.field] = error.errorMessage;
      });
      $scope.errorMessage = data.errorMessage;
      if (500 == status) {
        $scope.errorMessage = 'Internal server error while processing the request';
      }
    }

    $scope.save = function(currentUser) {
      if (!ctrl.checkPasswordMatch(currentUser.password, this.confirmPassword)) {
        $scope.errors = {};
        $scope.errors['confirmPassword'] = "Confirmed password doesn't match";
        return;
      }
      //currentUser.roles = ctrl.formatRolesForJson(this.selectedRoles);
      if (currentUser.id) {
        UserService.editUser(currentUser, function() {
          //console.log("Successfully edited user");
          $uibModalInstance.close();
        }, saveUserErrorCb);
      } else {
        UserService.saveUser(currentUser, function() {
          //console.log("Successfully saved user");
          $uibModalInstance.close();
        }, saveUserErrorCb);
      }
    }

    ctrl.checkPasswordMatch = function(password, confirmedPassword) {
      return (!ctrl.isStringEmpty(password) == !ctrl.isStringEmpty(confirmedPassword)) || password == confirmedPassword;
    } 
    
    ctrl.isStringEmpty = function(s) {
      return (!s || 0 === s.length);
    }
    
    $scope.cancel = function() {
      $uibModalInstance.dismiss('cancel');
    }
  }
})();