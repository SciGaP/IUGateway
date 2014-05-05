var fileManagerApp = angular.module("fileManagerApp",["user","urlprovider"]);

fileManagerApp.controller("FileManagerCtrl",function($scope,$http) {
    console.log("*******at controller*****");
    $scope.hideLoader = true;
    console.log($scope.hideLoader);
    $http({method: "GET", url: "getRemoteUser" , cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.remoteUser = data;
        }).
        error(function (data, status) {
            console.log("Error getting remote user !");
        });
    $http({method: "GET", url: "filemanager/command/ls", cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.files = data;
            $scope.hideLoader = false;
            console.log($scope.hideLoader);
        }).
        error(function (data, status) {
            console.log("Error listing the files !");
            $scope.hideLoader = true;
            $scope.showError = true;
            console.log($scope.hideLoader);
    });

    $scope.upOneLevel = function(){
        var parent = "..";
        var url = "filemanager/command/cd " + parent;
        console.log(url);
        $http({method: "GET", url: "filemanager/command/cd " + parent, cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.files = data;
            }).
            error(function (data, status) {
                console.log("Error getting files !");
            });
    }

    $scope.goInside = function(file){
        if (!file.file){
            var folderName = file.name;
            $http({method: "GET", url: "filemanager/command/cd " + folderName , cache: false}).
                success(function (data, status) {
                    console.log(data);
                    $scope.files = data;
                }).
                error(function (data, status) {
                    console.log("Error while cd ing to folder !");
                });
        }
    }

    $scope.addFolder = function (folderName) {
        console.log(folderName);
        console.log("*******at mkdir controller*****")
        $http({method: "GET", url: "filemanager/command/mkdir " + folderName, cache: false}).
            success(function (data, status) {
                $scope.files = data;
                $scope.createSuccess = true;
                $scope.foldername = null;
            }).
            error(function (data, status) {
                $scope.createDisabled = true;
            });
    }

    $scope.generateDeleteModel = function (file) {
        console.log("delete model");
        console.log(file);
    }



    // --------------- newly added capabilities - to be tested
    //todo: you have to check if the first parameter of a call is a directory or not
    //reason: for a file it's mv for move, but for a folder it's mv -r

    //getting the free disk space of the current directory
    $scope.freedisk = function () {
        console.log("*******at delete controller*****")
        $http({method: "GET", url: "filemanager/command/freedisk", cache: false}).
            success(function (data, status) {
                $scope.freedisk = data.ifree;
            }).
            error(function (data, status) {
                console.log("Error getting profile !");
            });

    }

    $scope.viewUsedSpace = function () {
        console.log("*******at used space controller*****");
        var numberOfFiles = $scope.files.length;
        var totalSize = 0;
        for (var i = 0 ; i < numberOfFiles; i ++){
            totalSize = +totalSize  +  +$scope.files[i].size;
        }
	console.log(totalSize);
	var sizeInMb = totalSize / 1000;
        var content = "<p>Number of files for user " + $scope.remoteUser + " : " + numberOfFiles + "</p>";
        content += "<p>Used space for user " + $scope.remoteUser + " : " + sizeInMb.toFixed(2) + "MB</p></br>";
        $('#viewUsedSpaceModel').show();
        $('#fileCount').show().html(content);
    }

    $scope.generateRenameModel = function () {
        var fileNames = getCheckedFiles($scope.files);
        var content = "";
        for (var j = 0; j < fileNames.length ; j++){
            content += "<div class=\"row-fluid\"><div class=\"span4\"><strong>Rename " + fileNames[j] + " to</strong></div><div><input type=\"text\" name=\"newName\"" + j +  " ng-model=\"newName\"" + j + "/></div></div>";
        }
        $('#renameModel').show();
        $('#renameFile').show().html(content);
    }

    //renaming a file or a folder
    $scope.rename = function (path1, path2) {
        console.log("*******at rename controller*****")
        $http({method: "GET", url: "filemanager/command/mv " + path1 +"*" + path2, cache: false}).
            success(function (data, status) {
            }).
            error(function (data, status) {
                console.log("Error getting profile !");
            });

    }

    //todo: if it's a directory, it should be mvr not mv
    //moving a file
    $scope.moveFile = function (file, path) {
        console.log("*******at mvFile controller*****")
        $http({method: "GET", url: "filemanager/command/mv " + file +"*" + path, cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.files = data;
            }).
            error(function (data, status) {
                console.log("Error getting files !");
            });

    }
    //moving a directory
    $scope.moveFolder = function (folder, path) {
        console.log("*******at mvFolder controller*****")
        $http({method: "GET", url: "filemanager/command/mv " + folder +"*" + path, cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.files = data;
            }).
            error(function (data, status) {
                console.log("Error getting files !");
            });
    }

    //deleting a file
    $scope.deleteFile = function () {
        console.log("*******at delete controller*****");
        var fileNames = getCheckedFiles($scope.files);
        for (var j = 0; j < fileNames.length ; j++){
            if (fileNames[j] != null || fileNames[j] != undefined ){
                $http({method: "GET", url: "filemanager/command/rm -rf " + fileNames[j], cache: false}).
                    success(function (data, status) {
                        $scope.files = data;
                        $scope.deleteSuccess = true;
                    }).
                    error(function (data, status) {
                        console.log("Error getting files !");
                        $scope.deleteDisabled = true;
                    });
            }
        }
    }

    //deleting a file
    $scope.deleteFolder = function (folder) {
        console.log("*******at delete controller*****")
        $http({method: "GET", url: "filemanager/command/rm -rf " + folder, cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.files = data;
            }).
            error(function (data, status) {
                console.log("Error getting files !");
            });

    }


    $scope.copyFile = function (file, path) {
        console.log("*******at copy controller*****")
        console.log("*******at mvFolder controller*****")
        $http({method: "GET", url: "filemanager/command/cp " + file +"*" + path, cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.files = data;
            }).
            error(function (data, status) {
                console.log("Error getting files !");
            });

    }

    $scope.copyFolder = function (folder, path) {
        console.log("*******at copy controller*****")
        console.log("*******at mvFolder controller*****")
        $http({method: "GET", url: "filemanager/command/mv " + folder +"*" + path, cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.files = data;
            }).
            error(function (data, status) {
                console.log("Error getting files !");
            });

    }
});

var getCheckedFiles = function(files) {
    var fileNames = [];
    for (var i = 0; i < files.length; i++) {
        if (files[i].checked) {
            fileNames.push(files[i].name);
        }
    }
    console.log(fileNames);
    return fileNames;
}



