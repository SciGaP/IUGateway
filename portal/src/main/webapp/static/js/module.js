var moduleApp = angular.module("moduleApp", ["user"]);

moduleApp.controller("DetailsCtrl", function ($scope, $http) {
    $scope.name = getUrlParameter("name");
    $scope.hideLoader = false;
    $http({method: "GET", url: "modules/" + $scope.name}).
        success(function (data, status) {
            $scope.hideLoader = true;
            $scope.detailLoadError = false;
            var clusters = {};
            data.forEach(function (module) {
                if (clusters[module.cluster] == undefined) {
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
        error(function (data, status) {
            $scope.detailLoadError = true;
        });
//	});
});

function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('?');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}

$(document).ready(function () {
    setTimeout(function () {
        $("#infoLink").parent().addClass("active");
    }, 50);
});