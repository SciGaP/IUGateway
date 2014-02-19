var application = angular.module("application",["user","appServices","appControllers","ngRoute","ngMockE2E","angularFileUpload"]);

application.config(['$routeProvider' ,function($routeProvider) {
    $routeProvider.
        when('/', {controller:'JobListCtrl', templateUrl:'static/application/jobs.html'}).
        when('/newJob', {controller:'NewJobCtrl', templateUrl:'static/application/newJob.html'}).
        when('/job/:jobID/details', {controller:'JobController', templateUrl:'static/application/jobDetails.html'}).
        otherwise({redirectTo:'/'});
}]);

// For Dev Server - To bypass actual http calls to Server and provide mock data
application.run(function($httpBackend) {
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
    jobj3.tleap.inputFiles = ["/usr/sachith/home/job/input.pdb"];
    jobj3.tleap.outputFiles = ["/usr/sachith/home/job/out.cfg"];
    jobj3.amber = {};
    jobj3.amber.status = "Completed";
    jobj3.amber.inputFiles = ["/usr/sachith/home/job/out.cfg","/usr/sachith/home/job/blah.cfg"];
    jobj3.amber.outputFiles = ["/usr/sachith/home/job/outNew.cfg","/usr/sachith/home/job/blah.restart"];
    jobj3.postProcess = {};
    jobj3.postProcess.status = "Completed";
    jobj3.postProcess.inputFiles = ["/usr/sachith/home/job/outNew.cfg","/usr/sachith/home/job/blah.restart"];
    jobj3.postProcess.outputFiles = ["/usr/sachith/home/job/output.txt","/usr/sachith/home/job/output.plot"];
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
    //$httpBackend.whenGET('application/jobs/all').respond(200, jobs, {header: 'one'});
    $httpBackend.whenGET('application/allJobs').passThrough();
    // Do not bother server, return data. Status and header are automatically set!
    //$httpBackend.whenGET('application/jobs/j1').respond(jobj1);
    //$httpBackend.whenGET('application/jobs/j2').respond(jobj1);

    $httpBackend.whenGET('application/jobs/j3/info').passThrough();
    $httpBackend.whenGET('application/jobs/j5/info').passThrough();
    $httpBackend.whenGET('application/jobs/j6/info').passThrough();
    $httpBackend.whenGET('application/jobs/j7/info').passThrough();
    $httpBackend.whenGET('application/jobs/j4/info').passThrough();

    $httpBackend.when('POST','application/uploadPDB/test').passThrough();
    $httpBackend.when('POST','application/createJob').passThrough();
    $httpBackend.when('GET','application/uploadPDB/test').passThrough();
    $httpBackend.when('GET','application/createJob').passThrough();

    //$httpBackend.whenGET('application/jobs/j4').respond(job4);
    //$httpBackend.whenGET('application/jobs/j4').respond(job3);

    $httpBackend.whenGET('anotherURL').respond('Another content');
    // Do real request
    $httpBackend.whenGET("static/application/topbars.html").passThrough();
    $httpBackend.whenGET("static/application/footer.html").passThrough();
    $httpBackend.whenGET("getUserinfo").passThrough();
    $httpBackend.whenGET("static/application/jobs.html").passThrough();
    $httpBackend.whenGET("static/application/jobDetails.html").passThrough();
    $httpBackend.whenGET("static/application/newJob.html").passThrough();
});
// End For Dev Server
/**
 * Created by swithana on 2/4/14.
 */
