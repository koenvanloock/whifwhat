module TournamentRunner{

    import Tournament = TournamentManagement.Tournament;
    import ILocationProvider = angular.ILocationProvider;
    import ILocationService = angular.ILocationService;
    import AlertService = TournamentManagement.AlertService;
    export class LoadingController{

        private tournaments: Array<Tournament>;

        constructor(private $location: ILocationService, private loadService: LoadingService, private alertService: AlertService){

            loadService.isThereActiveTournament().then(
                (response) => {
                    if (response.data.hasTournament) {
                        console.log(response);
                        this.gotoOverview();
                    } else {
                        loadService.loadTournaments().then((result) => {
                            this.tournaments = result.data
                        });
                    }
                },
                (errorResponse) => alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            )


        }

        activateTournament(tournamentId){
            this.loadService.activateTournament(tournamentId).then( (result) => {
                if(result.status === 200){
                    this.$location.path('/tournamentOverview');
                }
            },
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            )
        }

        gotoOverview(){
            this.$location.path('/tournamentOverview');
        }


    }

    angular.module("tournamentRunner").controller("loadingController", [ '$location','loadingService', "alertService", ($location, loadingService, alertService) => new LoadingController($location, loadingService, alertService)]);
}
