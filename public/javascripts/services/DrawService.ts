module TournamentManagement{
    import IHttpService = angular.IHttpService;
    export class DrawService{
        constructor(private $http: IHttpService, private base: any){}

        drawInitial(tournamentId) {
            return this.$http.get(this.base.url + '/drawseries/'+tournamentId);
        }

        redraw(seriesRound){
            return this.$http.post(this.base.url +"/redraw", seriesRound);
        }
    }

    angular.module("managerServices").factory("DrawService", ['$http', 'base', ($http, base) => {
        return new DrawService($http, base);
    }]);

}