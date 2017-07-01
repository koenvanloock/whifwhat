module TournamentManagement {

    export class PrintRoundController {
        static $inject = ["SeriesRoundService", "SeriesService","$routeParams", "$location", "alertService"];


        private series: Series;
        private roundToPrint: SeriesRound;
        private matchList: Array<SiteMatch>;
        private matchStyle: any;
        private economic: boolean;
        
        constructor(private seriesRoundService: SeriesRoundService, private seriesService: SeriesService, private $routeParams, private $location, private alertService: AlertService){
            this.economic=true;
            seriesRoundService.getRound(this.$routeParams["roundId"]).then( (response) => {

                this.roundToPrint = response.data;
                seriesService.getSeries(this.roundToPrint.seriesId).then( 
                    (seriesResponse) => {
                            this.series = seriesResponse.data;
                            this.matchStyle = {backgroundColor: this.series.seriesColor, color: '#000000'};
                        },
                    (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})

                );
                
                if(this.roundToPrint.roundType === "R"){
                    // the round can be printed per robin
                } else if(this.roundToPrint.roundType === "B") {
                    seriesRoundService.getMatchListOfRound(this.$routeParams["roundId"]).then( 
                        (matchListResponse) => {this.matchList = matchListResponse.data},
                        (errorResponse) => alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
                    );
                }

            });
            
        }

    }
    angular.module("managerControllers").controller("PrintRoundController", PrintRoundController)
}
