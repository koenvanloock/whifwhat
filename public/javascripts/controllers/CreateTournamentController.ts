module TournamentManagement{

    class CreateTournamentController{
        static $inject = ["TournamentService","$location", "alertService"];

        private name: string;
        private tournamentDate: Date;
        private hasMultipleSeries: boolean;
        private maximumNumberOfSeriesEntries: number;
        private showClub: boolean;
        private playingWithHandicaps: boolean;
        private showReferees: boolean;
        private setTargetScore: number;
        private numberOfSetsToWin: number;
        private extraHandicapForRecs: number;
        private seriesColor: string;
        private numberList = [1,2,3,4,5];
        private numberOfSetsToWinList = [1, 2, 3, 4, 5];
        private extraHandicapForRecsList = [0, 1, 2, 3, 4, 5];
        private targetScores = [11, 21];

        constructor(private tournamentService: TournamentService, private $location: angular.ILocationService, private alertService: AlertService){
            this.playingWithHandicaps = false;
            this.showReferees = false;
            this.extraHandicapForRecs = 0;
            this.seriesColor = "#ffffff"
        }

        createTournament(){
            this.tournamentService.addTournament(this.name,this.tournamentDate, this.hasMultipleSeries, this.maximumNumberOfSeriesEntries, this.showClub, parseInt(this.setTargetScore), this.numberOfSetsToWin, this.playingWithHandicaps, this.extraHandicapForRecs, this.seriesColor, this.showReferees).then(
                (response) => {
                this.tournamentService.getTournament(response.data.id).then(
                    (result) =>{
                            var selectedTournament: any = result.data;
                            this.tournamentService.setCurrentTournament(selectedTournament);
                            if(selectedTournament.hasMultipleSeries) {
                                this.$location.path('/tournament/' + selectedTournament.id + "/seriesSetup");
                            } else{
                                this.$location.path('/tournament/' + selectedTournament.id + '/playerSubscription');
                            }
                        },
                    (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
                )
            },
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            )
        }

        flipPlayingWithHandicaps() {
            this.playingWithHandicaps = !this.playingWithHandicaps
        };

        flipShowReferees() {
            this.showReferees = !this.showReferees
        };
    }

    angular.module("managerControllers").controller("CreateTournamentController", CreateTournamentController);
}