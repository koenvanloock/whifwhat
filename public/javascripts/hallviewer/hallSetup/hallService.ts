module HallOverview{
    import IHttpService = angular.IHttpService;
    export class HallService{
        constructor(private $http: IHttpService, private base: any){}

        getHalls() {
            return this.$http.get(this.base.url + '/hall')
        }
    }

    angular.module("hallViewer").factory("hallService", ['$http', 'base', ($http, base) => {
        return new HallService($http, base);
    }]);
}