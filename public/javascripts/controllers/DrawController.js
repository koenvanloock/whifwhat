var TournamentManagement;
(function (TournamentManagement) {
    var DrawController = (function () {
        function DrawController(tournamentService, drawService, seriesRoundService, $routeParams, $location) {
            var _this = this;
            this.tournamentService = tournamentService;
            this.drawService = drawService;
            this.seriesRoundService = seriesRoundService;
            this.$routeParams = $routeParams;
            this.$location = $location;
            tournamentService.getTournament($routeParams["tournamentId"]).then(function (response) {
                _this.tournament = response.data;
                _this.errors = new Array(_this.tournament.series.length);
                drawService.drawInitial($routeParams["tournamentId"]).then(function (drawResponse) {
                    drawResponse.data.map(function (drawResult) {
                        _this.tournament.series.map(function (series, index) {
                            if (series.id == drawResult.seriesId) {
                                if (!series.rounds) {
                                    series.rounds = [];
                                }
                                series.rounds.push(drawResult.round);
                                _this.errors[index] = drawResult.error;
                            }
                        });
                    });
                });
            });
        }
        DrawController.prototype.redraw = function (seriesRound) {
            var _this = this;
            this.drawService.redraw(seriesRound).then(function (roundResponse) {
                var newRound = roundResponse.data.round;
                console.log(newRound);
                _this.tournament.series.map(function (series, seriesIndex) {
                    if (series.id === newRound.seriesId) {
                        series.rounds.map(function (round, roundIndex) {
                            if (round.id === newRound.id) {
                                _this.tournament.series[seriesIndex].rounds[roundIndex] = newRound;
                            }
                        });
                    }
                });
            });
        };
        DrawController.prototype.updateSeriesRound = function (seriesRound) {
            this.seriesRoundService.updateSeriesRound(seriesRound).then(function (response) { return console.log("saved " + seriesRound.id); });
        };
        DrawController.prototype.printRound = function (roundId) {
            this.$location.path("/printRound/" + roundId);
        };
        DrawController.$inject = ["TournamentService", "DrawService", "SeriesRoundService", "$routeParams", "$location"];
        return DrawController;
    }());
    angular.module("managerControllers").controller("DrawController", DrawController);
})(TournamentManagement || (TournamentManagement = {}));
