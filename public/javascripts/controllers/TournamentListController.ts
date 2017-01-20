module TournamentManagement{
    import IHttpResponseTransformer = angular.IHttpResponseTransformer;
    import ILocationService = angular.ILocationService;
    class TournamentListController{
        static $inject = ["TournamentService", "$location"];

        tournaments: Array<Tournament>;

        constructor( private tournamentService: TournamentService, private $location: ILocationService){
            tournamentService.getAllTournaments().success( (response: Array<Tournament>) => {this.tournaments = response});

        }


        gotoSeriesSetup(tournamentId){
            return this.$location.path("/tournament/"+ tournamentId + "/seriesSetup");
        }

    }

    angular.module("managerControllers").controller("TournamentListController", TournamentListController);
}