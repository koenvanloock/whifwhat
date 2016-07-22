module TournamentManagement {
    import IRouteParamsService = angular.route.IRouteParamsService;

    class DrawController {
        static $inject = ["TournamentService", "DrawService", "$routeParams"];
        private tournament: Tournament;
        private errors: Array<string>;

        constructor(private tournamentService: TournamentService, private drawService, private $routeParams: IRouteParamsService) {
            tournamentService.getTournament($routeParams["tournamentId"]).then((response: any) => {

                this.tournament = response.data;
                this.errors = new Array(this.tournament.series.length);
                drawService.drawInitial($routeParams["tournamentId"]).then(
                    (drawResponse) => {
                        console.log(drawResponse.data);

                        drawResponse.data.map(  drawResult => {

                         this.tournament.series.map( (series, index) =>{
                            if(series.seriesId == drawResult.seriesId){
                                if(!series.rounds){
                                    series.rounds = [];
                                }
                                console.log(drawResult.round);
                                series.rounds[0] = drawResult.round;
                                this.errors[index] = drawResult.error;
                            }

                         })
                        })

                    }
                )
            })
        }



    }

    angular.module("managerControllers").controller("DrawController", DrawController);

}