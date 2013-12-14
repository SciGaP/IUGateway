angular.module("chartApp",["user"]).
	controller("ChartCtrl", function($scope) {
		var date = new Date();
		var dateString = (date.getMonth()+1)+"-"+date.getDate()+"-"+date.getFullYear();
		$("#startDate").datepicker("setValue","01-01-2012");
		$("#endDate").datepicker("setValue", dateString);
		
		$scope.$on('UserLogin', function(emitEvent,args) {
			$scope.username = args.username;
			$scope.authenticated = args.authenticated;
			loadCharts($scope.username);
		});
		
		$scope.update = function() {
			loadCharts($scope.username);
		};
	});

$(document).ready(function() {
	setTimeout(function() {
		$("#infoLink").parent().addClass("active");
		
		// Affix date input fields if window size is greater than 980px
		if($(window).width()>980) {
			$('#dateInputs').affix({
		        offset: {
		          top: 0
		        }
			});
		}
	},300);
	
});

var loadCharts = function(username) {
	var startDateValue = $("#startDateText").val();
	var startDateString = startDateValue.substring(6)+"-"+startDateValue.substring(0,2)+"-"+startDateValue.substring(3,5);
	var endDateValue = $("#endDateText").val();
	var endDateString = endDateValue.substring(6)+"-"+endDateValue.substring(0,2)+"-"+endDateValue.substring(3,5);
	jQuery.support.cors = true;
	
	/*$.get(protocol+'//rtstats-devel.uits.indiana.edu/charts/hps/users?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#hps_users_chart").html("Error occurred loading chart data");
	});*/
	
	// Written as seperate call for each chart because each returns the value on to a different div. Reading the div name from the data looks impossible.
	
	$.get('//rtstats-devel.uits.indiana.edu/charts/user/'+username+'/jobs?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#jobs_chart").html("Error occurred retrieving Jobs chart data");
	});
	
	$.get('//rtstats-devel.uits.indiana.edu/charts/user/'+username+'/bytes?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#io_chart").html("Error occurred retrieving Read write bytes chart data");
	});
	
	$.get('//rtstats-devel.uits.indiana.edu/charts/user/'+username+'/hours?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#hours_chart").html("Error occurred retrieving Core hours chart data");
	});
	
	$.get('//rtstats-devel.uits.indiana.edu/charts/user/'+username+'/queue?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#queue_chart").html("Error occurred retrieving Queue time chart data");
	});
	
	$.get('//rtstats-devel.uits.indiana.edu/charts/user/'+username+'/rfs-files?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#rfs_files_chart").html("Error occurred while retrieving RFS Files chart data");
	});
	
	$.get('//rtstats-devel.uits.indiana.edu/charts/user/'+username+'/rfs-gb?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#rfs_gb_chart").html("Error occurred while retrieving RFS GB chart data");
	});
	
	$.get('//rtstats-devel.uits.indiana.edu/charts/user/'+username+'/hpss-files?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#hpss_files_chart").html("Error occurred while retrieving HPSS Files chart data");
	});
	
	$.get('//rtstats-devel.uits.indiana.edu/charts/user/'+username+'/hpss-gb?from='+startDateString+'&to='+endDateString, function(data) {
		eval(data);
	}).fail(function(data){
		$("#hpss_gb_chart").html("Error occurred while retrieving HPSS GB chart data");
	});

};
