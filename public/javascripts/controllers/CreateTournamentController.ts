module TournamentManagement{

    class CreateTournamentController{
        static $inject = ["TournamentService","$location"];

        private name: string;
        private tournamentDate: Date;
        private hasMultipleSeries: boolean;
        private maximumNumberOfSeriesEntries: number;
        private showClub: boolean;
        private numberList = [1,2,3,4,5];

        constructor(private tournamentService: TournamentService, private $location: angular.ILocationService){

        }

        createTournament(){
            this.tournamentService.addTournament(this.name,this.tournamentDate, this.hasMultipleSeries, this.maximumNumberOfSeriesEntries, this.showClub)
            .success( (tournament: any) => {
                this.tournamentService.getTournament(tournament.tournamentId).then( function(result){
                    var selectedTournament = result.data;
                    this.tournamentService.setCurrentTournament(selectedTournament);
                    this.$location.path('/tournament/' + tournament.tournamentId + "/seriesSetup");
                });

            })};
    }

    angular.module("managerControllers").controller("CreateTournamentController", CreateTournamentController);
}