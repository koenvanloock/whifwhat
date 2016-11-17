module TournamentManagement {
    import IRouteParamsService = angular.route.IRouteParamsService;

    class DrawController {
        static $inject = ["TournamentService", "DrawService","SeriesRoundService", "$routeParams"];
        private tournament: Tournament;
        private errors: Array<string>;

        constructor(private tournamentService: TournamentService, private drawService: DrawService, private seriesRoundService: SeriesRoundService, private $routeParams: IRouteParamsService) {
            tournamentService.getTournament($routeParams["tournamentId"]).then((response: any) => {

                this.tournament = response.data;
                this.errors = new Array(this.tournament.series.length);
                drawService.drawInitial($routeParams["tournamentId"]).then(
                    (drawResponse: any) => {
                        console.log(drawResponse.data);

                        drawResponse.data.map(  drawResult => {

                         this.tournament.series.map( (series, index) =>{
                            if(series.id == drawResult.seriesId){
                                if(!series.rounds){
                                    series.rounds = [];
                                }
                                console.log(drawResult.round);
                                series.rounds.push(drawResult.round);
                                this.errors[index] = drawResult.error;
                            }

                         })
                        })

                    }
                )
            })
        }

        redraw(seriesRound: SeriesRound){
            this.drawService.redraw(seriesRound).then( (roundResponse: any) => {
                var newRound = roundResponse.data.round;
                console.log(newRound);
            this.tournament.series.map( (series,seriesIndex) => {
                if(series.id === newRound.seriesId){
                    series.rounds.map( (round,roundIndex) => {
                        if(round.id === newRound.id){
                            this.tournament.series[seriesIndex].rounds[roundIndex] = newRound;
                        }

                    })
                }

            })
            })
        }

        updateSeriesRound(seriesRound: SeriesRound){
            this.seriesRoundService.updateSeriesRound(seriesRound).then(
                (response) => console.log("saved " + seriesRound.id)
            )
        }



    }

    angular.module("managerControllers").controller("DrawController", DrawController);

}