module TournamentManagement{
    import IHttpResponseTransformer = angular.IHttpResponseTransformer;
    import ILocationService = angular.ILocationService;
    class TournamentListController{
        static $inject = ["TournamentService", "$location", "alertService"];

        tournaments: Array<Tournament>;

        constructor( private tournamentService: TournamentService, private $location: ILocationService, private alertService: AlertService){
            tournamentService.getAllTournaments().then( 
                (response: Array<Tournament>) => {this.tournaments = response.data},
                (errorResponse) => alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            );

        }


        gotoSetup(tournamentId){
            
            if(this.tournaments.filter((tournament) => tournament.id === tournamentId)[0].hasMultipleSeries){
                return this.$location.path("/tournament/"+ tournamentId + "/seriesSetup");
            } else{
                return this.$location.path("/tournament/"+ tournamentId + "/playerSubscription");
            }
            
        }

    }

    angular.module("managerControllers").controller("TournamentListController", TournamentListController);
}