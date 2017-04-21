module HallOverview{

    import Hall = TournamentManagement.Hall;
    import IAlert = TournamentManagement.IAlert;
    import AlertService = TournamentManagement.AlertService;
    import ILocationService = angular.ILocationService;
    import IRouteParamsService = angular.route.IRouteParamsService;

    class HallLayoutController{
        private hall: Hall;
        private rowList: Array<number> = [1,2,3,4,5,6,7,8,9,10];

        constructor(private hallService: HallService, private alertService: AlertService, private $routeParams: IRouteParamsService, private $location: ILocationService){
            let hallId = $routeParams["hallId"];


            hallService.getHallById(hallId).then(
                (response: any) => {this.hall = response.data; console.log(this.hall)},
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})

            )


        }



        saveHall(){
            this.hallService.update(this.hall).then(
                (success) => { this.$location.path("/hallSetup")},
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            )
        }

    }

    angular.module("hallViewer").controller("HallLayoutController", ["hallService", "alertService", "$routeParams", "$location", (hallService, alertService, $routeParams, $location) => new HallLayoutController(hallService, alertService, $routeParams, $location)]);

}