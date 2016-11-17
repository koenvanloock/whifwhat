module TournamentManagement {

    import IRootScopeService = angular.IRootScopeService;
    class SeriesRoundSetupController {

        static $inject = ["SeriesService", "SeriesRoundService", "$location", "$routeParams", "$rootScope"];

        private closed = [];
        seriesList = [];

        constructor(private seriesService:SeriesService, private roundService: SeriesRoundService, private $location:angular.ILocationService, private $routeParams:ng.route.IRouteParamsService, private $rootScope:IRootScopeService) {

            seriesService.getAllSeriesOfTournament(this.$routeParams["tournamentId"]).then(
                 (response: any) => {
                    this.seriesList = [];
                    response.data.map( (series) => {
                        roundService.loadRoundsOfSeries(series.id).then( rounds => {
                            series.rounds = rounds;
                            this.seriesList.push(series);
                            this.closed.push(true);
                        })
                    })
                });


            $rootScope.$watch(()  => roundService.getRoundsOfSeries,  (oldval, newval) => {
                if (oldval!=newval) {
                    this.seriesList[0].rounds = newval;
                }
            });
        }

        toggleOpenClosed(index) {
            this.closed[index] = !this.closed[index];
            this.selectionChanged(index);
        };

        isClosed(index) {
            return this.closed[index];
        };


        createSeriesRound(index) {

            this.closed[index] = false;
            if (this.seriesList[index].rounds == undefined) this.seriesList[index].rounds = [];
                this.roundService.createSeriesRound(this.seriesList[index].id).then((response) => {
                this.seriesList[index].rounds.push(response.data);
            })
        };

        gotoPlayerSubscription() {
            this.$location.path("/" + this.$routeParams["tournamentId"] + "/playerSubscription")
        };

        gotoDrawMenu() {
            this.$location.path("/drawMenu/" + this.$routeParams["tournamentId"]);
        };

        selectionChanged(index) {
            if(!this.seriesList[index].rounds || this.seriesList[index].rounds.length <1)
                this.seriesList[index].rounds = this.roundService.loadRoundsOfSeries(this.seriesList[index].id);

        };

        deleteRound(index, roundId, series){
            console.log("in de delete");
            this.roundService.deleteSeriesRound(roundId).then( (result) => {
                this.roundService.loadRoundsOfSeries(series.id).then( (roundsOfIndex) => {
                    series.rounds = roundsOfIndex;
                });
            })
        }

    }

    angular.module("managerControllers").controller("SeriesRoundSetupController", SeriesRoundSetupController)
}