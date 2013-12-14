var reservationApp = angular.module("reservationApp",["user","urlprovider"]);

reservationApp.controller("ResvCtrl",function($scope,$http,UrlProvider) {
	var machine = getURLParamValue('machine');
	var url = UrlProvider.getUrlForMachine(machine);
	var name = UrlProvider.getNameForMachine(machine);
	if(url == undefined || name == undefined) {
		$scope.machineName = "Unrecognized machine";
		$scope.hideLoader = true;
        $scope.showError = true;
	} else {
		fetchData($scope,$http,"reservation"+url);
		$scope.machineName = name;
	}
	$scope.showDetails = function(item) {
		$scope.item = item;
	};
});

var fetchData = function($scope,$http,url) {
	$http({method: "GET", url: url, cache: true}).
	success(function(data,status) {
		$scope.reservations = data.results;
		$scope.hideLoader = true;
	}).
	error(function(data,status) {
		$scope.hideLoader = true;
		$scope.showError = true;
	});
	
	$scope.showDetails = function(item) {
		$scope.item = item;
	};
};

$(document).ready(function() {
	setTimeout(function() {
		$("#jobLink").parent().addClass("active");
	},50);
});