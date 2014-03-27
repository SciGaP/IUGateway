var fileManagerApp = angular.module("fileManagerApp",["user","urlprovider"]);

fileManagerApp.controller("FileManagerCtrl",function($scope,$http) {
    console.log("*******at controller*****")
    $http({method: "GET", url: "filemng/command/ls" , cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.files = data;
        }).
        error(function (data, status) {
            console.log("Error getting profile !");
        });

    $scope.upOneLevel = function(){
        var parent = "...";
        var url = "filemng/command/cd " + parent;
        console.log(url);
        $http({method: "GET", url: "filemng/command/cd " + parent, cache: false}).
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
            $http({method: "GET", url: "filemng/command/cd " + folderName , cache: false}).
                success(function (data, status) {
                    console.log(data);
                    $scope.files = data;
                }).
                error(function (data, status) {
                    console.log("Error while cd ing to folder !");
                });
        }
    }
    $scope.rename = function (path1, path2) {
        console.log("*******at rename controller*****")
        $http({method: "GET", url: "filemng/command/mv " + path1 +" " + path2, cache: false}).
            success(function (data, status) {
            }).
            error(function (data, status) {
                console.log("Error getting profile !");
            });

    }

    $scope.addFolder = function (folderName) {
        console.log(folderName);
        console.log("*******at mkdir controller*****")
        $http({method: "GET", url: "filemng/command/mkdir " + folderName, cache: false}).
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
        $scope.file
        console.log(file);
    }

    $scope.move = function (path1, path2) {
        console.log("*******at mv controller*****")

    }

    $scope.copy = function (path1, path2) {
        console.log("*******at copy controller*****")

    }

    $scope.delete = function () {
        console.log("*******at delete controller*****")
        $http({method: "GET", url: "filemng/command/rm -r", cache: false}).
            success(function (data, status) {
                $scope.disciplines = getDisciplines(data);
            }).
            error(function (data, status) {
                console.log("Error getting profile !");
            });

    }

});



