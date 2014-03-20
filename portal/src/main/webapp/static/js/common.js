angular.module("user",[]).
	service("UserService", function($http) {
		function service() {
			this.login = function() {
				return $http({method:"GET", url:"getUserinfo", cache:false}).
				success(function(data,status) {
				}).
				error(function(data,status) {
					console.log("Error getting user information");
				});
			};
			this.profile = function() {
                return $http({method:"GET", url : "getScienceDiscipline", cache:false}).
                success(function(data, status) {
                    console.log("updating profile !");
                }).
                error(function(data, status) {
                    console.log("Error getting profile !");
                });
            };
			this.logout = function() {
				return $http({method:"POST", url : "logout", cache:false}).
				success(function(data, status) {
				}).
				error(function(data, status) {
					console.log("Error logging out user !");
				});
			};
		};
		return new service();
	}).
	controller("UserCtrl", function($scope,$http,UserService) {
		UserService.login().success(function(username) {
			if(username!=undefined && username!=null && username!="") {
				$scope.username = username;
				$scope.authenticated = true;
			} else {
				$scope.username = "";
				$scope.authenticated = false;
			}
			$scope.$emit("UserLogin",{ username:$scope.username, authenticated:$scope.authenticated});
		}).
		error(function(data) {
			$scope.username = "";
			$scope.authenticated = false;
			$scope.$emit("UserLogin",{ username:$scope.username, authenticated:$scope.authenticated});
		});

		$scope.profile = function() {
            UserService.profile().success(function() {
//               $scope.disciplines = {1: "Anthropology &amp; Archaeology", 2: "Human Studies" , 3: "Global Studies",
//                                    4: "Arts (Fine and Performing)",5: "Business &amp; Management", 6: "Education"};
//               $scope.disciplines = ["Anthropology & Archaeology","Human Studies" , "Global Studies",
//                                    "Arts (Fine and Performing)","Business & Management", "Education",
//                                    "Humanities, Classics, Language Studies",
//                                    "Informatics, Library Science, and Computing",
//                                    "Law", "Life Sciences, Allied Health, Biomedical Research",
//                                    "Physical and Mathematical Sciences",
//                                    "Public Affairs & Environmental Affairs",
//                                    "Social Sciences & Political Science",
//                                    "Sports and Related Fields"];

               $scope.disciplines = [{id:13, name:"Anthropology & Archaeology,Human Studies, Global Studies"},
                                    {id:14, name:"Arts (Fine and Performing)"},{id:15, name: "Business & Management"}, {id:16, name: "Education"},
                                    {id:17, name:"Humanities, Classics, Language Studies"},
                                    {id:18, name:"Informatics, Library Science, and Computing"},
                                    {id:19, name:"Law"}, {id:20, name:"Life Sciences, Allied Health, Biomedical Research"},
                                    {id:21, name:"Physical and Mathematical Sciences"},
                                    {id:22, name:"Public Affairs & Environmental Affairs"},
                                    {id:23, name:"Social Sciences & Political Science"},
                                    {id:24, name:"Sports and Related Fields"}];


               $scope.subdisciplines = [{id:378, name:"Ancient Studies",id:379, name:"Anthropology"} , {id:380, name:"Communication and Culture"},
                                   {id:381, name:"Cultural Studies"},{id:382, name:"Folklore"}, {id:383, name:"Game Studies"},
                                   {id:384, name:"Gerontology"}, {id:385, name:"Global Human Diversity"},
                                   {id:386, name:"Global Studies"}, {id:387, name:"Human-Environment Interaction"},
                                   {id:388, name:"Human Geography"}, {id:389, name:"India Studies"}, {id:390, name:"International Studies"},
                                   {id:391, name:"Jewish Studies"}, {id:392, name:"Journalism"}, {id:393, name:"Labor Studies"},
                                   {id:394, name:"Latin American and Caribbean Studies"}, {id:395, name:"Latino Studies"},
                                   {id:396, name:"Mass Communication"}, {id:397, name:"Medieval Studies"}, {id:398, name:"Methodology"},
                                   {id:399, name:"New Media and Interactive Storytelling"}, {id:400, name:"Photography"},
                                   {id:401, name:"Political and Civic Engagement"}, {id:402, name:"Political Science"},
                                   {id:403, name:"Political Science / Economics"}, {id:404, name:"Psychology"},
                                   {id:405, name:"Recreational Therapy"}, {id:406, name:"Sociology"}, {id:407, name:"Sociology of Work and Business"},
                                   {id:408, name:"Spatial Analysis"}, {id:409, name:"Telecommunications"}, {id:410, name:"Urban Studies"},
                                   {id:411, name:"West European Studies"}, {id:412, name:"Women & Studies"}, {id:413, name:"Other / Unspecified"}];

               $scope.message = "<div class='alert'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
                                                       +"success..</div>";
            }).
            error(function() {
                $scope.message = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
                                        +"Error while adding science discipline</div>";
            });
        };

        $scope.addDiscipline = function(disciplineInfo) {
                console.log($scope.username);
                disciplineInfo.username = $scope.username;
        		$http({method:"POST", url: "updateScienceDiscipline", data:disciplineInfo, dataType: "json", cache:false}).
        		success(function(data,status) {
        			$scope.item.submitSuccess = true;
        		}).
        		error(function(data,status) {
        			$scope.item.submitSuccess = true;
        		});
        	};
		
		$scope.logout = function() {
			UserService.logout().success(function() {
				$scope.username = "";
				$scope.authenticated = false;
				$scope.$emit("UserLogin",{ username:$scope.username, authenticated:$scope.authenticated});
				$scope.message = "<div class='alert alert-success'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
				+"User Logged out of Science Gateway. To logout of CAS, <a href='https://cas.iu.edu/cas/logout'>Click here</a></div>";
			}).
			error(function() {
				$scope.message = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
										+"Oops. There was a problem logging out. Please try again</div>";
			});
		};
	});

var FooterCtrl = function ($scope) {
	$scope.year = new Date().toJSON().substring(0,4);
};

$(document).ready(function() {

	$("[rel=filterTooltip]").tooltip({ placement: "right", title:"To invert filter functionality, use ! before your text"});

});

navigator.sayswho= (function(){
	  var N= navigator.appName, ua= navigator.userAgent, tem;
	  var M= ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
	  if(M && (tem= ua.match(/version\/([\.\d]+)/i))!= null) M[2]= tem[1];
	  M= M? [M[1], M[2]]: [N, navigator.appVersion,'-?'];
	  return M;
	 })();

var getURLParamValue = function(paramName){
	if(paramName=(new RegExp("[?&]"+encodeURIComponent(paramName)+"=([^&]*)")).exec(location.search))
		return decodeURIComponent(paramName[1]);
};

var secondsToString = function(seconds) {
	var numyears = Math.floor(seconds / 31536000);
	var numdays = Math.floor((seconds % 31536000) / 86400); 
	var numhours = Math.floor(((seconds % 31536000) % 86400) / 3600);
	var numminutes = Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
	var numseconds = (((seconds % 31536000) % 86400) % 3600) % 60;
	if(numyears>0) 
		return numyears + " yrs " +  numdays + " days " + numhours + " hrs " + numminutes + " mins " + numseconds + " secs";
	else if(numdays>0)
		return numdays + " days " + numhours + " hrs " + numminutes + " mins " + numseconds + " secs";
	else
		return numhours + " hrs " + numminutes + " mins " + numseconds + " secs";
};

if (typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function (str){
		return !this.indexOf(str);
	};
}

(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-40390259-1', 'iu.edu');
ga('send', 'pageview');
