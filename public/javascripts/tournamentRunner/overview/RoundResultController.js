var TournamentRunner;
(function (TournamentRunner) {
    var RoundResultController = (function () {
        function RoundResultController(alertService, $mdDialog, roundResult) {
            this.alertService = alertService;
            this.$mdDialog = $mdDialog;
            this.roundResult = roundResult;
        }
        RoundResultController.prototype.hide = function () {
            this.$mdDialog.hide();
        };
        ;
        RoundResultController.prototype.cancel = function () {
            this.$mdDialog.cancel();
        };
        ;
        RoundResultController.prototype.showNextRound = function () {
            this.$mdDialog.hide("drawNext");
        };
        ;
        return RoundResultController;
    }());
    angular.module("tournamentRunner").controller("RoundResultController", ['alertService', '$mdDialog', 'roundResult', function (alertService, $mdDialog, roundResult) { return new RoundResultController(alertService, $mdDialog, roundResult); }]);
})(TournamentRunner || (TournamentRunner = {}));
