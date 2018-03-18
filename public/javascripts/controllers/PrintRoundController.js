var TournamentManagement;
(function (TournamentManagement) {
    var PrintRoundController = (function () {
        function PrintRoundController(seriesRoundService, seriesService, $routeParams, $location, alertService) {
            var _this = this;
            this.seriesRoundService = seriesRoundService;
            this.seriesService = seriesService;
            this.$routeParams = $routeParams;
            this.$location = $location;
            this.alertService = alertService;
            this.economic = true;
            seriesRoundService.getRound(this.$routeParams["roundId"]).then(function (response) {
                _this.roundToPrint = response.data;
                seriesService.getSeries(_this.roundToPrint.seriesId).then(function (seriesResponse) {
                    _this.series = seriesResponse.data;
                    _this.matchStyle = { backgroundColor: _this.series.seriesColor, color: '#000000' };
                }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
                if (_this.roundToPrint.roundType === "R") {
                }
                else if (_this.roundToPrint.roundType === "B") {
                    seriesRoundService.getMatchListOfRound(_this.$routeParams["roundId"]).then(function (matchListResponse) { _this.matchList = matchListResponse.data; }, function (errorResponse) { return alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
                }
            });
        }
        return PrintRoundController;
    }());
    PrintRoundController.$inject = ["SeriesRoundService", "SeriesService", "$routeParams", "$location", "alertService"];
    TournamentManagement.PrintRoundController = PrintRoundController;
    angular.module("managerControllers").controller("PrintRoundController", PrintRoundController);
})(TournamentManagement || (TournamentManagement = {}));
