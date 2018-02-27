(function () {
    'use strict';
    angular
      .module('app', ['ngRoute', 'ngCookies', 'ui.bootstrap'])
      .config([ '$routeProvider', function($routeProvider) {
        $routeProvider
        .when('/', {
            templateUrl: 'app/views/home.html',
            controller: 'HomeController'
        })
        .when('/login', {
            templateUrl: 'app/views/login.html',
            controller: 'LoginController'
        })
        .otherwise({
          redirectTo : '/login'
        });
      } ]).config([ '$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push('TokenAuthInterceptor');
        //$httpProvider.interceptors.push('HttpErrorInterceptor');
      } ]).factory('TokenAuthInterceptor', function($cookies) {
          return {
            request : function(config) {
              var authToken = $cookies.get('token');
              if (authToken) {
                  config.headers['Authorization'] = authToken;
              }
              return config;
            }
          };
     })
     /*
     .factory('HttpErrorInterceptor', function($rootScope, $q, $log, $location){
       return {
         responseError: function(response) {
           //var httpErrorMessage = "";
           if ("io.jsonwebtoken.ExpiredJwtException" == response.data.exception) {
             $rootScope.httpErrorMessage = "Your session expired. Please re-login";
           }
           return response;
         }
       }
     })
     /**/
})();