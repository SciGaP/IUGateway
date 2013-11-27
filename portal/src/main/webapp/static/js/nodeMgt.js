var nodeApp = angular.module("nodeApp",["user","urlprovider"]);

nodeApp.controller("NodeCtrl",function($scope,$http,UrlProvider) {
	var machine = getURLParamValue('machine');
	var url = UrlProvider.getUrlForMachine(machine);
	var name = UrlProvider.getNameForMachine(machine);
	if(url == undefined || name == undefined) {
		$scope.machineName = "Unrecognized machine";
		$scope.hideLoader = true;
        $scope.showError = true;
	} else {
		// Special case for Big Red as it does not use MWS
		if(machine=="bigred") {
			fetchBRData($scope,$http,"nodeInfo"+url);
		} else {
			fetchData($scope,$http,"nodeInfo"+url);
		}
		$scope.machineName = name;
	}
	$scope.showDetails = function(item) {
		$scope.item = item;
	};
});

var fetchData = function($scope,$http,url) {
    $http({method: "GET", url: url, cache: true}).
        success(function(data,status) {
            $scope.nodes = getNodes(data.results);
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

var fetchBRData = function($scope,$http,url) {
    $http({method: "GET", url: url, cache: true}).
        success(function(data,status) {
            $scope.nodes = getBRNodes(data.results);
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

var getNodes = function(results) {
    var nodes = [];
    for(var i in results) {
        var node = {};
        node.name = getValue(results[i].name);
        node.state = getValue(results[i].states.state);
        node.classes = getValue(results[i].classes);
        node.featuresReported = getValue(results[i].featuresReported);
        node.cpuLoad = getValue(results[i].metrics.cpuLoad);
        node.partition = getValue(results[i].partition);
        node.processorsReal = getValue(results[i].resources.processors.real);
        node.processorsAvailable = getValue(results[i].resources.processors.available);
        node.memoryReal = getValue(results[i].resources.memory.real);
        node.memoryAvailable = getValue(results[i].resources.memory.available);
        node.diskConfigured = getValue(results[i].resources.disk.configured);
        node.diskAvailable = getValue(results[i].resources.disk.available);
        node.os = getValue(results[i].operatingSystem.image);
        nodes.push(node);
    }
    return nodes;
};

var getBRNodes = function(results) {
	// Removing the first element of the result. It contains just a value "default" which is not useful
	results.shift();
	console.log(results);
    var nodes = [];
    for(var i in results) {
        var node = {};
        node.name = getValue(results[i].name);
        node.state = getValue(results[i].state);
        node.classes = getValue(results[i].classes);
        node.featuresReported = getValue(results[i].featuresReported);
        node.cpuLoad = "N/A";
        node.partition = getValue(results[i].partition);
        node.processorsReal = getValue(results[i].resources.processors.real);
        node.processorsAvailable = getValue(results[i].resources.processors.available);
        node.memoryReal = getValue(results[i].resources.memory.real);
        node.memoryAvailable = getValue(results[i].resources.memory.available);
        node.diskConfigured = getValue(results[i].resources.disk.real);
        node.diskAvailable = getValue(results[i].resources.disk.available);
        node.os = getValue(results[i].os);
        nodes.push(node);
    }
    return nodes;
};

var getValue = function(value) {
    return (value == undefined || value == null) ? "N/A" : value;
};
