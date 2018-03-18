var TournamentManagement;
(function (TournamentManagement) {
    var CreateSeriesController = (function () {
        function CreateSeriesController($mdDialog, tournament) {
            this.$mdDialog = $mdDialog;
            this.tournament = tournament;
            this.numberOfSetsToWinList = [1, 2, 3, 4, 5];
            this.extraHandicapForRecsList = [0, 1, 2, 3, 4, 5];
            this.targetScores = [11, 21];
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
        CreateSeriesController.prototype.flipPlayingWithHandicaps = function () {
            this.series.playingWithHandicaps = !this.series.playingWithHandicaps;
        };
        ;
        CreateSeriesController.prototype.flipShowReferees = function () {
            this.series.showReferees = !this.series.showReferees;
        };
        ;
        CreateSeriesController.prototype.hide = function () {
            this.$mdDialog.hide();
        };
        ;
        CreateSeriesController.prototype.cancel = function () {
            this.$mdDialog.cancel();
        };
        ;
        CreateSeriesController.prototype.answer = function () {
            this.$mdDialog.hide(this.series);
        };
        ;
        return CreateSeriesController;
    }());
    CreateSeriesController.$inject = ["$mdDialog", "tournament"];
    angular.module("managerControllers").controller("CreateSeriesController", CreateSeriesController);
})(TournamentManagement || (TournamentManagement = {}));
