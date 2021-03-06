module TournamentManagement {

    import IScope = angular.IScope;
    import IRouteParamsService = angular.route.IRouteParamsService;
    class SeriesSetupController {
        static $inject = ["$scope", "$mdDialog", "$location", "$routeParams", "TournamentService", "SeriesService", "alertService"];

        private currentTournament:Tournament;
        private numberOfSetsToWinList = [1, 2, 3, 4, 5];
        private extraHandicapForRecsList = [0, 1, 2, 3, 4, 5];
        private targetScores = [11, 21];

        constructor(private $scope:IScope,
                    private $mdDialog:ng.material.IDialogService,
                    private $location:angular.ILocationService,
                    private $routeParams:IRouteParamsService,
                    private tournamentService:TournamentService,
                    private seriesService:SeriesService,
                    private alertService:AlertService) {
            tournamentService.getTournament($routeParams['tournamentId']).then(
                (result:any)=> {
                    this.currentTournament = result.data;
                    tournamentService.setCurrentTournament(result.data);
                },
                (response: any) => {
                    alertService.addAlert({type: "error", msg: response.data, timeout: 3000});
                });


            $scope.$watch(() => tournamentService.getCurrentTournament(), (oldVal, newVal) => {
                return (oldVal != newVal) ? newVal : oldVal;
            });
        };

        createSeries(ev) {
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

            this.$mdDialog.show(dialog).then((newSeries) => {
                newSeries.tournamentId = tournamentToAddSeries.id;
                if (!newSeries.extraHandicapForRecs) newSeries.extraHandicapForRecs = 0;
                newSeries.setTargetScore = parseInt(newSeries.setTargetScore);
                newSeries.numberOfSetsToWin = parseInt(newSeries.numberOfSetsToWin);
                newSeries.currentRoundNr = tournamentToAddSeries.series ? tournamentToAddSeries.series.length + 1 : 1;
                seriesService.addSeries(newSeries).then((insertedSeries) => {
                    if (!tournamentToAddSeries.series) tournamentToAddSeries.series = [];
                    tournamentToAddSeries.series.push(newSeries);
                    console.log(this.currentTournament);
                });

            });
        };

        updateSeries(index) {
            var seriesToUpdate = this.currentTournament.series[index];
            seriesToUpdate.setTargetScore = parseInt(seriesToUpdate.setTargetScore);
            this.seriesService.updateSeries(seriesToUpdate);
        };

        gotoPlayerSubscription(event) {
            if (this.currentTournament.series.length > 0) {
                this.$location.path("/tournament/" + this.$routeParams['tournamentId'] + "/playerSubscription");
            } else {
                function showNoSeries(event) {
                    this.$mdDialog.show(
                        this.$mdDialog.alert()
                            .clickOutsideToClose(true)
                            .title('Geen reeksen in dit tornooi')
                            .content('U hebt geen reeksen gemaakt in een tornooi met meerdere reeksen!')
                            .ariaLabel('Alert No series')
                            .ok('OK')
                            .targetEvent(event)
                    );
                }

                showNoSeries(event);
            }
        }


        deleteSeries(event, index, seriesId, seriesName) {

            var confirm = this.$mdDialog.confirm()
                .clickOutsideToClose(true)
                .title('delete reeks ' + seriesName + "?")
                .textContent('delete reeks ' + seriesName + "?")
                .ariaLabel('deleteRoundDialog')
                .targetEvent(event)
                .ok('delete')
                .cancel("annuleren");

            this.$mdDialog.show(confirm).then(
                () =>
                    this.seriesService.deleteSeries(seriesId).then((response) => this.currentTournament.series.splice(index, 1))
                , () => {
                });


        }

    }

    angular.module("managerControllers").controller("SeriesSetupController", SeriesSetupController);
}