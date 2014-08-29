var fileManagerApp = angular.module("fileManagerApp",["user","urlprovider"]);

fileManagerApp.controller("FileManagerCtrl",function($scope,$http) {
    $scope.hideLoader = true;
    $scope.addFolderinitial =  [
        {
            folderExist : false,
            createSuccess : false,
            createDisabled : false,
            foldername : "",
            folderExistMsg : "",
            successMsg : "",
            errorMsg : ""
        }
    ];
    $scope.renameinitial =  [
        {
            folderExist : false,
            renameSuccess : false,
            renameDisabled : false,
            folderExistMsg : "",
            successMsg : "",
            errorMsg : ""
        }
    ];
    $scope.deleteinitial =  [
        {
            deleteSuccess : false,
            deleteDisabled : false,
            successMsg : "",
            errorMsg : ""
        }
    ];
    $scope.mvinitial =  [
        {
            moveSuccess : false,
            moveDisabled : false,
            successMsg : "",
            errorMsg : ""
        }
    ];
    $scope.cpinitial =  [
        {
            copySuccess : false,
            copyDisabled : false,
            successMsg : "",
            errorMsg : ""
        }
    ];
    $scope.addFolderdatas = angular.copy($scope.addFolderinitial);
    $scope.renamedatas =  angular.copy($scope.renameinitial);
    $scope.deletedatas =  angular.copy($scope.deleteinitial);
    $scope.mvdatas =  angular.copy($scope.mvinitial);
    $scope.cpdatas =  angular.copy($scope.cpinitial);
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
        $scope.addFolderdatas.folderExist = false;
        for (var i = 0; i < $scope.files.length; i++) {
            if (folderName == $scope.files[i].name) {
                $scope.addFolderdatas.folderExist = true;
                $scope.addFolderdatas.foldername = "";
                $scope.addFolderdatas.folderExistMsg = folderName + " already exists. Please provide a different name...";
            }
        }
        if ($scope.addFolderdatas.folderExist == false) {
            $http({method: "GET", url: "filemanager/command/mkdir " + folderName, cache: false}).
                success(function (data, status) {
                    $scope.files = data;
                    $scope.addFolderdatas.createSuccess = true;
                    $scope.addFolderdatas.createDisabled = false;
                    $scope.addFolderdatas.foldername = "";
                    $scope.addFolderdatas.successMsg = "New folder " + folderName + " created successfully...";
                }).
                error(function (data, status) {
                    $scope.addFolderdatas.createDisabled = true;
                    $scope.addFolderdatas.createSuccess = false;
                    $scope.addFolderdatas.foldername = "";
                    $scope.addFolderdatas.errorMsg = "Error occured while creating folder " + folderName + ".Please try again later...";
                });
        }
    }

    $scope.resetAddFolder = function(){
        console.log($scope.addFolderinitial[0].folderExistMsg);
        $scope.addFolderdatas = angular.copy($scope.addFolderinitial);
        console.log($scope.addFolderdatas.folderExistMsg);
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
            var error = "<div class='alert alert-error' ng-show='true'><button type='button' class='close' data-dismiss='alert'>&times;</button>Please select files/folders to rename...</div>";
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
        $scope.renamedatas.successMsg = "";
        $scope.renamedatas.errorMsg = "";
        $('#renameDiv :input').each(function () {
            $scope.renamedatas.folderExist = false;
            for (var i = 0; i < $scope.files.length; i++) {
                if (this.value == $scope.files[i].name) {
                    $scope.renamedatas.folderExist = true;
                    $scope.renamedatas.folderExistMsg = "";
                    $scope.renamedatas.folderExistMsg += this.value + " already exists. Please provide a different name...";
                }
            }
            if ($scope.renamedatas.folderExist == false){
                var name = jQuery.trim(this.value);
                if (name == "" || name == "null" || name == "NULL" || name == null ){
                    $scope.renamedatas.renameDisabled = true;
                    $scope.renamedatas.errorMsg += "Cannot rename file/folder without a name or name with only spaces. Please rename with  valid name... "
                }else {
                    var pwd = $scope.pwd.replace(/\//g, '*');
                    var source = $(this).attr('id');
                    var dest = this.value;
                    $http({method: "GET", url: "filemanager/command/mv " + source + "*" + pwd + "*" + dest, cache: false}).
                        success(function (data, status) {
                            $scope.files = data;
                            $scope.renamedatas.renameSuccess = true;
                            $scope.renamedatas.successMsg += source + " successfully renamed to " + dest + "... ";
                        }).
                        error(function (data, status) {
                            $scope.renamedatas.errorMsg += "Error occurred while renaming " + source + ". Please try again later... ";
                            $scope.renamedatas.renameDisabled = true;
                        });
                }
            }
        });
    }

    $scope.resetRenameFolder = function(){
        $scope.renamedatas = angular.copy($scope.renameinitial);
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
        var fileNameFullPath;
        var fileName;
        var home = $scope.home.replace(/\//g, '*');
        var pwd = $scope.pwd.replace(/\//g, '*');
        if (targetFolder.name == "Other"){
            if (customFolderName.contains("/")){
                customFolderName = customFolderName.replace(/\//g, '*');
                fileNameFullPath = home + "*" + customFolderName;
                fileName = customFolderName;
            }else if (customFolderName == "Home"){
                fileNameFullPath = home;
                fileName = home;
            }
        }else if (targetFolder.name == "Select from Home"){
            fileNameFullPath = home + "*" + homeFolder.name;
            fileName = homeFolder.name;
        } else {
            fileNameFullPath = pwd + "*" + targetFolder.name;
            fileName = targetFolder.name;
        }
        $scope.mvdatas.successMsg = "";
        $scope.mvdatas.errorMsg = "";
        for (var i = 0; i < selectedFiles.length; i++){
            var mvSelectedFile = selectedFiles[i].name;
            $http({method: "GET", url: "filemanager/command/mv " + mvSelectedFile +"*" + fileNameFullPath, cache: false}).
                success(function (data, status) {
                    console.log(data);
                    $scope.files = data;
                    $scope.mvdatas.moveSuccess = true;
                    $scope.mvdatas.successMsg += mvSelectedFile + " moved to " + fileName + " successfully... ";
                }).
                error(function (data, status) {
                    console.log("Error getting files !");
                    $scope.mvdatas.moveDisabled = true;
                    $scope.mvdatas.errorMsg += "Error occured while moving " + mvSelectedFile + " to " + fileName + " .Please try again later...  ";
                });
        }
    }

    $scope.resetMv = function(){
        $scope.mvdatas = angular.copy($scope.mvinitial);
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
            $scope.otherfolder = "Home";

        }else if (folerName.name == "Select from Home"){
            $scope.selectHome = true;
        }
    }

    $scope.copy = function (targetFolder , customFolderName, homeFolder) {
        var selectedFiles = getCheckedFiles($scope.files);
        var fileNameFullPath;
        var fileName;
        var home = $scope.home.replace(/\//g, '*');
        var pwd = $scope.pwd.replace(/\//g, '*');
        if (targetFolder.name == "Other"){
            if (customFolderName.contains("/")){
                customFolderName = customFolderName.replace(/\//g, '*');
                fileNameFullPath = home + "*" +customFolderName;
                fileName = customFolderName;
            }else if (customFolderName == "Home"){
                fileNameFullPath = home;
                fileName = home;
            }
        }else if (targetFolder.name == "Select from Home"){
            fileNameFullPath = home + "*" + homeFolder.name;
            fileName = homeFolder.name;
        } else {
            fileNameFullPath = pwd + "*" + targetFolder.name;
            fileName = targetFolder.name;
        }
        $scope.cpdatas.successMsg = "";
        $scope.cpdatas.errorMsg = "";
        for (var i = 0; i < selectedFiles.length; i++){
            var selectedCPFile = selectedFiles[i].name;
            if (selectedCPFile == targetFolder.name){
                fileNameFullPath =  pwd + "*" + "Copy_" + targetFolder.name;
                fileName =  "Copy_" + targetFolder.name;
            } else if (targetFolder.name == "To Current Folder"){
                    fileNameFullPath = pwd + "*" + "Copy_" + selectedCPFile;
                    fileName = "Copy_" + selectedCPFile;
            }
            $http({method: "GET", url: "filemanager/command/cpr " + selectedCPFile +"*" + fileNameFullPath, cache: false}).
                success(function (data, status) {
                    $scope.files = data;
                    $scope.cpdatas.copySuccess = true;
                    $scope.cpdatas.successMsg += selectedCPFile + " copied to " + fileName + " successfully... " ;
                }).
                error(function (data, status) {
                    console.log("Error copying files !");
                    $scope.cpdatas.copyDisabled = true;
                    $scope.cpdatas.errorMsg += "Error occured while copying " + selectedCPFile + " to " + fileName + " . Please try again later... " ;
                });
        }
    }

    $scope.resetCp = function(){
        $scope.cpdatas = angular.copy($scope.cpinitial);
    }

    //deleting a file
    $scope.deleteFile = function () {
        var fileNames = getCheckedFiles($scope.files);
        $scope.deletedatas.successMsg = "";
        $scope.deletedatas.errorMsg = "";
        for (var j = 0; j < fileNames.length ; j++){
            if (fileNames[j] != null || fileNames[j] != undefined ){
                var deleteFilename = fileNames[j].name;
                $http({method: "GET", url: "filemanager/command/rm " + deleteFilename, cache: false}).
                    success(function (data, status) {
                        $scope.files = data;
                        $scope.deletedatas.deleteSuccess = true;
                        $scope.deletedatas.successMsg += deleteFilename + " deleted successfully... ";
                    }).
                    error(function (data, status) {
                        $scope.deletedatas.deleteDisabled = true;
                        $scope.deletedatas.errorMsg += "Unable to delete " + deleteFilename + " ... ";
                    });
            }
        }
    }

    $scope.resetDelete = function(){
        $scope.deletedatas = angular.copy($scope.deleteinitial);
    }

    $scope.uploadFile = function (file) {
        console.log("*******at upload controller*****");
        console.log(file);
    }

    $scope.validateForm = function(){
        var x = document.forms["fileUploadForm"]["file"].value;
        if (x==null || x=="") {
            alert("Please select a file to upload");
            return false;
        }
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
