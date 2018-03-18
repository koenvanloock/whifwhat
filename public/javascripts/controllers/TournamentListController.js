var TournamentManagement;
(function (TournamentManagement) {
    var TournamentListController = (function () {
        function TournamentListController(tournamentService, $location, alertService) {
            var _this = this;
            this.tournamentService = tournamentService;
            this.$location = $location;
            this.alertService = alertService;
            tournamentService.getAllTournaments().then(function (response) { _this.tournaments = response.data; }, function (errorResponse) { return alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        }
        TournamentListController.prototype.gotoSetup = function (tournamentId) {
            if (this.tournaments.filter(function (tournament) { return tournament.id === tournamentId; })[0].hasMultipleSeries) {
                return this.$location.path("/tournament/" + tournamentId + "/seriesSetup");
            }
            else {
                return this.$location.path("/tournament/" + tournamentId + "/playerSubscription");
            }
        };
        return TournamentListController;
    }());
    TournamentListController.$inject = ["TournamentService", "$location", "alertService"];
    angular.module("managerControllers").controller("TournamentListController", TournamentListController);
})(TournamentManagement || (TournamentManagement = {}));
