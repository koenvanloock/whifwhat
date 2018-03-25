var TournamentManagement;
(function (TournamentManagement) {
    var CreateTournamentController = (function () {
        function CreateTournamentController(tournamentService, $location, alertService) {
            this.tournamentService = tournamentService;
            this.$location = $location;
            this.alertService = alertService;
            this.numberList = [1, 2, 3, 4, 5];
            this.numberOfSetsToWinList = [1, 2, 3, 4, 5];
            this.extraHandicapForRecsList = [0, 1, 2, 3, 4, 5];
            this.targetScores = [11, 21];
            this.playingWithHandicaps = false;
            this.showReferees = false;
            this.extraHandicapForRecs = 0;
            this.seriesColor = "#ffffff";
        }
        CreateTournamentController.prototype.createTournament = function () {
            var _this = this;
            this.tournamentService.addTournament(this.name, this.tournamentDate, this.hasMultipleSeries, this.maximumNumberOfSeriesEntries, this.showClub, parseInt(this.setTargetScore), this.numberOfSetsToWin, this.playingWithHandicaps, this.extraHandicapForRecs, this.seriesColor, this.showReferees).then(function (response) {
                _this.tournamentService.getTournament(response.data.id).then(function (result) {
                    var selectedTournament = result.data;
                    _this.tournamentService.setCurrentTournament(selectedTournament);
                    if (selectedTournament.hasMultipleSeries) {
                        _this.$location.path('/tournament/' + selectedTournament.id + "/seriesSetup");
                    }
                    else {
                        _this.$location.path('/tournament/' + selectedTournament.id + '/playerSubscription');
                    }
                }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        CreateTournamentController.prototype.flipPlayingWithHandicaps = function () {
            this.playingWithHandicaps = !this.playingWithHandicaps;
        };
        ;
        CreateTournamentController.prototype.flipShowReferees = function () {
            this.showReferees = !this.showReferees;
        };
        ;
        CreateTournamentController.$inject = ["TournamentService", "$location", "alertService"];
        return CreateTournamentController;
    }());
    angular.module("managerControllers").controller("CreateTournamentController", CreateTournamentController);
})(TournamentManagement || (TournamentManagement = {}));
