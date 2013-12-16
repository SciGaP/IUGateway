angular.module("amberControllers",["amberServices"]).
controller("JobListCtrl",["JobService","$scope",function(JobService,$scope){
	console.log("In JobListCtrl");
	
	var loadJobs = function() {
		JobService.getAllJobs().then(function(jobs) {
			$scope.jobs = jobs;
			console.log(jobs);
		});
	};
	
	loadJobs();
	$scope.refresh = function() {
		loadJobs();
	};
	$scope.fetchDetail = function(jobIndex, jobId) {
		JobService.fetchJob(jobId).then(function(job) {
			$scope.jobs[jobIndex].detail = job;
		});
	};
	
}]).
controller("TleapCtrl",["$scope","$routeParams",function($scope,$routeParams) {
	$scope.job = {};
	$scope.job.id = $routeParams.jobId;
	console.log("In TleapCtrl");
}]).
controller("AmberCtrl",["$scope","$routeParams",function($scope,$routeParams) {
	$scope.job = {};
	$scope.job.id = $routeParams.jobId;
	console.log("In AmberCtrl");
}]).
controller("PostProcessCtrl",["$scope","$routeParams",function($scope,$routeParams) {
	$scope.job = {};
	$scope.job.id = $routeParams.jobId;
	console.log("In PostProcessCtrl");
}]);
