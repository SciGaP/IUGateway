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
        $scope.selected = "MyJobs";
        $scope.phases=[{name:"MyJobs", id:1},{name:"ProteinX23 Project", id:2},{name:"ProteinScala Project", id:3}];

        $scope.onItemClick = function (phaseID) {
            $scope.selected = phaseID;
            $scope.jobForm = "static/amber/new"+$scope.selected+"Job.html"
        };


    }]).
    controller("TleapCtrl", ["$scope", "$routeParams", function ($scope, $routeParams) {
        $scope.job = {};
        $scope.job.id = $routeParams.jobId;
        console.log("In TleapCtrl");
        console.log($scope.job.id);
        console.log("Job ID: "+$routeParams);
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
        $scope.selected = "Tleap";
        $scope.phases=[{name:"Tleap", id:1},{name:"Amber", id:2},{name:"PostProcess", id:3}];
        $scope.jobForm ="static/amber/newTleapJob.html"

        $scope.onItemClick = function (phaseID) {
            $scope.selected = phaseID;
            $scope.jobForm = "static/amber/new"+$scope.selected+"Job.html"
        };

        $scope.createJob = function(step,expName,files){

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
    }]).
    controller("ModalDemoCtrl", function ($scope, $modal, $log) {

    $scope.items = ['item1', 'item2', 'item3'];

    $scope.open = function () {

        var modalInstance = $modal.open({
            templateUrl: 'static/amber/myModalContent.html',
            controller: ModalInstanceCtrl,
            resolve: {
                items: function () {
                    return $scope.items;
                }
            }
        });

        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };
}).

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

controller("ModalInstanceCtrl", function ($scope, $modalInstance, items) {

    $scope.items = items;
    $scope.selected = {
        item: $scope.items[0]
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selected.item);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
