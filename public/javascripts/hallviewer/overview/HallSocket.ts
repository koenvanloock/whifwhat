module HallOverview{

    import Hall = TournamentManagement.Hall;
    import AlertService = TournamentManagement.AlertService;
    export class HallSocket{

        private webSocket: WebSocket;

     constructor(hallReference: Hall, base: any, alertService: AlertService){
            console.log(base.ws);
         let hall = hallReference;


         this.webSocket = new WebSocket(base.ws + '/activehall');

     }

    }
}