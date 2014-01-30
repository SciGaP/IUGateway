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
    controller("TleapCtrl", ["$scope", '$upload', "$routeParams", function ($scope, $routeParams) {
        $scope.job = {};
        $scope.job.id = $routeParams.jobId;
        console.log("In TleapCtrl");
        console.log($scope.job.id);
        console.log("Job ID: "+$routeParams.jobId);
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
    controller("NewJobCtrl", ["$scope", "$routeParams", function ($scope, $routeParams) {
        $scope.job = {};
        $scope.job.id = $routeParams.jobId;
        $scope.selected = "Select the phase";
        $scope.phases=[{name:"Tleap", id:1},{name:"Amber", id:2},{name:"PostProcess", id:3}];
        $scope.jobForm ="static/amber/newTleapJob.html"

        $scope.onItemClick = function (phaseID) {
            $scope.selected = phaseID;
            $scope.jobForm = "static/amber/new"+$scope.selected+"Job.html"
        };

        console.log($scope.selected);
        console.log("In New Job Controller ...");
    }]).
    controller("FileUploadController", [ '$scope', '$upload', function ($scope, $upload) {
        $scope.onFileSelect = function ($files) {
            //$files: an array of files selected, each file has name, size, and type.
            for (var i = 0; i < $files.length; i++) {
                var file = $files[i];
                $scope.upload = $upload.upload({
                    url: 'amberCtrl/uploadPDB/test', //upload.php script, node.js route, or servlet url
                    method: 'POST',
                    transformRequest: angular.identity,
                    headers: {'Content-Type': 'multipart/form-data'}, withCredential: true,
                    data: {file: file},
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
