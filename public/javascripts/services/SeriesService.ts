module TournamentManagement {

    export class SeriesService {

        constructor(private $http:angular.IHttpService, private base:any) {
        }

        getSeries(seriesId) {
            return this.$http.get(this.base.url + "/singleSeries/" + seriesId);
        }

        addSeries(series) {
            return this.$http.post(this.base.url + "/series", series);
        }

        updateSeries(series) {
            return this.$http.put(this.base.url + "/series/" + series.id, series);
        }

        deleteSeries(seriesId) {
            return this.$http.delete(this.base.url + "/series/" + seriesId);

        }

        fetchSeriesPlayers(seriesId) {
            return this.$http.get(this.base.url + "/seriesplayers/" + seriesId);
        }

        getAllSeriesOfTournament(tournamentId) {
            return this.$http.get(this.base.url + "/series/" + tournamentId)
        }

    }

    angular.module("managerServices").factory("SeriesService", ['$http', 'base', ($http, base) => {
        return new SeriesService($http, base);
    }]);
}