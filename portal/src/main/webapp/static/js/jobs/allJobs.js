var jobApp = angular.module("jobApp",["user","urlprovider"]);

jobApp.controller("JobCtrl",function($scope,$http,UrlProvider) {
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
			fetchBRData($scope,$http,"jobInfo"+url);
		} else {
			fetchData($scope,$http,"jobInfo"+url);
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
        $scope.jobs = getJobs(data.results);
        $scope.hideLoader = true;
	    var jobState = getURLParamValue('jobState');
	    if (jobState) {
	     	var filterText = {};
	       	filterText.state = jobState;
        	$scope.filterText = filterText;
	    }
    }).
    error(function(data,status) {
        $scope.hideLoader = true;
        $scope.showError = true;
    });
};

var fetchBRData = function($scope,$http,url) {
	$http({method: "GET", url: url, cache: true}).
    success(function(data,status) {
        $scope.jobs = getBRJobs(data.results);
        $scope.hideLoader = true;
        var jobState = getURLParamValue('jobState');
        if (jobState) {
           	var filterText = {};
           	filterText.state = jobState;
           	$scope.filterText = filterText;
        }
    }).
    error(function(data,status) {
        $scope.hideLoader = true;
        $scope.showError = true;
    });

};

$(document).ready(function() {
    setTimeout(function() {
        $("#jobLink").parent().addClass("active");
    },50);
});

var getJobs = function(results) {
    var jobs = [];
    for(var i in results) {
        var job = {};
        job.jobid = getValue(results[i].name);
        job.name = getValue(results[i].customName);
        job.username = getValue(results[i].credentials.user);
        job.state = getValue(results[i].states.state);
        job.startDate = getValue(results[i].dates.startDate);
        job.submitDate = getValue(results[i].dates.submitDate);
        job.walltime = secondsToString(results[i].duration);
        job.walltimesecs = results[i].duration;
        job.runtime = secondsToString(results[i].durationActive);
        job.runtimesecs = results[i].durationActive;
        job.queueWaittime = secondsToString(results[i].durationQueued);
        var nodes = [];
        var nodeCount = results[i].requirements[0].nodes.length;
        if (nodeCount!= 0){
            for(var j = 0 ; j < nodeCount; j++ ) {
                var nodeName = results[i].requirements[0].nodes[j].name;
                nodes.push(nodeName);
            }
        } else {
        	nodes.push("N/A");
        }
        job.nodes = nodes;
        jobs.push(job);
    }
    return jobs;
};

var getBRJobs = function(results) {
    var jobs = [];
    for (var i = 1; i < results.length; i++) {
        var job = {};
        if (results[i].startDate == 0){
            job.startDate = "N/A";
        }else{
            job.startDate = results[i].startDate;
        }
        if (results[i].customName == 0){
            job.name = "N/A";
        }else{
            job.name = results[i].customName;
        }
        if (results[i].runtime == ""){
            results[i].runtime = "0";
        }
        job.runtimesecs = parseInt(results[i].runtime);
        job.runtime = secondsToString(results[i].runtime);
        job.jobid = results[i].name;
        job.username = results[i].user;
        job.state = results[i].state;
        job.submitDate = results[i].submitDate;
        job.walltimesecs = parseInt(results[i].walltime);
        job.walltime = secondsToString(results[i].walltime);
        var nodes = [];
        nodes.push(results[i].nodes);
        job.nodes =  nodes;
        jobs.push(job);
    }
    return jobs;
};


var getValue = function(value) {
    return (value == undefined || value == null) ? "N/A" : value;
};