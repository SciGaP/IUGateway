var disciplineApp = angular.module("disciplineApp", ["user"]);

disciplineApp.controller("DisciplineCtrl", function ($scope, $http) {
        $http({method: "GET", url: "getScienceDiscipline", cache: false}).
            success(function (data, status) {
//                $scope.disciplines = getDisciplines(data);
                var savedDiscipline = "{\"disciplines\":[{\"name\":\"Informatics\",\"id\":180},{\"name\":\"Early Childhood Education\",\"id\":107}]}";
                $scope.disciplines = getUserDisciplines(data, savedDiscipline);
                console.log($scope.disciplines);

            }).
            error(function (data, status) {
                console.log("Error getting profile !");
            });

        $scope.getSubDisciplineList1 = function () {
            var id1 = $scope.item.primaryDisc.id;
            $scope.subdisciplines1 = getSubdisciplines(id1,$scope.disciplines);
        };

        $scope.getSubDisciplineList2 = function () {
            var id2 = $scope.item.secondaryDisc.id;
            $scope.subdisciplines2 = getSubdisciplines(id2,$scope.disciplines);
        };

        $scope.getSubDisciplineList3 = function () {
            var id3 = $scope.item.tertiaryDisc.id;
            $scope.subdisciplines3 = getSubdisciplines(id3,$scope.disciplines);
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
    savedDiscipline = JSON.parse(savedDiscipline);
    var disciplines = [];
    var discipline = {};
    var subdisciplines = [];
    var data1 = [];
    data1 = savedDiscipline.disciplines;
    for (var i in data1){
        var subdiscipline = {};
        subdiscipline.id = data1[i].id;
        subdiscipline.name = data1[i].name;
        discipline = getDisciplineContainsSubDiscipline(allDisciplines, subdiscipline.id);
        discipline.subdisciplines = subdiscipline;
        disciplines.push(discipline);
    }
    return disciplines;
}

var getDisciplineContainsSubDiscipline = function (allDisciplines, id){
    for (var i in allDisciplines) {
        var discipline = {};
        var subdisciplines = [];
        subdisciplines = allDisciplines[i].subdisciplines;
        for (var j in subdisciplines){
            var obj = subdisciplines[j];
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    if (key == id) {
                        discipline.id =  allDisciplines[i].id;
                        discipline.name = allDisciplines[i].name;
                        return discipline;
                    }
                }
            }
        }
    }
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

$(document).ready(function() {
    setTimeout(function() {
        $("#disciplineLink").parent().addClass("active");
    },50);
});


