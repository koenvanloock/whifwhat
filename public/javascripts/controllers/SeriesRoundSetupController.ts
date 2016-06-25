module TournamentManagement {

    import IRootScopeService = angular.IRootScopeService;
    class SeriesRoundSetupController {

        static $inject = ["SeriesService", "SeriesRoundService", "$location", "$routeParams", "$rootScope"];

        private closed = [];
        private seriesList = [];

        constructor(private seriesService:SeriesService, private roundService: SeriesRoundService, private $location:angular.ILocationService, private $routeParams:ng.route.IRouteParamsService, private $rootScope:IRootScopeService) {

            seriesService.getAllSeriesOfTournament(this.$routeParams["tournamentId"]).then(
                 (response: any) => {
                    this.seriesList = [];
                    response.data.map( (series) => {
                        this.seriesList.push(series);
                        this.closed.push(true);
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
            return closed[index];
        };


        createSeriesRound(index) {
            var initSeriesRound = {
                seriesId: this.seriesList[index].seriesId,
                roundType: "B",
                numberOfBracketRounds: 0,
                numberOfRobinGroups: 0
            };
            closed[index] = false;
            if (this.seriesList[index].rounds == undefined) this.seriesList[index].rounds = [];
                this.roundService.createSeriesRound(initSeriesRound).then(function (response) {
                this.seriesList[index].rounds.push(response.data);
            })
        };

        gotoPlayerSubscription() {
            this.$location.path("/" + this.$routeParams["tournamentId"] + "/playerSubscription")
        };

        gotoMenu() {
            this.$location.path("/tournamentMenu/" + this.$routeParams["tournamentId"]);
        };

        selectionChanged(index) {
            this.roundService.loadRoundsOfSeries(this.seriesList[index].seriesId).then((roundList: any) => {
                this.roundService.setRoundsOfSeries(roundList);
                this.seriesList[index].rounds = this.roundService.getRoundsOfSeries();
                roundList.data.map( (item,index) => {this.closed[index] = false;});
            });

        }
    }

    angular.module("managerControllers").controller("SeriesRoundSetupController", SeriesRoundSetupController)
}