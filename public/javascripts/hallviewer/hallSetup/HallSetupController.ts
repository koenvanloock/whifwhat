module HallOverview{

    import Hall = TournamentManagement.Hall;
    import IAlert = TournamentManagement.IAlert;
    import AlertService = TournamentManagement.AlertService;
    import ILocationService = angular.ILocationService;

    class HallOverviewController{
        private halls: Array<Hall>;
        private hallName: string;
        private numberOfTableRows: number;
        private numberOfTables: number;


        constructor(private hallService: HallService, private alertService: AlertService, private $location: ILocationService){

            hallService.getHalls().then(
                (success: any) => {
                    this.halls = success.data;
                },
                (error) => {this.alertService.addAlert({type: "error", msg: error.data, timeout: 3000})}
                )
        }


        createHall(){
            this.hallService.createHall(this.hallName, this.numberOfTableRows, this.numberOfTables).then(
                (result: any) => {this.addHall(result.data); this.clearInputs()},
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            )
        }


        addHall(createdHall){
            this.halls.push(createdHall);
        }

        clearInputs(){
            this.hallName="";
            this.numberOfTableRows=0;
            this.numberOfTables = 0;
        }

        chooseHallForLayout(id: string){
            this.$location.path("/hallLayout/"+id);
        }

        showHall(id: string){
            this.hallService.activateHall(id).then(
                (okResponse) =>  this.$location.path("/hallOverview/"),
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            )


        }
    }

    angular.module("hallViewer").controller("HallSetupController", ["hallService", "alertService", "$location", (hallService, alertService,$location) => new HallOverviewController(hallService, alertService, $location)]);

}