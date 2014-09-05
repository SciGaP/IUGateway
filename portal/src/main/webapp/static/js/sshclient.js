var secureSSHApp = angular.module("secureSSHApp", ["user"]);

secureSSHApp.controller("SecureSSHCtrl", function ($scope, $http) {
    GateOne.init({url: "https://gw110.iu.xsede.org:9090",
                  theme: 'black',
                  fontSize: '130%'});
});

$(document).ready(function () {
//    GateOne.init({url: "https://gw110.iu.xsede.org:9090/"});
    setTimeout(function () {
        $("#sshLink").parent().addClass("active");
    }, 50);
});


