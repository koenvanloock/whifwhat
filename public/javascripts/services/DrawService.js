var TournamentManagement;
(function (TournamentManagement) {
    var DrawService = (function () {
        function DrawService($http, base) {
            this.$http = $http;
            this.base = base;
        }
        DrawService.prototype.drawInitial = function (tournamentId) {
            return this.$http.get(this.base.url + '/drawseries/' + tournamentId);
        };
        DrawService.prototype.redraw = function (seriesRound) {
            return this.$http.post(this.base.url + "/redraw", seriesRound);
        };
        return DrawService;
    }());
    TournamentManagement.DrawService = DrawService;
    angular.module("managerServices").factory("DrawService", ['$http', 'base', function ($http, base) {
            return new DrawService($http, base);
        }]);
})(TournamentManagement || (TournamentManagement = {}));
