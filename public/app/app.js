'use strict';

google.load('visualization', '1', {
  packages: ['corechart', 'gauge', 'line', 'annotationchart']
});

google.setOnLoadCallback(function() {
  angular.bootstrap(document.body, ['myApp']);
});


// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
  'myApp.main',
  'myApp.statistics',
  'myApp.findmusic'
 ]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/main'});
}]);
