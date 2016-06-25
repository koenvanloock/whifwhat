module TournamentManagement{
    import IHttpService = angular.IHttpService;
    export class PlayerService{
        constructor(private $http: IHttpService, private base: any){}

        getAllPlayers() {
        return this.$http.get(this.base.url + '/players');
    }

        getRanks() {
        return this.$http.get(this.base.url + '/ranks')
    }
        postPlayer(player) {
        player.rank.value = parseInt(player.rank.value);
        return this.$http.post(this.base.url + '/player', player)
    }

        subscribePlayer(seriesPlayerList, seriesList, playerId) {
        return this.$http.post(this.base.url + '/seriesplayers', {"subscriptions": seriesPlayerList, seriesList: seriesList, playerId: playerId})
    }

        updatePlayer(player) {
        return this.$http.put(this.base.url + "/player", player)
    }

        deletePlayer(id) {
        return this.$http.delete(this.base.url + '/player/' + id)
    }

        getSeriesSubscriptionsOfPlayer(playerId, tournamentId){
        return this.$http.get(this.base.url + '/seriesofplayer/'+playerId+'/'+tournamentId)
    }
    }

    angular.module("managerServices").factory("PlayerService", ['$http', 'base', ($http, base) => {
        return new PlayerService($http, base);
    }]);

}