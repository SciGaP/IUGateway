var disciplineApp = angular.module("disciplineApp", ["user"]);

disciplineApp.controller("DisciplineCtrl", function ($scope, $http) {
        $http({method: "GET", url: "getUsersScienceDiscipline", cache: false}).
            success(function (savedDiscipline, status) {
                $http({method: "GET", url: "getScienceDiscipline", cache: false}).
                    success(function (allDisciplines, status) {
                        $scope.item = {};
                        $scope.disciplineForm = {};
//                        $scope.item.primaryDisc = [];
//                        $scope.item.primarySubDisc = [];
//                        $scope.item.secondaryDisc = [];
//                        $scope.item.secondarySubDisc = [];
//                        $scope.item.tertiaryDisc = [];
//                        $scope.item.tertiarySubDisc = [];
                        $scope.allDisciplines = getDisciplines(allDisciplines);
                        var selectedDisciplines = getUserDisciplines(allDisciplines, savedDiscipline);
                        console.log($scope.allDisciplines);
                        console.log(selectedDisciplines);
                        if ($scope.selectedDisciplines != undefined && $scope.selectedDisciplines.length > 0){
                            $scope.item.primaryDisc = $scope.allDisciplines[selectedDisciplines[0].index];
                            $scope.item.primarySubDisc = $scope.allDisciplines[selectedDisciplines[0].index].subdisciplines[[selectedDisciplines[0].sindex]];
                            //$scope.item.primaryDisc = $scope.allDisciplines[1];
                            //$scope.item.primarySubDisc = $scope.allDisciplines[1].subdisciplines;
                            if ($scope.selectedDisciplines[1] != undefined){
                                $scope.item.secondaryDisc = $scope.selectedDisciplines[1];
                                $scope.item.secondarySubDisc = $scope.selectedDisciplines[1].subdisciplines;
                            } else {
                                //$scope.item.secondaryDisc.name = "-Select-";
                                //$scope.item.secondarySubDisc.name = "-Select-";
                            }
                            if ($scope.selectedDisciplines[2] != undefined){
                                $scope.item.tertiaryDisc = $scope.selectedDisciplines[2];
                                $scope.item.tertiarySubDisc = $scope.selectedDisciplines[2].subdisciplines;
                            }
//                            }else{
//                                $scope.item.tertiaryDisc.name = "-Select-";
//                                $scope.item.tertiarySubDisc = "-Select-";
//                            }
                        }else{
                            $scope.item.primaryDisc.name = "-Select-";
                            $scope.item.primarySubDisc.name = "-Select-";
                            $scope.item.secondaryDisc.name = "-Select-";
                            $scope.item.secondarySubDisc.name = "-Select-";
                            $scope.item.tertiaryDisc.name = "-Select-";
                            $scope.item.tertiarySubDisc = "-Select-";
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
            var id1 = $scope.item.primaryDisc.id;
            $scope.subdisciplines1 = getSubdisciplines(id1,$scope.allDisciplines);
        };

        $scope.getSubDisciplineList2 = function () {
            var id2 = $scope.item.secondaryDisc.id;
            $scope.subdisciplines2 = getSubdisciplines(id2,$scope.allDisciplines);
        };

        $scope.getSubDisciplineList3 = function () {
            var id3 = $scope.item.tertiaryDisc.id;
            $scope.subdisciplines3 = getSubdisciplines(id3,$scope.allDisciplines);
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


var getUserDisciplines = function (allDisciplines, savedDiscipline) {
    if (!savedDiscipline)   {
        return getDisciplines(allDisciplines);
    }
    var data1 = JSON.parse(savedDiscipline["disciplines"]);
    var disciplines = [];
    var discipline = {};
    var subdisciplines = [];
    for (var i = 0; i < data1.length; i++){
        var subdiscipline = {};
        subdiscipline.id = data1[i].id;
        subdiscipline.name = data1[i].name;
        discipline = getDisciplineContainsSubDiscipline(allDisciplines, subdiscipline.id);
        discipline.sindex = i;
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
                return discipline;
            }
        }
    }
}

var getSubdisciplines  = function(id, disciplines){
    for (var i = 0 ; i < disciplines.length; i++){
        var id1 = disciplines[i].id;
        if (id1 == id){
            return disciplines[i].subdisciplines;
        }
    }
    return null;
}

$(document).ready(function() {
    setTimeout(function() {
        $("#disciplineLink").parent().addClass("active");
    },50);
});


