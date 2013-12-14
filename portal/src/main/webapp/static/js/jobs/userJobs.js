var userJobApp = angular.module("userJobApp",["user","urlprovider"]);

var UserJobCtrl = userJobApp.controller("UserJobCtrl",function($scope,$http,UrlProvider) {
	$scope.jobs = [];
	fetchData($scope,$http,"jobInfo/bigred2/user","Big Red II");
	fetchData($scope,$http,"jobInfo/mason/user","Mason");
	fetchData($scope,$http,"jobInfo/quarry/user","Quarry");
	$scope.hideLoader = true;
	$scope.showDetails = function(item) {
		$scope.item = item;
	};
	
	$scope.cancelJob = function(job) {
		var machineUrl = UrlProvider.getUrlForMachine(job.machine);
		$http({method:"DELETE", url: "jobInfo"+machineUrl+"/"+job.jobid, cache:false}).
		success(function(data,status) {
			$scope.item.cancelSuccess = true;
		}).
		error(function(data,status) {
			$scope.item.cancelDisabled = true;
		});
	};
	$scope.holdJob = function(job) {
		var machineUrl = UrlProvider.getUrlForMachine(job.machine);
		$http({method:"PUT", url: "jobInfo"+machineUrl+"/"+job.jobid+"/hold", cache:false}).
		success(function(data,status) {
			$scope.item.holdSuccess = true;
		}).
		error(function(data,status) {
			$scope.item.holdDisabled = true;
		});
	};
	$scope.unholdJob = function(job) {
		var machineUrl = UrlProvider.getUrlForMachine(job.machine);
		$http({method:"PUT", url: "jobInfo"+machineUrl+"/"+job.jobid+"/unhold", cache:false}).
		success(function(data,status) {
			$scope.item.unholdSuccess = true;
		}).
		error(function(data,status) {
			$scope.item.unholdDisabled = true;
		});
	};
});


var fetchData = function($scope,$http,url,machine) {
	$http({method: "GET", url: url, cache: true}).
    success(function(data,status) {
    	var jobs = getJobs(data.results,machine);
        for(var i in jobs) {
        	$scope.jobs.push(jobs[i]);
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

var getJobs = function(results,machine) {
    var jobs = [];
    for(var i in results) {
        var job = {};
        job.jobid = getValue(results[i].name);
        job.name = getValue(results[i].customName);
        job.username = getValue(results[i].credentials.user);
        job.machine = machine;
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


var getValue = function(value) {
    return (value == undefined || value == null) ? "N/A" : value;
};