var TournamentManagement;
(function (TournamentManagement) {
    var SeriesService = (function () {
        function SeriesService($http, base) {
            this.$http = $http;
            this.base = base;
        }
        SeriesService.prototype.getSeries = function (seriesId) {
            return this.$http.get(this.base.url + "/singleSeries/" + seriesId);
        };
        SeriesService.prototype.addSeries = function (series) {
            return this.$http.post(this.base.url + "/series", series);
        };
        SeriesService.prototype.updateSeries = function (series) {
            return this.$http.put(this.base.url + "/series/" + series.id, series);
        };
        SeriesService.prototype.deleteSeries = function (seriesId) {
            return this.$http.delete(this.base.url + "/series/" + seriesId);
        };
        SeriesService.prototype.fetchSeriesPlayers = function (seriesId) {
            return this.$http.get(this.base.url + "/seriesplayers/" + seriesId);
        };
        SeriesService.prototype.getAllSeriesOfTournament = function (tournamentId) {
            return this.$http.get(this.base.url + "/series/" + tournamentId);
        };
        return SeriesService;
    }());
    TournamentManagement.SeriesService = SeriesService;
    angular.module("managerServices").factory("SeriesService", ['$http', 'base', function ($http, base) {
            return new SeriesService($http, base);
        }]);
})(TournamentManagement || (TournamentManagement = {}));
