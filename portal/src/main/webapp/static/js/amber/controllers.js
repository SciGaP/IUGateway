angular.module("amberControllers", ["amberServices", "angularFileUpload"]).
    controller("JobListCtrl", ["JobService", "$scope", function (JobService, $scope) {
        console.log("In JobListCtrl");

        var loadJobs = function () {
            JobService.getAllJobs().then(function (jobs) {
                $scope.jobs = jobs;
                console.log(jobs);
            });
        };

        loadJobs();
        $scope.refresh = function () {
            loadJobs();
        };
        $scope.fetchDetail = function (jobIndex, jobId) {
            JobService.fetchJob(jobId).then(function (job) {
                $scope.jobs[jobIndex].detail = job;
            });
        };

    }]).
    controller("TleapCtrl", ["$scope", '$upload', "$routeParams", function ($scope, $routeParams, $upload) {
        $scope.job = {};
        $scope.job.id = $routeParams.jobId;
        console.log("In TleapCtrl");
    }]).
    controller("AmberCtrl", ["$scope", "$routeParams", function ($scope, $routeParams) {
        $scope.job = {};
        $scope.job.id = $routeParams.jobId;
        console.log("In AmberCtrl");
    }]).
    controller("PostProcessCtrl", ["$scope", "$routeParams", function ($scope, $routeParams) {
        $scope.job = {};
        $scope.job.id = $routeParams.jobId;
        console.log("In PostProcessCtrl");
    }]).
    controller("FileUploadController", [ '$scope', '$upload', function ($scope, $upload) {
        $scope.onFileSelect = function ($files) {
            //$files: an array of files selected, each file has name, size, and type.
            for (var i = 0; i < $files.length; i++) {
                var file = $files[i];
                $scope.upload = $upload.upload({
                    url: 'amberCtrl/uploadPDB/test', //upload.php script, node.js route, or servlet url
                    method: 'POST',
                    headers: {'headerKey': 'headerValue'}, withCredential: true,
                    data: {myObj: $scope.myModelObj},
                    file: file,
                    // file: $files, //upload multiple files, this feature only works in HTML5 FromData browsers
                    /* set file formData name for 'Content-Desposition' header. Default: 'file' */
                    fileFormDataName: 'myFile' //OR for HTML5 multiple upload only a list: ['name1', 'name2', ...]
                    /* customize how data is added to formData. See #40#issuecomment-28612000 for example */
                    //formDataAppender: function(formData, key, val){}
                }).progress(function (evt) {
                        console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                    }).success(function (data, status, headers, config) {
                        // file is uploaded successfully
                        console.log("File uploaded successfully")
                        console.log(data);

                    });
                //.error(...)
                //.then(success, error, progress);
            }
        };
    }]);
