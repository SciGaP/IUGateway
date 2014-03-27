var userApp = angular.module("userApp", ["user", "urlprovider"]);

// Enabling CORS in Angular
userApp.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}]);

angular.module("user", []).
    service("UserService",function ($http) {
        function service() {
            this.login = function () {
                return $http({method: "GET", url: "getUserinfo", cache: false}).
                    success(function (data, status) {
                    }).
                    error(function (data, status) {
                        console.log("Error getting user information");
                    });
            };
            this.logout = function () {
                return $http({method: "POST", url: "logout", cache: false}).
                    success(function (data, status) {
                    }).
                    error(function (data, status) {
                        console.log("Error logging out user !");
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

        $scope.profile = function () {
            $http({method: "GET", url: "getScienceDiscipline", cache: false}).
                success(function (data, status) {
                    $scope.disciplines = getDisciplines(data);
                }).
                error(function (data, status) {
                    console.log("Error getting profile !");
                });
        };

        $scope.getSubDisciplineList1 = function () {
            var id1 = $scope.item.primaryDisc.id;
            $scope.subdisciplines1 = getSubdisciplines(id1,$scope.disciplines);

            $scope.message = "<div class='alert'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
                + "success..</div>";
        };

        $scope.getSubDisciplineList2 = function () {
            var id2 = $scope.item.secondaryDisc.id;
            $scope.subdisciplines2 = getSubdisciplines(id2,$scope.disciplines);

            $scope.message = "<div class='alert'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
                + "success..</div>";
        };

        $scope.getSubDisciplineList3 = function () {
            var id3 = $scope.item.tertiaryDisc.id;
            $scope.subdisciplines3 = getSubdisciplines(id3,$scope.disciplines);

            $scope.message = "<div class='alert'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
                + "success..</div>";
        };

        $scope.addDiscipline = function (disciplineInfo) {
            if (disciplineInfo == undefined){
                $scope.submitDisabled = true;
            } else{
                var d = new Date();
                var month = d.getMonth()+1;
                var day = d.getDate();

                var date = d.getFullYear() + '-' +
                    (month<10 ? '0' : '') + month + '-' +
                    (day<10 ? '0' : '') + day;
                disciplineInfo.username = $scope.username;
                disciplineInfo.date = date;
                var url = "updateScienceDiscipline";
                $http({method:"POST", url:"updateScienceDiscipline", data:disciplineInfo, dataType:"json", cache:false}).
                    success(function (data, status) {
                        $scope.submitSuccess = true;
//                    $('#myModal').modal('hide');
                        $scope.item = null;
                    }).
                    error(function (data, status) {
                        $scope.submitDisabled = true;
                    });
            }
        };

        $scope.logout = function () {
            UserService.logout().success(function () {
                $scope.username = "";
                $scope.authenticated = false;
                $scope.$emit("UserLogin", { username: $scope.username, authenticated: $scope.authenticated});
                $scope.message = "<div class='alert alert-success'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
                    + "User Logged out of Science Gateway. To logout of CAS, <a href='https://cas.iu.edu/cas/logout'>Click here</a></div>";
            }).
                error(function () {
                    $scope.message = "<div class='alert alert-error'><button type='button' class='close' data-dismiss='alert'>&times;</button>"
                        + "Oops. There was a problem logging out. Please try again</div>";
                });
        };
    });

var getDisciplines = function (data) {
    var disciplines = [];
    for (var i in data) {
        var discipline = {};
        var subdisciplines = [];
        discipline.name = data[i].name;
        discipline.id = data[i].id;
        subdisciplines = data[i].subdisciplines;
        for (var j in subdisciplines){
            var subdiscipline = {};
            subdiscipline.name = subdisciplines[j].name;
            subdiscipline.id = subdisciplines[j].id;
            subdisciplines.push(subdiscipline);
        }
        discipline.subdisciplines = subdisciplines;
        disciplines.push(discipline);
    }
    return disciplines;
}

var getSubdisciplines  = function(id, disciplines){
    for (var i in disciplines){
        var id1 = disciplines[i].id;
        if (id1 == id){
            return disciplines[i].subdisciplines;
        }
    }
    return null;
}

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
