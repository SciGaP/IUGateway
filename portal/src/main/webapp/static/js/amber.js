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
	var jobj1 = {};
	jobj1.tleap = {};
	jobj1.tleap.status = "Completed";
	jobj1.tleap.inputFiles = ["/usr/home/job/input.pdb"];
	jobj1.tleap.outputFiles = ["/usr/home/job/out.cfg"];
	jobj1.amber = {};
	jobj1.amber.status = "Started";
	jobj1.amber.inputFiles = ["/usr/home/job/out.cfg","/usr/home/job/blah.cfg","/usr/home/job/aer.cfg","usr/home/extra/file.cfg"];
	jobj1.amber.outputFiles = [];
	jobj1.postProcess = {};
	jobj1.postProcess.status = "";
	jobj1.postProcess.inputFiles = [];
	jobj1.postProcess.outputFiles = [];
	var jobj3 = {};
	jobj3.tleap = {};
	jobj3.tleap.status = "Completed";
	jobj3.tleap.inputFiles = ["/usr/viknes/home/job/input.pdb"];
	jobj3.tleap.outputFiles = ["/usr/viknes/home/job/out.cfg"];
	jobj3.amber = {};
	jobj3.amber.status = "Completed";
	jobj3.amber.inputFiles = ["/usr/viknes/home/job/out.cfg","/usr/viknes/home/job/blah.cfg"];
	jobj3.amber.outputFiles = ["/usr/viknes/home/job/outNew.cfg","/usr/viknes/home/job/blah.restart"];
	jobj3.postProcess = {};
	jobj3.postProcess.status = "Completed";
	jobj3.postProcess.inputFiles = ["/usr/viknes/home/job/outNew.cfg","/usr/viknes/home/job/blah.restart"];
	jobj3.postProcess.outputFiles = ["/usr/viknes/home/job/output.txt","/usr/viknes/home/job/output.plot"];
	var jobj4 = {};
	jobj4.tleap = {};
	jobj4.tleap.status = "Completed";
	jobj4.tleap.inputFiles = ["/usr/home/job/input.pdb"];
	jobj4.tleap.outputFiles = ["/usr/home/job/out.cfg"];
	jobj4.amber = {};
	jobj4.amber.inputFiles = ["/usr/home/job/out.cfg","/usr/home/job/blah.cfg","/usr/home/job/aer.cfg"];
	jobj4.amber.outputFiles = ["/usr/home/job/outNew.cfg","/usr/home/job/blah.restart","/usr/home/job/aer.restart"];
	jobj4.amber.status = "Completed";
	jobj4.postProcess = {};
	jobj4.postProcess.status = "";
	jobj4.postProcess.inputFiles = [];
	jobj4.postProcess.outputFiles = [];
	
	// Do not bother server, return specified response status code, data and header
	$httpBackend.whenGET('amberCtrl/jobs/all').respond(200, jobs, {header: 'one'});
	// Do not bother server, return data. Status and header are automatically set!
	$httpBackend.whenGET('amberCtrl/jobs/j1').respond(jobj1);
	$httpBackend.whenGET('amberCtrl/jobs/j2').respond(jobj1);
	$httpBackend.whenGET('amberCtrl/jobs/j3').respond(jobj3);
	$httpBackend.whenGET('amberCtrl/jobs/j4').respond(jobj4);
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
