var userApp = angular.module("userApp", ["user", "urlprovider"]);

angular.module("user", []).
    service("UserService",function ($http) {
        function service() {
            this.login = function () {
                return $http({method: "GET", url: "getRemoteUser", cache: false}).
                    success(function (data, status) {
                        console.log(data);
                    }).
                    error(function (data, status) {
                        console.log("Error getting user information");
                    });
            };
        };
        return new service();
    }).
    controller("UserCtrl", function ($scope, $http, UserService) {
        UserService.login().success(function (username) {
            if (username != undefined && username != null && username != "") {
                $scope.username = username;
                $scope.authenticated = true;
            } else {
                $scope.username = "";
                $scope.authenticated = false;
            }
            $scope.$emit("UserLogin", { username: $scope.username, authenticated: $scope.authenticated});
        }).
            error(function (data) {
                $scope.username = "";
                $scope.authenticated = false;
                $scope.$emit("UserLogin", { username: $scope.username, authenticated: $scope.authenticated});
            });

    });

var FooterCtrl = function ($scope) {
    $scope.year = new Date().toJSON().substring(0, 4);
};

$(document).ready(function () {

    $("[rel=filterTooltip]").tooltip({ placement: "right", title: "To invert filter functionality, use ! before your text"});

    $('body').on('hidden.bs.modal', '.modal', function () {
        $(this).removeData('bs.modal');
    });

});

navigator.sayswho = (function () {
    var N = navigator.appName, ua = navigator.userAgent, tem;
    var M = ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
    if (M && (tem = ua.match(/version\/([\.\d]+)/i)) != null) M[2] = tem[1];
    M = M ? [M[1], M[2]] : [N, navigator.appVersion, '-?'];
    return M;
})();

var getURLParamValue = function (paramName) {
    if (paramName = (new RegExp("[?&]" + encodeURIComponent(paramName) + "=([^&]*)")).exec(location.search))
        return decodeURIComponent(paramName[1]);
};

var secondsToString = function (seconds) {
    var numyears = Math.floor(seconds / 31536000);
    var numdays = Math.floor((seconds % 31536000) / 86400);
    var numhours = Math.floor(((seconds % 31536000) % 86400) / 3600);
    var numminutes = Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
    var numseconds = (((seconds % 31536000) % 86400) % 3600) % 60;
    if (numyears > 0)
        return numyears + " yrs " + numdays + " days " + numhours + " hrs " + numminutes + " mins " + numseconds + " secs";
    else if (numdays > 0)
        return numdays + " days " + numhours + " hrs " + numminutes + " mins " + numseconds + " secs";
    else
        return numhours + " hrs " + numminutes + " mins " + numseconds + " secs";
};

if (typeof String.prototype.startsWith != 'function') {
    String.prototype.startsWith = function (str) {
        return !this.indexOf(str);
    };
}

(function (i, s, o, g, r, a, m) {
    i['GoogleAnalyticsObject'] = r;
    i[r] = i[r] || function () {
        (i[r].q = i[r].q || []).push(arguments)
    }, i[r].l = 1 * new Date();
    a = s.createElement(o),
        m = s.getElementsByTagName(o)[0];
    a.async = 1;
    a.src = g;
    m.parentNode.insertBefore(a, m)
})(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

ga('create', 'UA-40390259-1', 'iu.edu');
ga('send', 'pageview');
