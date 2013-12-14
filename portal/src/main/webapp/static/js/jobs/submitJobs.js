var submitJobApp = angular.module("submitJobApp",["user"]);

submitJobApp.controller("SubmitJobCtrl",function($scope,$http) {
	$('#appsTextbox').typeahead({source:['Choose a machine']});
	$scope.$on('UserLogin', function(emitEvent,args) {
		$scope.username = args.username;
		$scope.authenticated = args.authenticated;
	});
	$scope.machine = [];
	$scope.machine['bigred2'] = {pubkey:""};
	$scope.machine['mason'] = {pubkey:""};
	$scope.machine['quarry'] = {pubkey:""};
	$scope.chooseMachine = function(machinename) {
		$scope.machinename = machinename;
		$http({method: "GET", url: "modules/search?machine="+machinename+"&category=applications", cache: true}).
		success(function(data, status) {
			var apps = [];
			for(var i in data) {
				if(apps.indexOf(data[i].name)<0)
					apps.push(data[i].name);
			}
			$('#appsTextbox').typeahead().data('typeahead').source = apps;
			$http({method:"GET", url:"credentials/"+machinename+"/exist?username="+$scope.username,cache:false}).
			success(function(data,status) {
				if(data=='true')
					$scope.machine[machinename].credentialsExist = true;
				else {
					$scope.machine[machinename].credentialsExist = false;
					$('#myModal').modal('toggle');
				}
				console.log("CredentialExist for "+machinename+" is " + $scope.machine[machinename].credentialsExist);
			}).
			error(function(data,status) {
				$scope.machine[machinename].credentialsExist = false;
				$('#myModal').modal('toggle');
			});
		}).
		error(function(data, status) {
			console.log(data);
	    });
	};
	$scope.submitPassword = function() {
		console.log($scope.username);
		$http({method:"POST", url: "credentials/"+$scope.machinename+"/create",
			params:{username:$scope.username, password:$scope.password}}).
		success(function(data,status) {
			$scope.machine[$scope.machinename].keyCreationError = false;
			$scope.machine[$scope.machinename].pubkey=data;
		}).
		error(function(data, status) {
			$scope.machine[$scope.machinename].keyCreationError = true;
		});
	};	
	$scope.download = function() {
		var blob = new Blob([$scope.machine[$scope.machinename].pubkey], {type: "text/plain;charset=utf-8"});
		saveAs(blob, "id_rsa_"+$scope.username+".pub");
	};
	$scope.submitJob = function() {
		console.log("Trying to submit");
		$http({method:"POST", url: "job/bigred2/submit",
			params:{application : $scope.application, jobname : $scope.jobname,
				jobtype : $scope.jobtype, workingdir : $scope.workingdir,
				executable : $scope.executable, inputfile : $scope.inputfile,
				queuename : $scope.queuename, maxwalltime: $scope.maxwalltime,
				cpucount : $scope.cpucount, nodecount : $scope.nodecount,
				ppn : $scope.ppn, minmemory : $scope.minmemory,
				maxmemory : $scope.maxmemory}}).
		success(function(data, status) {
			if(data!=undefined && data!=null && data!='null') {
				console.log("Job submitted with ID "+data);
				$scope.isSubmitted=true;
				$scope.message = "<div class='alert alert-success'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
					+"Job submitted successfully with ID "+data;
			} else {
				$scope.isSubmissionError = true;
				$scope.message = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
					+"There was an error submitting the job";
			}
		}).
		error(function(data, status) {
			console.log("There was an error");
			$scope.isSubmissionError = true;
			$scope.message = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
				+"There was an error submitting the job";
		});
	};
});

$(document).ready(function() {
    setTimeout(function() {
        $("#jobLink").parent().addClass("active");
    },50);
});