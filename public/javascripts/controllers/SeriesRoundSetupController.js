var TournamentManagement;
(function (TournamentManagement) {
    var SeriesRoundSetupController = (function () {
        function SeriesRoundSetupController(seriesService, roundService, $location, $routeParams, $rootScope, alertService) {
            var _this = this;
            this.seriesService = seriesService;
            this.roundService = roundService;
            this.$location = $location;
            this.$routeParams = $routeParams;
            this.$rootScope = $rootScope;
            this.alertService = alertService;
            this.closed = [];
            this.seriesList = [];
            seriesService.getAllSeriesOfTournament(this.$routeParams["tournamentId"]).then(function (response) {
                _this.seriesList = [];
                response.data.map(function (series) {
                    roundService.loadRoundsOfSeries(series.id).then(function (rounds) {
                        series.rounds = rounds;
                        _this.seriesList.push(series);
                        _this.closed.push(true);
                    });
                });
            }, function (errorResponse) { return alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            $rootScope.$watch(function () { return roundService.getRoundsOfSeries; }, function (oldval, newval) {
                if (oldval != newval) {
                    _this.seriesList[0].rounds = newval;
                }
            });
        }
        SeriesRoundSetupController.prototype.toggleOpenClosed = function (index) {
            this.closed[index] = !this.closed[index];
            this.selectionChanged(index);
        };
        ;
        SeriesRoundSetupController.prototype.isClosed = function (index) {
            return this.closed[index];
        };
        ;
        SeriesRoundSetupController.prototype.createSeriesRound = function (index) {
            var _this = this;
            this.closed[index] = false;
            if (this.seriesList[index].rounds == undefined)
                this.seriesList[index].rounds = [];
            this.roundService.createSeriesRound(this.seriesList[index].id).then(function (response) {
                _this.seriesList[index].rounds.push(response.data);
            });
        };
        ;
        SeriesRoundSetupController.prototype.gotoPlayerSubscription = function () {
            this.$location.path("/tournament/" + this.$routeParams["tournamentId"] + "/playerSubscription");
        };
        ;
        SeriesRoundSetupController.prototype.gotoDrawMenu = function () {
            this.$location.path("/drawMenu/" + this.$routeParams["tournamentId"]);
        };
        ;
        SeriesRoundSetupController.prototype.selectionChanged = function (index) {
            if (!this.seriesList[index].rounds || this.seriesList[index].rounds.length < 1)
                this.seriesList[index].rounds = this.roundService.loadRoundsOfSeries(this.seriesList[index].id);
        };
        ;
        SeriesRoundSetupController.prototype.deleteRound = function (index, roundId, series) {
            var _this = this;
            this.roundService.deleteSeriesRound(roundId).then(function (result) {
                _this.roundService.loadRoundsOfSeries(series.id).then(function (roundsOfIndex) { series.rounds = roundsOfIndex; }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        ;
        return SeriesRoundSetupController;
    }());
    SeriesRoundSetupController.$inject = ["SeriesService", "SeriesRoundService", "$location", "$routeParams", "$rootScope", "alertService"];
    angular.module("managerControllers").controller("SeriesRoundSetupController", SeriesRoundSetupController);
})(TournamentManagement || (TournamentManagement = {}));
