var fileManagerApp = angular.module("fileManagerApp",["user","urlprovider"]);

fileManagerApp.controller("FileManagerCtrl",function($scope,$http) {
    $scope.hideLoader = true;
    $http({method: "GET", url: "getRemoteUser" , cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.remoteUser = data;
        }).
        error(function (data, status) {
        });
    $http({method: "GET", url: "filemanager/getPortalUrl" , cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.portalUrl = data;
        }).
        error(function (data, status) {
            console.log("Error getting home directory !");
        });
    $http({method: "GET", url: "filemanager/usedSpace", cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.totalSize = data;

        }).
        error(function (data, status) {
        });
    $http({method: "GET", url: "filemanager/fileCount", cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.totalfiles = data;

        }).
        error(function (data, status) {
        });
    $http({method: "GET", url: "filemanager/getPwd" , cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.pwd = data;
        }).
        error(function (data, status) {
            console.log("Error getting current working directory !");
        });
    $http({method: "GET", url: "filemanager/getHome" , cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.home = data;
        }).
        error(function (data, status) {
            console.log("Error getting home directory !");
        });
    $http({method: "GET", url: "filemanager/command/ls", cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.files = data;
            $scope.hideLoader = false;
            $http({method: "GET", url: "filemanager/getPwd" , cache: false}).
                success(function (data, status) {
                    console.log(data);
                    $scope.pwd = data;
                }).
                error(function (data, status) {
                    console.log("Error getting current working directory !");
                });
        }).
        error(function (data, status) {
            $scope.hideLoader = false;
            $scope.showError = true;
    });

    $scope.upOneLevel = function(){
        $scope.hideLoader = true;
        var parent = "..";
        var url = "filemanager/command/cd " + parent;
        $http({method: "GET", url: "filemanager/command/cd " + parent, cache: false}).
            success(function (data, status) {
                $scope.files = data;
                $scope.hideLoader = false;
                $http({method: "GET", url: "filemanager/getPwd" , cache: false}).
                    success(function (data, status) {
                        console.log(data);
                        $scope.pwd = data;
                    }).
                    error(function (data, status) {
                        console.log("Error getting current working directory !");
                    });
            }).
            error(function (data, status) {
                $scope.hideLoader = false;
                console.log("Error getting files !");
            });
    }

    $scope.goInside = function(file){
        if (!file.file){
            $scope.hideLoader = true;
            var folderName = file.name;
            $http({method: "GET", url: "filemanager/command/cd " + folderName , cache: false}).
                success(function (data, status) {
                    $scope.hideLoader = false;
                    $scope.files = data;
                    $http({method: "GET", url: "filemanager/getPwd" , cache: false}).
                        success(function (data, status) {
                            console.log(data);
                            $scope.pwd = data;
                        }).
                        error(function (data, status) {
                            console.log("Error getting current working directory !");
                        });
                }).
                error(function (data, status) {
                    $scope.hideLoader = false;
                    console.log("Error while cd ing to folder !");
                });
        } else{
            window.location = "filemanager/download/ " + file.name;
        }
    }

    $scope.addFolder = function (folderName) {
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

    $scope.generateDeleteModel = function () {
        var selectedFiles = getCheckedFiles($scope.files);
        $scope.selectedFiles = selectedFiles;
        if (selectedFiles.length == 0){
            var error = "<div class='alert alert-error' ng-show='true'><button type='button' class='close' data-dismiss='alert'>&times;</button>Please select files to delete...</div>";
            $('#deleteModel').show();
            $('#deletefiles').show().html(error);
        } else {
            var content = "<div id='deleteDiv'>";
            for (var j = 0; j < selectedFiles.length ; j++){
                if (selectedFiles[j].file){
                    content += "<p>Will delete the file " + selectedFiles[j].name + "</p>";
                } else{
                    content += "<p>Will delete the folder " + selectedFiles[j].name + " and its content.</p>";
                }
            }
            content += "</div>";
            $('#deleteModel').show();
            $('#deletefiles').show().html(content);
        }
    }

    $scope.freedisk = function () {
        $http({method: "GET", url: "filemanager/usedSpace", cache: false}).
            success(function (data, status) {
            }).
            error(function (data, status) {
                console.log("Error getting profile !");
            });
    }

    $scope.viewUsedSpace = function () {
        $http({method: "GET", url: "filemanager/fileCount", cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.totalfiles = data;
                $http({method: "GET", url: "filemanager/usedSpace", cache: false}).
                    success(function (data, status) {
                        console.log(data);
                        $scope.totalSize = data;
                        var content = "<p>Number of files for user " + $scope.remoteUser + " : " + $scope.totalfiles + "</p>";
                        content += "<p>Used space for user " + $scope.remoteUser + " : " + $scope.totalSize  + "</p></br>";
                        $('#viewUsedSpaceModel').show();
                        $('#fileCount').show().html(content);
                    }).
                    error(function (data, status) {
                    });
            }).
            error(function (data, status) {
            });
    }

    $scope.generateRenameModel = function () {
        var selectedFiles = getCheckedFiles($scope.files);
        $scope.selectedFiles = selectedFiles;
        if (selectedFiles.length == 0){
            var error = "<div class='alert alert-error' ng-show='true'><button type='button' class='close' data-dismiss='alert'>&times;</button>Please select files to rename...</div>";
            $('#renameModel').show();
            $('#renameFile').show().html(error);
        } else {
            var content = "<div id='renameDiv'>";
            for (var j = 0; j < selectedFiles.length ; j++){
                content += "<div class='row-fluid'><div class='span4'><strong>Rename " + selectedFiles[j].name + " to</strong></div><div><input id='" + selectedFiles[j].name + "' type='text' value='"+ selectedFiles[j].name + "' name='newName" + j +  "' ng-model='newName" + j + "'/></div></div>";
            }
            content += "</div>";
            $('#renameModel').show();
            $('#renameFile').show().html(content);
        }
    }

    //renaming a file or a folder
    $scope.rename = function () {
        $('#renameDiv :input').each(function () {
            console.log(this.value);
            $scope.found = false;
            for (var i = 0; i < $scope.files.length; i++) {
                if (this.value == $scope.files[i].name) {
                    $scope.found = true;
                }
            }
            if ($scope.found == false){
                var pwd = $scope.pwd.replace(/\//g, '*');
                $http({method: "GET", url: "filemanager/command/mv " + $(this).attr('id') + "*" + pwd + "*" + this.value, cache: false}).
                    success(function (data, status) {
                        $scope.files = data;
                        $scope.renameSuccess = true;
                    }).
                    error(function (data, status) {
                        $scope.renameDisabled = true;
                        console.log("Error while renaming object !");
                    });
            }
        });
    }

    $scope.generateMvModel = function () {
        $scope.selectOther = false;
        $scope.selectHome = false;
        $scope.selectedFiles = getCheckedFiles($scope.files);
        var fileNames = getCheckedFileNames($scope.selectedFiles);
        var files =  $scope.files;
        var homeFolders = [];
        var folders = [];
        $http({method: "GET", url: "filemanager/command/ls ~", cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.homeFiles = data;
                var homeFiles =  $scope.homeFiles;
                for (var i=0; i < homeFiles.length; i++){
                    if (!homeFiles[i].file ){
                        var found  = $.inArray(homeFiles[i].name, fileNames);
                        console.log(found);
                        if (found == -1){
                            homeFolders.push(homeFiles[i]);
                        }
                    }
                }
                $scope.homeFoldername = homeFolders[0];
                $scope.homeFolders = homeFolders;
                for (var j=0; j < files.length; j++){
                    if (!files[j].file ){
                        var found  = $.inArray(files[j].name, fileNames);
                        console.log(found);
                        if (found == -1){
                            folders.push(files[j]);
                        }
                    }
                }
                var homeFile = {};
                homeFile.name = "Select from Home";
                folders.push(homeFile);
                var otherFile = {};
                otherFile.name = "Other";
                folders.push(otherFile);
//                $scope.foldername = folders[0];
                $scope.folders = folders;
            }).
            error(function (data, status) {
                $scope.showError = true;
            });

    }

    //todo: if it's a directory, it should be mvr not mv
    //moving a file
    $scope.move = function (targetFolder , customFolderName, homeFolder) {
        var selectedFiles = getCheckedFiles($scope.files);
        var fileName;
        var home = $scope.home.replace(/\//g, '*');
        var pwd = $scope.pwd.replace(/\//g, '*');
        if (targetFolder.name == "Other"){
            if (customFolderName.contains("/")){
                customFolderName = customFolderName.replace(/\//g, '*');
                fileName = home + "*" + customFolderName;
            }
        }else if (targetFolder.name == "Select from Home"){
            fileName = home + "*" + homeFolder.name;
        } else {
            fileName = pwd + "*" + targetFolder.name;
        }
        for (var i = 0; i < selectedFiles.length; i++){
            $http({method: "GET", url: "filemanager/command/mv " + selectedFiles[i].name +"*" + fileName, cache: false}).
                success(function (data, status) {
                    console.log(data);
                    $scope.files = data;
                    $scope.mvSuccess = true;
                }).
                error(function (data, status) {
                    console.log("Error getting files !");
                    $scope.mvDisabled = true;
                });
        }
    }

    $scope.generateCpModel = function () {
        $scope.selectOther = false;
        $scope.selectHome = false;
        $scope.selectedFiles = getCheckedFiles($scope.files);
        var fileNames = getCheckedFileNames($scope.selectedFiles);
        var files =  $scope.files;
        var homeFolders = [];
        var folders = [];
        $http({method: "GET", url: "filemanager/command/ls ~", cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.homeFiles = data;
                var homeFiles =  $scope.homeFiles;
                for (var i=0; i < homeFiles.length; i++){
                    if (!homeFiles[i].file ){
                        var found  = $.inArray(homeFiles[i].name, fileNames);
                        console.log(found);
                        if (found == -1){
                            homeFolders.push(homeFiles[i]);
                        }
                    }
                }
                $scope.homeFoldername = homeFolders[0];
                $scope.homeFolders = homeFolders;
                for (var j=0; j < files.length; j++){
                    if (!files[j].file ){
                        var found  = $.inArray(files[j].name, fileNames);
                        console.log(found);
                        if (found == -1){
                            folders.push(files[j]);
                        }
                    }
                }
                var homeFile = {};
                homeFile.name = "Select from Home";
                folders.push(homeFile);
                var currentFolder = {};
                currentFolder.name = "To Current Folder";
                folders.push(currentFolder);
                var otherFile = {};
                otherFile.name = "Other";
                folders.push(otherFile);
//                $scope.foldername = folders[0];
                $scope.folders = folders;
            }).
            error(function (data, status) {
                $scope.showError = true;
            });
    }

    $scope.populateRest = function(folerName){
        console.log(folerName.name);
        $scope.selectOther = false;
        $scope.selectHome = false;
        if (folerName.name == "Other"){
             $scope.selectOther = true;
        }else if (folerName.name == "Select from Home"){
            $scope.selectHome = true;
        }
    }

    $scope.copy = function (targetFolder , customFolderName, homeFolder) {
        var selectedFiles = getCheckedFiles($scope.files);
        var fileName;
        var home = $scope.home.replace(/\//g, '*');
        var pwd = $scope.pwd.replace(/\//g, '*');
        if (targetFolder.name == "Other"){
            if (customFolderName.contains("/")){
                customFolderName = customFolderName.replace(/\//g, '*');
                fileName = home + "*" +customFolderName;
            }
        }else if (targetFolder.name == "Select from Home"){
            fileName = home + "*" + homeFolder.name;
        } else {
            fileName = pwd + "*" + targetFolder.name;
        }
        for (var i = 0; i < selectedFiles.length; i++){
            if (selectedFiles[i].name == targetFolder.name){
                fileName =  pwd + "*" + "Copy_" + targetFolder.name;
            } else if (targetFolder.name == "To Current Folder"){
                    fileName = pwd + "*" + "Copy_" + selectedFiles[i].name;
            }
            $http({method: "GET", url: "filemanager/command/cpr " + selectedFiles[i].name +"*" + fileName, cache: false}).
                success(function (data, status) {
                    console.log(data);
                    $scope.files = data;
                    $scope.cpSuccess = true;
                }).
                error(function (data, status) {
                    console.log("Error copying files !");
                    $scope.cpDisabled = true;
                });
        }
    }
    //deleting a file
    $scope.deleteFile = function () {
        console.log("*******at delete controller*****");
        var fileNames = getCheckedFiles($scope.files);
        for (var j = 0; j < fileNames.length ; j++){
            if (fileNames[j] != null || fileNames[j] != undefined ){
                $http({method: "GET", url: "filemanager/command/rm " + fileNames[j].name, cache: false}).
                    success(function (data, status) {
                        $scope.files = data;
                        $scope.deleteSuccess = true;
                    }).
                    error(function (data, status) {
                        $scope.deleteDisabled = true;
                    });
            }
        }
    }

    $scope.uploadFile = function (file) {
        console.log("*******at upload controller*****");
        console.log(file);
    }
});


var getCheckedFiles = function(files) {
    var fileNames = [];
    for (var i = 0; i < files.length; i++) {
        if (files[i].checked) {
            fileNames.push(files[i]);
        }
    }
    return fileNames;
}

var getCheckedFileNames = function(files) {
    var fileNames = [];
    for (var i = 0; i < files.length; i++) {
        fileNames.push(files[i].name);
    }
    return fileNames;
}



