var filebrowser = angular.module("filebrowser",["ngRoute","user","fileController"]);

filebrowser.config(['$routeProvider' ,function($routeProvider) {
    $routeProvider.
        when('/', {controller:'FileBrowserCtrl', templateUrl:'static/filebrowser/filelist.html'}).
        otherwise({redirectTo:'/'});
}]);