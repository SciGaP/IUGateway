angular.module("amberServices",[]).
factory("JobService",["$http",function($http) {
	return {
		getAllJobs : function() {
			return $http({method:"GET", url:"amberCtrl/jobs/all", cache:false}).
				then(function(response) {
					return response.data;
				},function(response,status) {
					console.log("Error fetching all amber jobs !!");
					console.log(response);
					console.log(status);
				});
		},
		fetchJob : function(jobId) {
			return $http({method:"GET", url:"amberCtrl/jobs/"+jobId, cache:false}).
				then(function(response) {
					return response.data;
				}, function(response, status) {
					console.log("Error fetching job detail for Job Id "+jobId);
					console.log(response);
					console.log(status);
				});
		}
	};
}]);
