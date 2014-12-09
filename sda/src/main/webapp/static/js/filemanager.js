var fileManagerApp = angular.module("fileManagerApp", ["user", "urlprovider"]);

fileManagerApp.controller("FileManagerCtrl", function ($scope, $http) {
    $scope.hideLoader = true;
    $scope.addFolderinitial = [
        {
            folderExist: false,
            createSuccess: false,
            createDisabled: false,
            foldername: "",
            folderExistMsg: "",
            successMsg: "",
            errorMsg: ""
        }
    ];
    $scope.renameinitial = [
        {
            folderExist: false,
            renameSuccess: false,
            renameDisabled: false,
            folderExistMsg: "",
            successMsg: "",
            errorMsg: ""
        }
    ];
    $scope.deleteinitial = [
        {
            deleteSuccess: false,
            deleteDisabled: false,
            successMsg: "",
            errorMsg: ""
        }
    ];
    $scope.mvinitial = [
        {
            moveSuccess: false,
            moveDisabled: false,
            successMsg: "",
            errorMsg: ""
        }
    ];
    $scope.cpinitial = [
        {
            copySuccess: false,
            copyDisabled: false,
            successMsg: "",
            errorMsg: ""
        }
    ];
    $scope.fileUploadinitial = [
        {
            uploadSuccess: false,
            uploadDisabled: false,
            successMsg: "",
            errorMsg: ""
        }
    ];
    $scope.addFolderdatas = angular.copy($scope.addFolderinitial);
    $scope.renamedatas = angular.copy($scope.renameinitial);
    $scope.deletedatas = angular.copy($scope.deleteinitial);
    $scope.mvdatas = angular.copy($scope.mvinitial);
    $scope.cpdatas = angular.copy($scope.cpinitial);
    $scope.fileUploadDatas = angular.copy($scope.fileUploadinitial);
    $http({method: "GET", url: "filemanager/getRemoteUser", cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.remoteUser = data;
        }).
        error(function (data, status) {
        });
    $http({method: "GET", url: "filemanager/getPortalUrl", cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.portalUrl = data;
        }).
        error(function (data, status) {
            console.log("Error getting home directory !");
        });
    $http({method: "GET", url: "filemanager/getPwd", cache: false}).
        success(function (data, status) {
            console.log(data);
            $scope.pwd = data;
        }).
        error(function (data, status) {
            console.log("Error getting current working directory !");
        });
    $http({method: "GET", url: "filemanager/getHome", cache: false}).
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
            $http({method: "GET", url: "filemanager/getPwd", cache: false}).
                success(function (data, status) {
                    console.log(data);
                    $scope.pwd = data;
                }).
                error(function (data, status) {
                    console.log("Error getting current working directory !");
                    $scope.hideLoader = false;
                    $scope.showError = true;
                });
        }).
        error(function (data, status) {
            $scope.hideLoader = false;
            $scope.showError = true;
        });

    $scope.upOneLevel = function () {
        $scope.hideLoader = true;
        var parent = "..";
        var url = "filemanager/command/cd " + parent;
        $http({method: "GET", url: "filemanager/command/cd " + parent, cache: false}).
            success(function (data, status) {
                $scope.files = data;
                $scope.hideLoader = false;
                $http({method: "GET", url: "filemanager/getPwd", cache: false}).
                    success(function (data, status) {
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

    $scope.goInside = function (file) {
        if (file.fileType == "dir") {
            $scope.hideLoader = true;
            var folderName = file.name;
            $http({method: "GET", url: "filemanager/command/cd " + folderName, cache: false}).
                success(function (data, status) {
                    $scope.hideLoader = false;
                    $scope.files = data;
                    $http({method: "GET", url: "filemanager/getPwd", cache: false}).
                        success(function (data, status) {
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
        } else if (file.fileType == "file"){
            var name = encodeURIComponent(file.name).replace(/['()]/g, escape).replace(/\*/g, '%2A').replace(/%(?:7C|60|5E)/g, unescape);
            try {
                $scope.showError1 = false;
                window.location = "filemanager/download/ " + name;
            }catch(err) {
                $scope.showError1 = true;
            }
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

    $scope.resetAddFolder = function () {
        $scope.addFolderdatas = angular.copy($scope.addFolderinitial);
    }

    $scope.generateDeleteModel = function () {
        var selectedFiles = getCheckedFiles($scope.files);
        $scope.selectedFiles = selectedFiles;
        if (selectedFiles.length == 0) {
            var error = "<div class='alert alert-error' ng-show='true'><button type='button' class='close' data-dismiss='alert'>&times;</button>Please select files / folders to delete...</div>";
            $('#deleteModel').show();
            $('#deletefiles').show().html(error);
        } else {
            var content = "<div id='deleteDiv'>";
            for (var j = 0; j < selectedFiles.length; j++) {
                if (selectedFiles[j].file) {
                    content += "<p>Will delete the file " + selectedFiles[j].name + "</p>";
                } else {
                    content += "<p>Will delete the folder " + selectedFiles[j].name + " and any of its available content.</p>";
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
                console.log("Error getting free disk space !");
            });
    }

    $scope.viewUsedSpace = function () {
        $http({method: "GET", url: "filemanager/fileCount", cache: false}).
            success(function (data, status) {
                console.log(data);
                $scope.totalfiles = data;
                $http({method: "GET", url: "filemanager/usedSpace", cache: false}).
                    success(function (data, status) {
                        $scope.totalSize = data;
                        var content = "<p>Number of files for user " + $scope.remoteUser + " : " + $scope.totalfiles + "</p>";
                        content += "<p>Used space for user " + $scope.remoteUser + " : " + $scope.totalSize + "</p></br>";
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
        if (selectedFiles.length == 0) {
            var error = "<div class='alert alert-error' ng-show='true'><button type='button' class='close' data-dismiss='alert'>&times;</button>Please select files/folders to rename...</div>";
            $('#renameModel').show();
            $('#renameFile').show().html(error);
        } else {
            var content = "<div id='renameDiv'>";
            for (var j = 0; j < selectedFiles.length; j++) {
                content += "<div class='row-fluid'><div class='span4'><strong>Rename " + selectedFiles[j].name + " to</strong></div><div><input id='" + selectedFiles[j].name + "' type='text' value='" + selectedFiles[j].name + "' name='newName" + j + "' ng-model='newName" + j + "'/></div></div>";
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
//                if (!$scope.files[i].file){
                if (this.value == $scope.files[i].name) {
                    $scope.renamedatas.folderExist = true;
                    $scope.renamedatas.folderExistMsg = "";
                    $scope.renamedatas.folderExistMsg += this.value + " already exists. Please provide a different name...";
                }
//                }
            }
            if ($scope.renamedatas.folderExist == false) {
                var name = jQuery.trim(this.value);
                if (name == "" || name == "null" || name == "NULL" || name == null) {
                    $scope.renamedatas.renameDisabled = true;
                    $scope.renamedatas.errorMsg += "Cannot rename file/folder without a name or name with only spaces. Please rename with  valid name... "
                } else {
                    var pwd = $scope.pwd.replace(/\//g, '*');
                    var source = $(this).attr('id');
                    var encodedSource = encodeURIComponent(source);
                    var dest = this.value;
                    $http({method: "GET", url: "filemanager/command/rename " + encodedSource + "*" + pwd + "*" + dest, cache: false}).
                        success(function (data, status) {
                            $scope.files = data;
                            $scope.files = removeDuplicates($scope.files);
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
//        $scope.files = getRemainingFiles($scope.files);
    }

    $scope.resetRenameFolder = function () {
        $scope.renamedatas = angular.copy($scope.renameinitial);
    }

    $scope.generateMvModel = function () {
        $scope.selectOther = false;
        $scope.selectHome = false;
        $scope.selectedFiles = getCheckedFiles($scope.files);
        var fileNames = getCheckedFileNames($scope.selectedFiles);
        var files = $scope.files;
        var homeFolders = [];
        var folders = [];
        $http({method: "GET", url: "filemanager/command/ls ~", cache: false}).
            success(function (data, status) {
                $scope.homeFiles = data;
                var homeFiles = $scope.homeFiles;
                for (var i = 0; i < homeFiles.length; i++) {
                    if (!homeFiles[i].file) {
                        var found = $.inArray(homeFiles[i].name, fileNames);
                        if (found == -1) {
                            homeFolders.push(homeFiles[i]);
                        }
                    }
                }
                $scope.homeFoldername = homeFolders[0];
                $scope.homeFolders = homeFolders;
                for (var j = 0; j < files.length; j++) {
                    if (!files[j].file) {
                        var found = $.inArray(files[j].name, fileNames);
                        if (found == -1) {
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

    $scope.move = function (targetFolder, customFolderName, homeFolder) {
        var selectedFiles = getCheckedFiles($scope.files);
        var home = $scope.home.replace(/\//g, '*');
        var pwd = $scope.pwd.replace(/\//g, '*');
        var fileNameFullPath = home;
        var fileName;

        if (targetFolder.name == "Other") {
            if (customFolderName.contains("/")) {
                customFolderName = customFolderName.replace(/\//g, '*');
                fileNameFullPath = home + "*" + customFolderName;
                fileName = customFolderName;
            } else if (customFolderName == "Home") {
                fileNameFullPath = home;
                fileName = home;
            } else {
                fileNameFullPath = home + "*" + customFolderName;
                fileName = home + "/" + customFolderName;
            }
        } else if (targetFolder.name == "Select from Home") {
            fileNameFullPath = home + "*" + homeFolder.name;
            fileName = homeFolder.name;
        } else {
            fileNameFullPath = pwd + "*" + targetFolder.name;
            fileName = targetFolder.name;
        }
        $scope.mvdatas.successMsg = "";
        $scope.mvdatas.errorMsg = "";
        for (var i = 0; i < selectedFiles.length; i++) {
            var mvSelectedFile = selectedFiles[i].name;
            mvSelectedFile = encodeURIComponent(mvSelectedFile);
            var path = fileNameFullPath + "*" + mvSelectedFile;
            $http({method: "GET", url: "filemanager/command/rename " + mvSelectedFile + "*" + path, cache: false}).
                success(function (data, status) {
                    $scope.files = data;
                    $scope.files = removeDuplicates($scope.files);
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

    $scope.resetMv = function () {
        $scope.mvdatas = angular.copy($scope.mvinitial);
    }

    $scope.generateCpModel = function () {
        $scope.selectOther = false;
        $scope.selectHome = false;
        $scope.selectedFiles = getCheckedFiles($scope.files);
        var fileNames = getCheckedFileNames($scope.selectedFiles);
        var files = $scope.files;
        var homeFolders = [];
        var folders = [];
        $http({method: "GET", url: "filemanager/command/ls ~", cache: false}).
            success(function (data, status) {
                $scope.homeFiles = data;
                var homeFiles = $scope.homeFiles;
                for (var i = 0; i < homeFiles.length; i++) {
                    if (!homeFiles[i].file) {
                        var found = $.inArray(homeFiles[i].name, fileNames);
                        if (found == -1) {
                            homeFolders.push(homeFiles[i]);
                        }
                    }
                }
                $scope.homeFoldername = homeFolders[0];
                $scope.homeFolders = homeFolders;
                for (var j = 0; j < files.length; j++) {
                    if (!files[j].file) {
                        var found = $.inArray(files[j].name, fileNames);
                        if (found == -1) {
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

    $scope.populateRest = function (folerName) {
        $scope.selectOther = false;
        $scope.selectHome = false;
        if (folerName.name == "Other") {
            $scope.selectOther = true;
            $scope.otherfolder = "Home";

        } else if (folerName.name == "Select from Home") {
            $scope.selectHome = true;
        }
    }

    $scope.copy = function (targetFolder, customFolderName, homeFolder) {
        var selectedFiles = getCheckedFiles($scope.files);
        var fileNameFullPath;
        var fileName;
        var home = $scope.home.replace(/\//g, '*');
        var pwd = $scope.pwd.replace(/\//g, '*');
        if (targetFolder.name == "Other") {
            if (customFolderName.contains("/")) {
                customFolderName = customFolderName.replace(/\//g, '*');
                fileNameFullPath = home + "*" + customFolderName;
                fileName = customFolderName;
            } else if (customFolderName == "Home") {
                fileNameFullPath = home;
                fileName = home;
            } else {
                fileNameFullPath = home + "*" + customFolderName;
                fileName = home + "/" + customFolderName;
            }
        } else if (targetFolder.name == "Select from Home") {
            fileNameFullPath = home + "*" + homeFolder.name;
            fileName = homeFolder.name;
        } else {
            fileNameFullPath = pwd + "*" + targetFolder.name;
            fileName = targetFolder.name;
        }
        $scope.cpdatas.successMsg = "";
        $scope.cpdatas.errorMsg = "";
        for (var i = 0; i < selectedFiles.length; i++) {
            var selectedFile = selectedFiles[i].name;
            var selectedCPFile = encodeURIComponent(selectedFile);
            var path = "";
            if (selectedCPFile == targetFolder.name) {
                path = pwd + "*" + "Copy_" + targetFolder.name;
                fileName = "Copy_" + targetFolder.name;
            } else if (targetFolder.name == "To Current Folder") {
                path = pwd + "*" + "Copy_" + selectedCPFile;
                fileName = "Copy_" + selectedCPFile;
            } else {
                path = fileNameFullPath + "*" + selectedCPFile;
            }
            $http({method: "GET", url: "filemanager/command/cpr " + selectedCPFile + "*" + path, cache: false}).
                success(function (data, status) {
                    $scope.files = data;
                    $scope.files = removeDuplicates($scope.files);
                    $scope.cpdatas.copySuccess = true;
                    $scope.cpdatas.successMsg += selectedFile + " copied to " + fileName + " successfully... ";
                }).
                error(function (data, status) {
                    console.log("Error copying files !");
                    $scope.cpdatas.copyDisabled = true;
                    $scope.cpdatas.errorMsg += "Error occured while copying " + selectedFile + " to " + fileName + " . Please try again later... ";
                });
        }
    }

    $scope.resetCp = function () {
        $scope.cpdatas = angular.copy($scope.cpinitial);
    }

    //deleting a file
    $scope.deleteFile = function () {
        var fileNames = getCheckedFiles($scope.files);

        $scope.deletedatas.successMsg = "";
        $scope.deletedatas.errorMsg = "";
        for (var j = 0; j < fileNames.length; j++) {
            if (fileNames[j] != null || fileNames[j] != undefined) {
                var filename = fileNames[j].name;
                var deleteFilename = encodeURIComponent(filename);
                $http({method: "GET", url: "filemanager/command/rm " + deleteFilename, cache: false}).
                    success(function (data, status) {
                        $scope.files = data;
                        $scope.files = removeDuplicates($scope.files);
                        $scope.deletedatas.deleteSuccess = true;
                        $scope.deletedatas.successMsg += filename + " deleted successfully... ";
                    }).
                    error(function (data, status) {
                        $scope.deletedatas.deleteDisabled = true;
                        $scope.deletedatas.errorMsg += "Unable to delete " + filename + " ... ";
                    });
            }
        }
    }

    $scope.resetDelete = function () {
        $scope.deletedatas = angular.copy($scope.deleteinitial);
    }

    $scope.validateForm = function () {
        var x = document.forms["fileUploadForm"]["file"].value;
        if (x == null || x == "") {
            alert("Please select a file to upload");
            return false;
        }
    }

    angular.element(document).ready(function () {
        var bar = $('.bar');
        var percent = $('.percent');
        $('#fileUploadForm').ajaxForm({
            beforeSend: function (arr, $form, options) {
                if (validateUpload()) {
                    if ($('#overwrite_enabled').prop('checked')) {

                    } else {
                        var fileName = $('#fileField')[0].files[0].name;
                        var files = $scope.files;
                        for (var i = 0; i < files.length; i++) {
                            if ($scope.files[i].file) {
                                if ($scope.files[i].name == fileName) {
                                    alert(fileName + " already exists...");
                                    $form.abort();
                                    return false;
                                }
                            }
                        }
                    }
                } else {
                    $scope.fileUploading = false;
                    console.log($scope.fileUploading);
                    $form.abort();
                    return false;
                }
//              status.empty();
                var percentVal = '0%';
                bar.width(percentVal)
                percent.html(percentVal);
            },
            uploadProgress: function (event, position, total, percentComplete) {
                var percentVal = percentComplete + '%';
                bar.width(percentVal);
                percent.html(percentVal);
                console.log(percentVal, position, total);
            },
            success: function () {
                var percentVal = '100%';
                bar.width(percentVal);
                percent.html(percentVal);
            },
            complete: function (xhr) {
                console.log("complete");
//                $(this).closest(".ui-dialog-content").dialog("close");
//                $('#fileUploadForm').
                location.reload();
//                status.html(xhr.responseText);
            }
        });
//        $( "#fileUploadForm" ).submit(function( event ) {
//            if (validateUpload()){
//                if ($('#overwrite_enabled').prop('checked')){
//                    var bar = $('.bar');
//                    var percent = $('.percent');
//                    var formData = new FormData($('fileUploadForm')[0]);
//                    $.ajax({
//                        url: 'filemanager/uploadFile',  //Server script to process data
//                        type: 'POST',
//                        xhr: function() {  // Custom XMLHttpRequest
//                            var myXhr = $.ajaxSettings.xhr();
//                            if(myXhr.upload){ // Check if upload property exists
//                                myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
//                            }
//                            return myXhr;
//                        },
//                        //Ajax events
//                        beforeSend: beforeSendHandler,
//                        success: completeHandler,
//                        error: errorHandler,
//                        // Form data
//                        data: formData,
//                        //Options to tell jQuery not to process data or worry about content-type.
//                        cache: false,
//                        contentType: false,
//                        processData: false
//                    });
////                    $scope.fileUploading = true;
////                    $('#progress1').show();
////                    $('#progress2').show();
//                }else{
//                    var fileName = $('#fileField')[0].files[0].name;
//                    var files = $scope.files;
//                    for (var i = 0; i < files.length; i++) {
//                        if ($scope.files[i].file) {
//                            if ($scope.files[i].name == fileName){
//                                $scope.fileUploading = false;
//                                event.preventDefault();
//                                alert(fileName + " already exists...");
//                            }
//                        }
//                    }
//                }
//            }else{
//                $scope.fileUploading = false;
//                console.log($scope.fileUploading);
//                event.preventDefault();
//            }
////            $("#overlay, #PleaseWait").show();
//        })
    });
});

function progressHandlingFunction(e) {
    if (e.lengthComputable) {
        $('progress').attr({value: e.loaded, max: e.total});
    }
}

var getCheckedFiles = function (files) {
    var fileNames = [];
    for (var i = 0; i < files.length; i++) {
        if (files[i].checked) {
            fileNames.push(files[i]);
        }
    }
    return fileNames;
}

var removeDuplicates = function (files) {
    console.log(files);
    var fileNames = [];
    for (var i = 0; i < files.length; i++) {
        fileNames[files[i].name] = files[i];
    }

    files = [];
    for (var key in fileNames) {
        files.push(fileNames[key]);
    }
    console.log(files);
    return files;
}

var getCheckedFileNames = function (files) {
    var fileNames = [];
    for (var i = 0; i < files.length; i++) {
        fileNames.push(files[i].name);
    }
    return fileNames;
}


var validateUpload = function () {
    if (window.File && window.FileReader && window.FileList && window.Blob) {
        //get the file size and file type from file input field
        var fsize = $('#fileField')[0].files[0].size;
        fsize = fsize / 1000000000;
        fsize = parseInt(fsize, 10)
        if (fsize > 10) //do something if file size more than 20 mb
        {
            alert(fsize + " GB\nToo big! Please upload a file less than 10 GB.");
            return false;
        } else {
            return true;
        }
    }
}
