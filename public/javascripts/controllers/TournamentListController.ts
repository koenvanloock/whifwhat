module TournamentManagement{
    import IHttpResponseTransformer = angular.IHttpResponseTransformer;
    class TournamentListController{
        static $inject = ["TournamentService"];

        tournaments: Array<Tournament>;

        constructor( private tournamentService: TournamentService){
            tournamentService.getAllTournaments().success( (response: Array<Tournament>) => {console.log(response); this.tournaments = response});

        }


    }

    angular.module("managerControllers").controller("TournamentListController", TournamentListController);
}