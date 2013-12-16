var amberApp = angular.module("amberApp",["user","amberServices","amberControllers","ngRoute","ngMockE2E"]);

amberApp.config(['$routeProvider' ,function($routeProvider) {
	$routeProvider.
	when('/', {controller:'JobListCtrl', templateUrl:'static/amber/jobs.html'}).
	when('/job/:jobId/tleap', {controller:'TleapCtrl', templateUrl:'static/amber/tleap.html'}).
	when('/job/:jobId/amber', {controller:'AmberCtrl', templateUrl:'static/amber/amberStep.html'}).
	when('/job/:jobId/postprocess', {controller:'PostProcessCtrl', templateUrl:'static/amber/postProcess.html'}).
	otherwise({redirectTo:'/'});
}]);


