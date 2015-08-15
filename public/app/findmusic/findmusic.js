'use strict';

angular.module('myApp.findmusic', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/findmusic', {
    templateUrl: '/public/app/findmusic/findmusic.html',
    controller: 'FindMusicCtrl'
  });
}])

.controller('FindMusicCtrl', ['$scope', '$http', function($scope, $http) {
}]);