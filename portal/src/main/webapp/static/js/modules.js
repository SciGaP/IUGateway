var moduleSearchApp = angular.module("moduleSearchApp", ["user"]);
moduleSearchApp.controller("SearchCtrl", function($scope, $http) {
	loadData($scope,$http,"modules/all");
	
	$scope.showDetails = function(module) {
		$scope.$broadcast("moduleSelection", module);
	};
});

moduleSearchApp.controller("DetailsCtrl", function($scope, $http) {
	$scope.$on("moduleSelection", function(emitEvent, moduleName) {
		$scope.name = moduleName;
		$http({method:"GET", url:"modules/"+moduleName}).
		success(function(data,status) {
			$scope.detailLoadError = false;
			var clusters = {};
			data.forEach(function(module) {
				if(clusters[module.cluster]==undefined) {
					var cluster = {};
					cluster.name = module.cluster;
					cluster.versions = [];
					cluster.versions.push(module.version);
					cluster.description = module.description;
					clusters[module.cluster] = cluster;
				}
				else
					clusters[module.cluster].versions.push(module.version);
			});
			$scope.clusters = clusters;
		}).
		error(function(data, status) {
			$scope.detailLoadError = true;
		});
	});
});

/*var modulesApp = angular.module("modulesApp", ["user","urlprovider"]);
modulesApp.controller("ModulesCtrl", function($scope, $http, UrlProvider) {
	var machine = getURLParamValue("machine");
	var url = UrlProvider.getUrlForMachine(machine);
	var name = UrlProvider.getNameForMachine(machine);
	if(url == undefined || name == undefined) {
		$scope.machineName = "Unrecognized machine";
		$scope.hideLoader = true;
        $scope.showError = true;
	} else {
		$scope.machineName = name;
		loadData($scope,$http,"modules"+url);
	}
});*/


var loadData = function($scope,$http,url) {
	$http({method: "GET", url: url, cache: true}).
	success(function(data, status) {
        /*var updatedData = new Array();
        for (var i = 0; i < data.length; i++) {
            if (data[i].description == ""){
                data[i].description = "N/A";
            }
            if (data[i].cluster == "tds"){
                data[i].cluster = "Big Red II";
            }
            updatedData[i] = data[i];
        }*/
		$scope.modules = data;
		$scope.hideLoader = true;
	}).
	error(function(data, status) {
		$scope.hideLoader = true;
		$scope.showError = true;
    });
};

$(document).ready(function() {
	setTimeout(function() {
		$("#infoLink").parent().addClass("active");
	},50);
});