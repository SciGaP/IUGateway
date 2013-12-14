var feedbackApp = angular.module("feedbackApp",["user"]);

feedbackApp.controller("FeedbackCtrl", function($scope,$http) {
	$scope.saveFeedback = function() {
		$http({method:"POST", url:"submitFeedback", cache:false, 
			params : {name:($scope.name==undefined)?"":$scope.name, email: ($scope.email==undefined)?"":$scope.email, comment:$scope.comment}}).
		success(function(data,status) {
			$scope.message = "<div class='alert alert-success'><button type='button' class='close' data-dismiss='alert'>&times;</button>Thanks for your Feedback !</div>";
			$('#feedbackForm')[0].reset();
		}).
		error(function(data,status) {
			$scope.message = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>Something went wrong ! Could you please try again</div>";
		});
	};
});
$(document).ready(function() {
	/*	// Time out of >10ms is required for the topbar html to be loaded*/
	setTimeout(function() {
		$("#aboutLink").parent().addClass("active");
	},50);
});