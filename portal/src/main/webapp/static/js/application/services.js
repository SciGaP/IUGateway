angular.module("appServices",[]).
factory("JobService",["$http",function($http) {
	return {
		getAllJobs : function() {
			return $http({method:"GET", url:"amberCtrl/allJobs", cache:false}).
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
		},
        newJob : function(step,expName,files) {
            return $http({method:"GET", url:"amberCtrl/newExperiment/", cache:false}).
                then(function(response) {
                    return response.data;
                }, function(response, status) {
                    console.log("Error fetching job detail for Job Id "+jobId);
                    console.log(response);
                    console.log(status);
                });
        },
        uploadFile: function (file,jobID, callback) {
            $http.uploadFile({
                url: "amberCtrl/uploadPDB/"+jobID,
                file: file
            }).progress(function(event) {
                    console.log('percent: ' + parseInt(100.0 * event.loaded / event.total));
                }).error(function (data, status, headers, config) {
                    console.error('Error uploading file')
                    callback(status);
                }).then(function(data, status, headers, config) {
                    callback(null);
                });
        }
	};
}]);
