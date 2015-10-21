'use strict';

angular.module('myApp.findmusic', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/findmusic', {
    templateUrl: '/public/app/findmusic/findmusic.html',
    controller: 'FindMusicCtrl'
  });
}])

.controller('FindMusicCtrl', ['$scope', '$http', function($scope, $http) {1
	
		$http.get('/recommend').
		success(function(data, status, headers, config) {
		  	$scope.songs = data.recmap;
		}).
		error(function(data, status, headers, config) {
		  console.error(status);
		});
		
		
		$http.get('/jobstats').
		success(function(data, status, headers, config) {
			$scope.jobData = data;
		}).
		error(function(data, status, headers, config) {
		  console.error(status);
		});
}]);