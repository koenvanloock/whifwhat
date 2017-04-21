module HallOverview{
    import IHttpService = angular.IHttpService;
    import Hall = TournamentManagement.Hall;
    export class HallService{
        constructor(private $http: IHttpService, private base: any){}

        getHalls() {
            return this.$http.get(this.base.url + '/hall')
        }

        createHall(hallName, numberOfTableRows, numberOfTables){
            return this.$http.post(this.base.url + '/hall', {hallName: hallName, rows: numberOfTableRows, tablesPerRow: numberOfTables})
        }

        getHallById(hallId){
            return this.$http.get(this.base.url + "/hall/"+hallId);
        }

        update(hall: Hall){
            return this.$http.patch(this.base.url + '/hall', hall);
        }


        activateHall(id: string){
            return this.$http.post(this.base.url + '/activehall/' + id, {});
        }

        getActiveHall(){
            return this.$http.get(this.base.url + '/activehall');
        }
    }

    angular.module("hallViewer").factory("hallService", ['$http', 'base', ($http, base) => {
        return new HallService($http, base);
    }]);
}