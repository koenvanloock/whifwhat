module TournamentManagement{
    class TournamentListController{
        static $inject = ["TournamentService"];

        tournaments: Array<Tournament>;

        constructor( private tournamentService: TournamentService){
            tournamentService.getAllTournaments().success( (response: any) => {console.log(response);this.tournaments = response});

        }


    }

    angular.module("managerControllers").controller("TournamentListController", TournamentListController);
}