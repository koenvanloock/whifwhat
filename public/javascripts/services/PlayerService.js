var TournamentManagement;
(function (TournamentManagement) {
    var PlayerService = (function () {
        function PlayerService($http, base) {
            this.$http = $http;
            this.base = base;
        }
        PlayerService.prototype.getAllPlayers = function () {
            return this.$http.get(this.base.url + '/players');
        };
        PlayerService.prototype.getPlayersBySearch = function (searchString) {
            return this.$http.get(this.base.url + "/players?searchString=" + searchString);
        };
        PlayerService.prototype.getRanks = function () {
            return this.$http.get(this.base.url + '/ranks');
        };
        PlayerService.prototype.postPlayer = function (player) {
            player.rank.value = parseInt(player.rank.value);
            return this.$http.post(this.base.url + '/players', player);
        };
        PlayerService.prototype.subscribePlayer = function (seriesPlayerList, seriesList, player) {
            return this.$http.post(this.base.url + '/seriesplayers', { "subscriptions": seriesPlayerList, seriesList: seriesList, player: player });
        };
        PlayerService.prototype.updatePlayer = function (player) {
            return this.$http.put(this.base.url + "/players", player);
        };
        PlayerService.prototype.deletePlayer = function (id) {
            return this.$http.delete(this.base.url + '/players/' + id);
        };
        PlayerService.prototype.getSeriesSubscriptionsOfPlayer = function (playerId, tournamentId) {
            return this.$http.get(this.base.url + '/seriesofplayer/' + playerId + '/' + tournamentId);
        };
        return PlayerService;
    }());
    TournamentManagement.PlayerService = PlayerService;
    angular.module("managerServices").factory("PlayerService", ['$http', 'base', function ($http, base) {
            return new PlayerService($http, base);
        }]);
})(TournamentManagement || (TournamentManagement = {}));
