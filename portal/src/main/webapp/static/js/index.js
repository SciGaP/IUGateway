var indexApp = angular.module("indexApp", ["user","urlprovider"]);

// Enabling CORS in Angular
indexApp.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}]);

indexApp.controller("IndexCtrl", function($scope,$http,UrlProvider) {
	
	// Begin get user login information - Gets user info from UserCtrl
	$scope.$on('UserLogin', function(emitEvent,args) {
		$scope.username = args.username;
		$scope.authenticated = args.authenticated;
	});
	// End get user login information

	var prevMonthDate = new Date();
    prevMonthDate.setMonth(new Date().getMonth()-1);
    $('#prevMonth').html(prevMonthDate.toString().substring(4,7));
    $('#prevMonthYear').html(prevMonthDate.toJSON().substring(0,4));
    
	// RTStat assumes month from 1-12 where as getDate gives month from 0-11.
	// So adding a 1 to the month when calling RTStat. Refer https://gateways.atlassian.net/browse/IUGATEWAY-140
    var summaryUrl = "//rtstats-devel.uits.indiana.edu/charts/monthly/hps/summary/"+prevMonthDate.toJSON().substring(0,4)+"/"+(prevMonthDate.getMonth()+1);
    var summaryUrlPrev = "//rtstats-devel.uits.indiana.edu/charts/monthly/hps/summary/"+prevMonthDate.toJSON().substring(0,4)+"/"+(prevMonthDate.getMonth());
    $http({method:"GET", url:summaryUrl, cache:true, timeout:30000}).
    success(function(data,status) {
    	if(data==undefined || data==null || data.length<1) {
    		prevMonthDate.setMonth(new Date().getMonth()-2);
    		$('#prevMonth').html(prevMonthDate.toString().substring(4,7));
    		$('#prevMonthYear').html(prevMonthDate.toJSON().substring(0,4));
    		$http({method:"GET", url:summaryUrlPrev, cache:true, timeout:30000}).
                success(function(data,status){
                    if(data==undefined || data==null || data.length<1) {
                        $scope.showSummaryUnavailable = true;
                    }
                    $scope.monthlySummary = data;
                    $scope.hideSummaryLoader = true;
            }).
                error(function(data,status,header, config) {
                    console.log("Error loading summary data");
                    $scope.hideSummaryLoader = true;
                    $scope.showSummaryError = true;
                });
        }
    	$scope.monthlySummary = data;
    	$scope.hideSummaryLoader = true;
    }).
    error(function(data,status,header, config) {
    	console.log("Error loading summary data");
    	$scope.hideSummaryLoader = true;
    	$scope.showSummaryError = true;
    });
	
    // Should initialize these variables. The http promise functions looks for these variables to fill in values
	$scope.systemStatus = {};
    $scope.systemStatus.bigred2 = {};
	$scope.systemStatus.mason = {};
	$scope.systemStatus.quarry = {};
	
	var updateSystemStatus = function() {
        loadJobCounts($scope,$http,UrlProvider,"bigred2");
		loadJobCounts($scope,$http,UrlProvider,"mason");
		loadJobCounts($scope,$http,UrlProvider,"quarry");
        loadNodeCounts($scope,$http,UrlProvider,"bigred2");
		loadNodeCounts($scope,$http,UrlProvider,"mason");
		loadNodeCounts($scope,$http,UrlProvider,"quarry");
	};
	updateSystemStatus();
	// Refresh System status every 5 mins
	setInterval(updateSystemStatus , 300000);
});

indexApp.controller("CIBFeedCtrl", function($scope,$http,UrlProvider) {
	var url = UrlProvider.getCIBFeedUrl();
	loadFeed($scope,$http,UrlProvider,url);
});

indexApp.controller("NoticesFeedCtrl", function($scope,$http,UrlProvider) {
	var url = UrlProvider.getNoticesFeedUrl();
	loadFeed($scope,$http,UrlProvider,url);
});

var loadFeed = function($scope,$http,UrlProvider,url) {
	$http({method:"JSONP", url: document.location.protocol + UrlProvider.getFeedLoaderUrl() + encodeURIComponent(url), cache:true}).
	success(function(data,status) {
		if (data!=null && data.responseData != null && data.responseData.feed!=null){
            $scope.feeds = data.responseData.feed.entries;
		} else {
			$scope.feeds = [];
		}
		$scope.hideLoader = true;
	}).
	error(function(data,status) {
		console.log("Error loading feed "+url);
		$scope.hideLoader = true;
	});
};

var loadJobCounts = function($scope,$http,UrlProvider,machine) {
	$http({method: "GET", url : "jobInfo"+UrlProvider.getUrlForMachine(machine)+"/jobstatus", cache:false, timeout:30000}).
	success(function(data,status) {
		systemHealthy = true;
		var jobs = {};

        if(data!=null && data!="" && data.results[0]!=null) {
            var runningJobCount = 0;
            var idleJobCount = 0;
            var notQueuedJobCount = 0;
            var completedJobCount = 0;
            var otherJobCount = 0;
            var results = data.results;
            var systemState;

            if (systemHealthy){
                systemState = "Healthy";
            } else{
                systemState = "Not Healthy";
            }

            for(var i = 0; i < results.length; i++) {
                var state = results[i].states.state;
                if (state == "Running"){
                     runningJobCount++;
                } else if (state == "Idle"){
                    idleJobCount++;
                }else if (state == "NotQueued"){
                    notQueuedJobCount++;
                }else if (state == "Completed"){
                    completedJobCount++;
                }else{
                    otherJobCount++;
                }
            }
            var totalJobCount = results.length;

            jobs.totalJobCount = totalJobCount;
            jobs.runningJobCount = runningJobCount;
            jobs.idleJobCount = idleJobCount;
            jobs.notQueuedJobCount = notQueuedJobCount;
            jobs.completedJobCount = completedJobCount;
            jobs.otherJobCount = otherJobCount;

            $scope.systemStatus[machine].machineName = UrlProvider.getNameForMachine(machine);
            $scope.systemStatus[machine].machine = machine;
            $scope.systemStatus[machine].systemState = systemState;
            $scope.systemStatus[machine].jobs = jobs;

        }
	}).
	error(function(data,status) {
		console.log("Error fetching job data !!!!!");
		$scope.systemStatus[machine].machineName = UrlProvider.getNameForMachine(machine);
		$scope.systemStatus[machine].dataloaderror = true;
	});
};

var loadNodeCounts = function($scope,$http,UrlProvider,machine) {
	$http({method:"GET", url : "nodeInfo"+UrlProvider.getUrlForMachine(machine)+"/nodestatus", cache:false, timeout:30000}).
	success(function(data,status) {
		if(data!=null && data!="" && data.results[0]!=null) {
            var runningNodeCount = 0;
            var idleNodeCount = 0;
            var busyNodeCount = 0;
            var drainedNodeCount = 0;
            var otherNodeCount = 0;
            var totalNodeCount = data.totalCount;
            var results = data.results;

            for(var i = 0; i < results.length; i++) {
                var state = results[i].states.state;
                if (state == "Running"){
                    runningNodeCount++;
                } else if (state == "Idle"){
                    idleNodeCount++;
                }else if (state == "Busy"){
                    busyNodeCount++;
                }else if (state == "Drained"){
                    drainedNodeCount++;
                }else{
                    otherNodeCount++;
                }
            }

            var nodes = {};
            nodes.runningNodeCount = runningNodeCount;
            nodes.idleNodeCount = idleNodeCount;
            nodes.busyNodeCount = busyNodeCount;
            nodes.drainedNodeCount = drainedNodeCount;
            nodes.otherNodeCount = otherNodeCount;
            nodes.totalNodeCount = totalNodeCount;
            
            $scope.systemStatus[machine].nodes = nodes;
            
        }
	}).
	error(function(data,status) {
		console.log("Error fetching node data !!!!!");
		$scope.systemStatus[machine].machineName = UrlProvider.getNameForMachine(machine);
		$scope.systemStatus[machine].dataloaderror = true;
	});
};

$(document).ready(function() {
	setTimeout(function() {
		$("#homeLink").parent().addClass("active");

        var username = $("#loggedUser").attr("data-username");
        if(username == undefined) {
            $(".authenticated").hide();
        } else {
            $(".notAuthenticated").hide();
        }

	},500);
});

