module HallOverview {


    import Hall = TournamentManagement.Hall;
    import ILocationService = angular.ILocationService;
    import AlertService = TournamentManagement.AlertService;
    import AuthService = TournamentManagement.AuthService;
    import IScope = angular.IScope;
    import IRootScopeService = angular.IRootScopeService;
    class HallOverviewController {
        private hall: Hall;
        private hallSocket: WebSocket;
        private rowList: Array<number> = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];


        constructor(private base: any, private $location: ILocationService, private alertService: AlertService, private $scope: any) {
            /*
             var source = new EventSource(base.url + "/stream");

             source.addEventListener('message', updateEvent, false);

             source.addEventListener('error', function (e) {
             if (e.eventPhase == EventSource.CLOSED) {
             // Connection was closed.
             console.log("Connection was closed on error: " + e);
             } else {
             console.log("Error occurred while streaming: " + e);
             }
             }, false);
             }*/

        }

        /*let updateEvent = function (e) {
         let json = JSON.parse(e.data);
         this.hall = json;
         console.log(this.hall.hallName);
         document.getElementById("hallName").innerText = this.hall.hallName;

         }
         */
    }

    angular.module("hallViewer").controller("HallOverviewController", ["base", "$location", "alertService", "$rootScope", (base, $location, alertService, $scope) => new HallOverviewController(base, $location, alertService, $scope)])
}