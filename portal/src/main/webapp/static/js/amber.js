var amberApp = angular.module("amberApp",["user","amberServices","amberControllers","ngRoute","ngMockE2E"]);

amberApp.config(['$routeProvider' ,function($routeProvider) {
	$routeProvider.
	when('/', {controller:'JobListCtrl', templateUrl:'static/amber/jobs.html'}).
	when('/job/:jobId/tleap', {controller:'TleapCtrl', templateUrl:'static/amber/tleap.html'}).
	when('/job/:jobId/amber', {controller:'AmberCtrl', templateUrl:'static/amber/amberStep.html'}).
	when('/job/:jobId/postprocess', {controller:'PostProcessCtrl', templateUrl:'static/amber/postProcess.html'}).
	otherwise({redirectTo:'/'});
}]);

// For Dev Server - To bypass actual http calls to Server and provide mock data
amberApp.run(function($httpBackend) {
	var jobs = [];
	jobs.push({id:"j1",name:"Job One",machine:"Mason",currentStep:"TLeap",lastRunTime:new Date(2011,0,13)});
	jobs.push({id:"j2",name:"Job Two",machine:"Quarry",currentStep:"TLeap",lastRunTime:new Date(2013,11,1)});
	jobs.push({id:"j3",name:"Viknes's Job",machine:"Big Red II",currentStep:"Post Processing",lastRunTime:new Date(2012,4,6)});
	jobs.push({id:"j4",name:"Job Four",machine:"Big Red II",currentStep:"Amber",lastRunTime:new Date()});
	// Do not bother server, return specified response status code, data and header
	$httpBackend.whenGET('amberCtrl/jobs/all').respond(200, jobs, {header: 'one'});
	// Do not bother server, return data. Status and header are automatically set!
	$httpBackend.whenGET('anotherURL').respond('Another content');
	// Do real request
	$httpBackend.whenGET("static/topbars.html").passThrough();
	$httpBackend.whenGET("static/footer.html").passThrough();
	$httpBackend.whenGET("getUserinfo").passThrough();
	$httpBackend.whenGET("static/amber/jobs.html").passThrough();
	$httpBackend.whenGET("static/amber/tleap.html").passThrough();
	$httpBackend.whenGET("static/amber/amberStep.html").passThrough();
	$httpBackend.whenGET("static/amber/postProcess.html").passThrough();
});
// End For Dev Server
