module HallOverview{

    import Hall = TournamentManagement.Hall;
    import IAlert = TournamentManagement.IAlert;

    class HallOverviewController{
        private halls: Array<Hall>;
        private hallName: string;
        private numberOfTableRows: number;
        private numberOfTables: number;


        constructor(private hallService: HallService){

            hallService.getHalls().then(
                (success) => {this.halls = success.data},
                (error) => {
                var alert: IAlert = {
                    type: "danger",
                    msg:  error,
                    timeout: 3000
                }
            });
        }
    }

    angular.module("hallViewer").controller("HallSetupController", ["hallService", (hallService) => new HallOverviewController(hallService)]);

}