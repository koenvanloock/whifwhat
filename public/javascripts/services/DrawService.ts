module DrawService{
    import IHttpService = angular.IHttpService;
    export class PlayerService{
        constructor(private $http: IHttpService, private base: any){}

        drawInitial(tournamentId) {
            return this.$http.get(this.base.url + '/drawseries/'+tournamentId);
        }
    }

    angular.module("managerServices").factory("DrawService", ['$http', 'base', ($http, base) => {
        return new PlayerService($http, base);
    }]);

}