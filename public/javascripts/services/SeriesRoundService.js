var TournamentManagement;
(function (TournamentManagement) {
    var SeriesRoundService = (function () {
        function SeriesRoundService($http, base) {
            this.$http = $http;
            this.base = base;
            this.roundsMap = {};
        }
        SeriesRoundService.prototype.getRound = function (roundId) {
            return this.$http.get("/round/" + roundId);
        };
        SeriesRoundService.prototype.proceedToNextRound = function (seriesId, roundNr) {
            return this.$http.get("/nextRound/" + seriesId + '/' + roundNr);
        };
        SeriesRoundService.prototype.previousRound = function (seriesId, roundNr) {
            return this.$http.get("/previousRound/" + seriesId + "/" + roundNr);
        };
        SeriesRoundService.prototype.getMatchListOfRound = function (roundId) {
            return this.$http.get("/matchList/" + roundId);
        };
        SeriesRoundService.prototype.createSeriesRound = function (seriesId) {
            return this.$http.post(this.base.url + "/seriesrounds/" + seriesId, {});
        };
        SeriesRoundService.prototype.updateSeriesRound = function (seriesRoundJson) {
            return this.$http.put(this.base.url + "/seriesrounds", seriesRoundJson);
        };
        SeriesRoundService.prototype.updateSeriesRoundConfig = function (seriesRoundJson) {
            return this.$http.put(this.base.url + "/seriesroundsconfig", seriesRoundJson);
        };
        SeriesRoundService.prototype.deleteSeriesRound = function (roundId) {
            return this.$http.delete(this.base.url + "/seriesrounds/" + roundId);
        };
        SeriesRoundService.prototype.loadRoundsOfSeries = function (seriesId) {
            var _this = this;
            return this.$http.get(this.base.url + "/seriesrounds/" + seriesId).then(function (result) {
                _this.roundsMap[seriesId] = result.data;
                return _this.roundsMap[seriesId] ? _this.roundsMap[seriesId] : [];
            });
        };
        SeriesRoundService.prototype.getRoundsOfSeries = function (seriesId) {
            return this.roundsMap[seriesId] ? this.roundsMap[seriesId] : [];
        };
        SeriesRoundService.prototype.getRoundCountOfSeries = function (seriesId) {
            return this.roundsMap[seriesId] ? this.roundsMap[seriesId].length : 0;
        };
        SeriesRoundService.prototype.showRoundResult = function (roundId) {
            return this.$http.get(this.base.url + "/roundresult/" + roundId);
        };
        SeriesRoundService.prototype.convertRoundType = function (round) {
            if (round.roundType) {
                switch (round.roundType) {
                    case "B":
                    default:
                        round.roundType = { type: "B", name: "Tabel" };
                        break;
                    case "R":
                        round.roundType = { type: "R", name: "Poulerounde" };
                }
            }
            return round;
        };
        return SeriesRoundService;
    }());
    TournamentManagement.SeriesRoundService = SeriesRoundService;
    angular.module("managerServices").factory("SeriesRoundService", ['$http', 'base', function ($http, base) {
            return new SeriesRoundService($http, base);
        }]);
})(TournamentManagement || (TournamentManagement = {}));
