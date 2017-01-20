var TournamentRunner;
(function (TournamentRunner) {
    var LoadingService = (function () {
        function LoadingService($http, base) {
            this.$http = $http;
            this.base = base;
            this.loadTournaments = function () {
                return this.$http.get(this.base.url + "/tournaments");
            };
            this.activateTournament = function (tournamentId) {
                return this.$http.put(this.base.url + "/activeTournament/" + tournamentId);
            };
            this.getActiveTournament = function () {
                return this.$http.get(this.base.url + "/activeTournament");
            };
            this.getSeriesOfTournament = function (tournamentId) {
                return this.$http.get(this.base.url + "/series/" + tournamentId);
            };
            this.getRoundsOfActiveSeries = function (seriesId) {
                return this.$http.get(this.base.url + "/seriesrounds/" + seriesId);
            };
            this.updateSeriesRound = function (roundId, match) {
                return this.$http.patch(this.base.url + "/seriesrounds/" + roundId, match);
            };
        }
        return LoadingService;
    }());
    TournamentRunner.LoadingService = LoadingService;
    angular.module("tournamentRunner").factory("loadingService", ['$http', 'base', function ($http, base) { return new LoadingService($http, base); }]);
})(TournamentRunner || (TournamentRunner = {}));
