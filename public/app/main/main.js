'use strict';

angular.module('myApp.main', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/main', {
    templateUrl: '/public/app/main/main.html',
    controller: 'MainCtrl'
  });
}])

.controller('MainCtrl', ['$scope', '$http', function($scope, $http) {
	$http.get('/playlists').
    success(function(data, status, headers, config) {
      	$scope.playlists = data;
    }).
    error(function(data, status, headers, config) {
      console.error(status);
    });
}]);