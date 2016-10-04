module TournamentManagement {
    class CreateSeriesController {
        static $inject = ["$mdDialog","tournament"];

        private numberOfSetsToWinList = [1, 2, 3, 4, 5];
        private extraHandicapForRecsList = [0, 1, 2, 3, 4, 5];
        private targetScores = [11, 21];
        private series:Series;

        constructor(private $mdDialog:ng.material.IDialogService, private tournament: Tournament) {
            this.series = {
                "id": null,
                "seriesName": "",
                "seriesColor": "#ffffff",
                "setTargetScore": 21,
                "numberOfSetsToWin": 2,
                "playingWithHandicaps": false,
                "extraHandicapForRecs": 0,
                "showReferees": false,
                "currentRoundNr": null,
                "tournamentId": null
            };
        }

        flipPlayingWithHandicaps() {
            this.series.playingWithHandicaps = !this.series.playingWithHandicaps
        };

        flipShowReferees() {
            this.series.showReferees = !this.series.showReferees
        };

        hide() {
            this.$mdDialog.hide();
        };

        cancel() {
            this.$mdDialog.cancel();
        };

        answer() {
            //this.series.numberOfSetsToWin = parseInt(this.series.numberOfSetsToWin);
            //this.series.setTargetScore = parseInt(this.series.setTargetScore);
            console.log(this.series);
            this.$mdDialog.hide(this.series);
        };

    }

    angular.module("managerControllers").controller("CreateSeriesController", CreateSeriesController);

}