var disciplineApp = angular.module("disciplineApp", ["user"]);

disciplineApp.controller("DisciplineCtrl", function ($scope, $http) {
    $http({method: "GET", url: "getUsersScienceDiscipline", cache: false}).
        success(function (savedDiscipline, status) {
            $scope.submitDisabled = false;
            $http({method: "GET", url: "getScienceDiscipline", cache: false}).
                success(function (allDisciplines, status) {
                    $scope.item = {};
                   // $scope.disciplineForm = {};
                    $scope.allDisciplines = getDisciplines(allDisciplines);
                    $scope.selectedDisciplines = getUserDisciplines(allDisciplines, savedDiscipline);
                    console.log($scope.allDisciplines);
                    console.log($scope.selectedDisciplines);
                    $scope.item.primaryDisc = null;
                    $scope.item.primarySubDisc = null;
                    $scope.item.secondaryDisc = null;
                    $scope.item.secondarySubDisc = null;
                    $scope.item.tertiaryDisc = null;
                    $scope.item.tertiarySubDisc = null;
                    if ($scope.selectedDisciplines != undefined && $scope.selectedDisciplines.length > 0) {
                        $scope.item.primaryDisc = $scope.allDisciplines[$scope.selectedDisciplines[0].index];
                        $scope.item.primarySubDisc = $scope.allDisciplines[$scope.selectedDisciplines[0].index].subdisciplines[[$scope.selectedDisciplines[0].sindex]];
                        $scope.subdisciplines1 = getSubdisciplines($scope.item.primaryDisc.id, $scope.allDisciplines);
                        if ($scope.selectedDisciplines[1] != undefined) {
                            $scope.item.secondaryDisc = $scope.allDisciplines[$scope.selectedDisciplines[1].index];
                            $scope.item.secondarySubDisc = $scope.allDisciplines[$scope.selectedDisciplines[1].index].subdisciplines[[$scope.selectedDisciplines[1].sindex]];
                            $scope.subdisciplines2 = getSubdisciplines($scope.item.secondaryDisc.id, $scope.allDisciplines);
                        }
                        if ($scope.selectedDisciplines[2] != undefined) {
                            $scope.item.tertiaryDisc = $scope.allDisciplines[$scope.selectedDisciplines[2].index];
                            $scope.item.tertiarySubDisc = $scope.allDisciplines[$scope.selectedDisciplines[2].index].subdisciplines[[$scope.selectedDisciplines[2].sindex]];
                            $scope.subdisciplines3 = getSubdisciplines($scope.item.tertiaryDisc.id, $scope.allDisciplines);
                        }
                    } else {
                        $scope.item = null;
                    }
                }).
                error(function (data, status) {
                    console.log("Error getting user disciplines !");
                });
        }).
        error(function (data, status) {
            console.log("Error getting disciplines !");
        });

    $scope.getSubDisciplineList1 = function () {
        if ($scope.item.primaryDisc != undefined) {
            $scope.item.primarySubDisc = null;
            var id1 = $scope.item.primaryDisc.id;
            $scope.subdisciplines1 = getSubdisciplines(id1, $scope.allDisciplines);
        } else{
            $scope.item.primarySubDisc = null;
        }
    };

    $scope.getSubDisciplineList2 = function () {
        if ($scope.item.secondaryDisc != undefined) {
            $scope.item.secondarySubDisc = null;
            var id2 = $scope.item.secondaryDisc.id;
            $scope.subdisciplines2 = getSubdisciplines(id2, $scope.allDisciplines);
        } else{
            $scope.item.secondarySubDisc = null;
        }
    };

    $scope.getSubDisciplineList3 = function () {
        if ($scope.item.tertiaryDisc != undefined) {
            $scope.item.tertiarySubDisc = null;
            var id3 = $scope.item.tertiaryDisc.id;
            $scope.subdisciplines3 = getSubdisciplines(id3, $scope.allDisciplines);
        } else {
            $scope.item.tertiarySubDisc = null;
        }
    };

    $scope.addDiscipline = function (disciplineInfo) {
        if (disciplineInfo == undefined) {
            $scope.submitDisabled = true;
        } else {
            var d = new Date();
            var month = d.getMonth() + 1;
            var day = d.getDate();

            var date = d.getFullYear() + '-' +
                (month < 10 ? '0' : '') + month + '-' +
                (day < 10 ? '0' : '') + day;
            disciplineInfo.date = date;
            var url = "updateScienceDiscipline";
            $http({method: "POST", url: "updateScienceDiscipline", data: disciplineInfo, dataType: "json", cache: false}).
                success(function (data, status) {
                    $scope.submitSuccess = true;
//                    $('#myModal').modal('hide');
                    $scope.item = disciplineInfo;
                }).
                error(function (data, status) {
                    $scope.submitDisabled = true;
                });
        }
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
        for (var j in subdisciplines) {
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

var getUserDisciplines = function (allDisciplines, savedDiscipline) {
    if (!savedDiscipline) {
        return getDisciplines(allDisciplines);
    }
    var data1 = JSON.parse(savedDiscipline["disciplines"]);
    var disciplines = [];
    var discipline = {};
    var subdisciplines = [];
    for (var i = 0; i < data1.length; i++) {
        var subdiscipline = {};
        subdiscipline.id = data1[i].id;
        subdiscipline.name = data1[i].name;
        discipline = getDisciplineContainsSubDiscipline(allDisciplines, subdiscipline.id);
        disciplines.push(discipline);
    }
    return disciplines;
}

var getDisciplineContainsSubDiscipline = function (allDisciplines, id) {
    for (var i = 0; allDisciplines.length; i++) {
        var discipline = {};
        var subdisciplines = [];
        subdisciplines = allDisciplines[i].subdisciplines;
        for (var j = 0; j < subdisciplines.length; j++) {
            var obj = subdisciplines[j];
            if (obj.id == id) {
                discipline.index = i;
                discipline.sindex = j;
                return discipline;
            }
        }
    }
}

var getSubdisciplines = function (id, disciplines) {
    for (var i = 0; i < disciplines.length; i++) {
        var id1 = disciplines[i].id;
        if (id1 == id) {
            return disciplines[i].subdisciplines;
        }
    }
    return null;
}

$(document).ready(function () {
    setTimeout(function () {
        $("#disciplineLink").parent().addClass("active");
    }, 50);
});


