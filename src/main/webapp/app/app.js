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
        .when('/privacypolicy', {
            templateUrl: 'app/views/privacypolicy.html',
            controller: 'PrivacypolicyController'
        })
        .when('/login', {
            templateUrl: 'app/views/login.html',
            controller: 'LoginController'
        })
        .when('/googlelogin', {
            redirectTo : '/'
        })
        .otherwise({
          redirectTo : '/login'
        });
      } ]).config([ '$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push('TokenAuthInterceptor');
        //$httpProvider.interceptors.push('GoogleLoginInterceptor');
        //$httpProvider.interceptors.push('RedirectInterceptor');
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
    .factory('GoogleLoginInterceptor', function($rootScope, $q, $log, $location){
      return {
        request : function(request) {
          //console.log("Request");
          return request;
        },
        response: function(response) {
          console.log("Response status : " + response.status);
          return response;
        }
      };
    })
    .factory('RedirectInterceptor', function($rootScope, $q, $log, $location){
      return {
        request : function(request) {
          //console.log("Request");
          return request;
        },
        response: function(response) {
          console.log("Response");
          return response;
        }
      };
    })
    /**/
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