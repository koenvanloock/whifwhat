module TournamentRunner{

    import Tournament = TournamentManagement.Tournament;
    import ILocationProvider = angular.ILocationProvider;
    import ILocationService = angular.ILocationService;
    export class LoadingController{

        private tournaments: Array<Tournament>;

        constructor(private $location: ILocationService, private loadService: LoadingService){

            loadService.isThereActiveTournament().then( (response) => {
                if (response.data.hasTournament) {
                    console.log(response);
                    this.gotoOverview();
                } else {
                    loadService.loadTournaments().then((result) => {
                        this.tournaments = result.data
                    });
                }
            } )


        }

        activateTournament(tournamentId){
            this.loadService.activateTournament(tournamentId).then( (result) => {
                if(result.status === 200){
                    this.$location.path('/tournamentOverview');
                }
            })
        }

        gotoOverview(){
            this.$location.path('/tournamentOverview');
        }


    }

    angular.module("tournamentRunner").controller("loadingController", [ '$location','loadingService', ($location, loadingService) => new LoadingController($location, loadingService)]);
}
