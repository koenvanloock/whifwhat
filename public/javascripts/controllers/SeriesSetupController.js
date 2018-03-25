var TournamentManagement;
(function (TournamentManagement) {
    var SeriesSetupController = (function () {
        function SeriesSetupController($scope, $mdDialog, $location, $routeParams, tournamentService, seriesService, alertService) {
            var _this = this;
            this.$scope = $scope;
            this.$mdDialog = $mdDialog;
            this.$location = $location;
            this.$routeParams = $routeParams;
            this.tournamentService = tournamentService;
            this.seriesService = seriesService;
            this.alertService = alertService;
            this.numberOfSetsToWinList = [1, 2, 3, 4, 5];
            this.extraHandicapForRecsList = [0, 1, 2, 3, 4, 5];
            this.targetScores = [11, 21];
            tournamentService.getTournament($routeParams['tournamentId']).then(function (result) {
                _this.currentTournament = result.data;
                tournamentService.setCurrentTournament(result.data);
            }, function (response) {
                alertService.addAlert({ type: "error", msg: response.data, timeout: 3000 });
            });
            $scope.$watch(function () { return tournamentService.getCurrentTournament(); }, function (oldVal, newVal) {
                return (oldVal != newVal) ? newVal : oldVal;
            });
        }
        ;
        SeriesSetupController.prototype.createSeries = function (ev) {
            var _this = this;
            var tournamentToAddSeries = this.currentTournament;
            var seriesService = this.seriesService;
            var dialog = this.$mdDialog.alert({
                controller: "CreateSeriesController",
                controllerAs: "createSeriesCtrl",
                templateUrl: "assets/dialogs/createSeriesDialog.html",
                locals: {
                    tournament: this.currentTournament
                },
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: true
            });
            this.$mdDialog.show(dialog).then(function (newSeries) {
                newSeries.tournamentId = tournamentToAddSeries.id;
                if (!newSeries.extraHandicapForRecs)
                    newSeries.extraHandicapForRecs = 0;
                newSeries.setTargetScore = parseInt(newSeries.setTargetScore);
                newSeries.numberOfSetsToWin = parseInt(newSeries.numberOfSetsToWin);
                newSeries.currentRoundNr = tournamentToAddSeries.series ? tournamentToAddSeries.series.length + 1 : 1;
                seriesService.addSeries(newSeries).then(function (insertedSeries) {
                    if (!tournamentToAddSeries.series)
                        tournamentToAddSeries.series = [];
                    tournamentToAddSeries.series.push(newSeries);
                    console.log(_this.currentTournament);
                });
            });
        };
        ;
        SeriesSetupController.prototype.updateSeries = function (index) {
            var seriesToUpdate = this.currentTournament.series[index];
            seriesToUpdate.setTargetScore = parseInt(seriesToUpdate.setTargetScore);
            this.seriesService.updateSeries(seriesToUpdate);
        };
        ;
        SeriesSetupController.prototype.gotoPlayerSubscription = function (event) {
            if (this.currentTournament.series.length > 0) {
                this.$location.path("/tournament/" + this.$routeParams['tournamentId'] + "/playerSubscription");
            }
            else {
                function showNoSeries(event) {
                    this.$mdDialog.show(this.$mdDialog.alert()
                        .clickOutsideToClose(true)
                        .title('Geen reeksen in dit tornooi')
                        .content('U hebt geen reeksen gemaakt in een tornooi met meerdere reeksen!')
                        .ariaLabel('Alert No series')
                        .ok('OK')
                        .targetEvent(event));
                }
                showNoSeries(event);
            }
        };
        SeriesSetupController.prototype.deleteSeries = function (event, index, seriesId, seriesName) {
            var _this = this;
            var confirm = this.$mdDialog.confirm()
                .clickOutsideToClose(true)
                .title('delete reeks ' + seriesName + "?")
                .textContent('delete reeks ' + seriesName + "?")
                .ariaLabel('deleteRoundDialog')
                .targetEvent(event)
                .ok('delete')
                .cancel("annuleren");
            this.$mdDialog.show(confirm).then(function () {
                return _this.seriesService.deleteSeries(seriesId).then(function (response) { return _this.currentTournament.series.splice(index, 1); });
            }, function () {
            });
        };
        SeriesSetupController.$inject = ["$scope", "$mdDialog", "$location", "$routeParams", "TournamentService", "SeriesService", "alertService"];
        return SeriesSetupController;
    }());
    angular.module("managerControllers").controller("SeriesSetupController", SeriesSetupController);
})(TournamentManagement || (TournamentManagement = {}));
