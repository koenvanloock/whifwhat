var TournamentRunner;
(function (TournamentRunner) {
    var LoadingController = (function () {
        function LoadingController($location, loadService, alertService) {
            var _this = this;
            this.$location = $location;
            this.loadService = loadService;
            this.alertService = alertService;
            loadService.isThereActiveTournament().then(function (response) {
                if (response.data.hasTournament) {
                    console.log(response);
                    _this.gotoOverview();
                }
                else {
                    loadService.loadTournaments().then(function (result) {
                        _this.tournaments = result.data;
                    });
                }
            }, function (errorResponse) { return alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        }
        LoadingController.prototype.activateTournament = function (tournamentId) {
            var _this = this;
            this.loadService.activateTournament(tournamentId).then(function (result) {
                if (result.status === 200) {
                    _this.$location.path('/tournamentOverview');
                }
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        LoadingController.prototype.gotoOverview = function () {
            this.$location.path('/tournamentOverview');
        };
        return LoadingController;
    }());
    TournamentRunner.LoadingController = LoadingController;
    angular.module("tournamentRunner").controller("loadingController", ['$location', 'loadingService', "alertService", function ($location, loadingService, alertService) { return new LoadingController($location, loadingService, alertService); }]);
})(TournamentRunner || (TournamentRunner = {}));
