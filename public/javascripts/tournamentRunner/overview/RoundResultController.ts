module TournamentRunner {
    import AlertService = TournamentManagement.AlertService;
    class RoundResultController {

        constructor(private alertService: AlertService, private $mdDialog:ng.material.IDialogService, private roundResult: any) {}

        hide() {
            this.$mdDialog.hide();
        };

        cancel() {
            this.$mdDialog.cancel();
        };

        showNextRound() {
            this.$mdDialog.hide("drawNext");
        };


    }

    angular.module("tournamentRunner").controller("RoundResultController", ['alertService', '$mdDialog', 'roundResult', (alertService, $mdDialog, roundResult) => new RoundResultController(alertService, $mdDialog, roundResult)])

}