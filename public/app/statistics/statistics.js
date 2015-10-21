'use strict';

angular.module('myApp.statistics', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/statistics', {
    templateUrl: '/public/app/statistics/statistics.html',
    controller: 'StatisticsCtrl'
  });
}])
.directive('onLastRepeat', function() {
        return function(scope, element, attrs) {
            if (scope.$last) setTimeout(function(){
                scope.$emit('onRepeatLast', element, attrs);
            }, 1);
        };
})
.controller('StatisticsCtrl', ['$scope', '$http', '$q', function($scope, $http, $q) {

		$scope.job1name = 'sdgCF';
		$scope.job2name = 'sparkCF';

        $scope.activeJb = 'active';
        $scope.activeMse = '';
        var job1 = $http.get('/timeseries/sdgCF'),
            job2 = $http.get('/timeseries/sparkCF');

        var jobs = [job1, job2];

		$q.all(jobs).then(function(arrayOfResults) {
		    var graphsData = new Array();
            for(var i in arrayOfResults) {
                var theData = arrayOfResults[i].data;
                graphsData.push(theData);
            }

            $scope.graphsData = graphsData;
        });

        $scope.$on('onRepeatLast', function(scope, element, attrs){

            var graphsData = $scope.graphsData;
            for(var i in graphsData){
                drawGauge(graphsData[i], graphsData[i][0].id + 'gauge');
            }

            drawLineChart(graphsData, 'Job time(s)');

        });

        $scope.refreshDiagrams = function(diagramName){
            if(diagramName === 'Job time(s)'){
                $scope.activeJb = 'active';
                $scope.activeMse = '';
            }
            else{
                $scope.activeJb = '';
                $scope.activeMse = 'active';
            }
            drawLineChart($scope.graphsData, diagramName);
            console.log($scope.graphsData);
        };

		function drawLineChart(lineData, stat){

            document.getElementById("stat").innerText = stat;
            var job1Data = lineData[0];
            var job2Data = lineData[1];

            var data1 = new google.visualization.DataTable();
            data1.addColumn('date', 'Date');
            data1.addColumn('number', lineData[0][0].id);

            var data2 = new google.visualization.DataTable();
            data2.addColumn('date', 'Date');
            data2.addColumn('number', lineData[1][0].id);

            var rows1 = new Array();
            for(var i in job1Data){
                var d = job1Data[i];
                var mapData = d.metricsmap;
                if(mapData !== undefined){
                    rows1.push([new Date(d.timestamp), parseFloat(mapData[stat])]);
                }
            }

            data1.addRows(rows1);

            var rows2 = new Array();
            for(var x in lineData[1]){
                var d = job2Data[x];
                var mapData = d.metricsmap;
                if(mapData !== undefined){
                    rows2.push([new Date(d.timestamp), parseFloat(mapData[stat])]);
                }

            }

            data2.addRows(rows2);

            var chart1 = new google.visualization.LineChart(document.querySelector('#chart_div_1'));
            chart1.draw(data1, {
                height: 300,
                width: 800,
                series: {
                  0: { color: '#e2431e' },
                }
            });

            var chart2 = new google.visualization.LineChart(document.querySelector('#chart_div_2'));
            chart2.draw(data2, {
                height: 300,
                width: 800,
            });
		}

		function drawGauge(gaugeData, gaugeName){
			var system_loadavg = 0;
			var memory  = 0;
			var arrayLength = gaugeData.length;
            for (var i = 0; i < arrayLength; i++) {
            	var g_m = gaugeData[i].metricsmap;
                system_loadavg += parseFloat(g_m['system-loadavg']);
                memory += 1 - ((g_m['mem-total'] - g_m['mem-avail'] ) / g_m['mem-total']);
            }


			var data1 = google.visualization.arrayToDataTable([
			  ['Label', 'Value'],
			  ['Load', Number(parseFloat(system_loadavg / arrayLength).toFixed(2))],
			]);

			var data2 = google.visualization.arrayToDataTable([
              ['Label', 'Value'],
              ['Memory', parseInt((memory / arrayLength) * 100)],
            ]);


            var options1 = {
                width: 120, height: 120,
                min:0, max:10,
                redFrom: 9, redTo: 10,
                yellowFrom:7.5, yellowTo: 9,
                minorTicks: 5
             };


            var options2 = {
                 width: 120, height: 120,
                 redFrom: 90, redTo: 100,
                 yellowFrom:75, yellowTo: 90,
                 minorTicks: 5
            };

			var chart1 = new google.visualization.Gauge(document.getElementById(gaugeName + '1'));
			chart1.draw(data1, options1);

            var chart2 = new google.visualization.Gauge(document.getElementById(gaugeName + '2'));
            chart2.draw(data2, options2);
		}
}]);
