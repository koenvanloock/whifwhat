module TournamentManagement{
    import IHttpService = angular.IHttpService;
    export class PlayerService{
        constructor(private $http: IHttpService, private base: any){}

        getAllPlayers() {
        return this.$http.get(this.base.url + '/players');
    }
        
        getPlayersBySearch(searchString: string){
            return this.$http.get(this.base.url + "/players?searchString="+searchString);
        }

        getRanks() {
        return this.$http.get(this.base.url + '/ranks')
    }
        postPlayer(player) {
        player.rank.value = parseInt(player.rank.value);
        return this.$http.post(this.base.url + '/players', player)
    }

        subscribePlayer(seriesPlayerList, seriesList, player) {
        return this.$http.post(this.base.url + '/seriesplayers', {"subscriptions": seriesPlayerList, seriesList: seriesList, player: player})
    }

        updatePlayer(player) {
        return this.$http.put(this.base.url + "/players", player)
    }

        deletePlayer(id) {
        return this.$http.delete(this.base.url + '/players/' + id)
    }

        getSeriesSubscriptionsOfPlayer(playerId, tournamentId){
        return this.$http.get(this.base.url + '/seriesofplayer/'+playerId+'/'+tournamentId)
    }
    }

    angular.module("managerServices").factory("PlayerService", ['$http', 'base', ($http, base) => {
        return new PlayerService($http, base);
    }]);

}