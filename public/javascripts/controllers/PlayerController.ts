module TournamentManagement{
    import ITimeoutService = angular.ITimeoutService;
    import IRootScopeService = angular.IRootScopeService;
    class PlayerController{

        static $inject = ["PlayerService", "Upload","$timeout", "base", "alertService","$rootScope"];

        private ranks= [];
        private inserting: boolean;
        private editing: boolean;
        private playerToEdit: Player;
        private allPlayers: Array<Player>;
        private shownPlayers: Array<Player>;
        private searchString: string;

        private editIndex = null;
        private emptyPlayer = {
            'id': "",
            'firstname': "",
            'lastname': "",
            'rank': null
        };

        private query: any;

        private result: any;
        private errorMsg: string;
        private progress: number;


        constructor(private playerService: PlayerService, private Upload: any, private $timeout: ITimeoutService, private base: any, private alertService: AlertService, private $rootScope: IRootScopeService){
            this.inserting = true;
            this.allPlayers = [];
            playerService.getRanks().then(
                (result: any) => {
                    result.data.map( (x) => {
                        this.ranks.push({'name': x.name, 'value': x.value})
                    });
                },
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            );

            this.query = {
                limit: 5,
                page: 1
            };

            playerService.getAllPlayers().then(
                (result: any) => {
                    result.data.map((player) => {
                        this.allPlayers.push({
                            'id': player.id,
                            'firstname': player.firstname,
                            'lastname': player.lastname,
                            'rank': player.rank,
                            'imagepath': player.imagepath
                        });
                    });

                });

            $rootScope.$watch( () => this.searchString, (newValue, oldValue) => {if(newValue != oldValue){
                playerService.getPlayersBySearch(this.searchString).then(
                    (success) => { this.allPlayers = success.data},
                    (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
                )
            }})
        }


        getImagePath(player: Player){
            return (player.imagepath) ? player.imagepath : "../assets/images/noImageIcon.jpg";
        }

        getRankOfValue(value: number){
            return this.ranks.filter( rank => rank.value === value)[0];
        }

        addPlayer() {
            this.playerToEdit.rank = this.getRankOfValue(this.playerToEdit.rank.value);
         this.playerService.postPlayer(this.playerToEdit).then( (insertedPlayerResponse: any) => {
                this.allPlayers.push(insertedPlayerResponse.data);
                this.playerToEdit = this.emptyPlayer;
            });
        };

        startEditPlayer(playerindex){
        if(this.editIndex != playerindex) {
            var indexToEdit = this.query.limit * (this.query.page-1) + playerindex;

            this.playerToEdit = {
                "id": this.allPlayers[indexToEdit].id,
                "firstname": this.allPlayers[indexToEdit].firstname,
                "lastname": this.allPlayers[indexToEdit].lastname,
                "imagepath": this.allPlayers[indexToEdit].imagepath,
                "rank": this.ranks[this.allPlayers[indexToEdit].rank.value]
            };
            this.editIndex = indexToEdit;
            this.editing = true;
            this.inserting = false;
        } else {
            this.playerToEdit = this.emptyPlayer;
            this.editIndex = -1;
            this.editing = false;
            this.inserting = true;
        }
    };

        editPlayer(){
        this.playerToEdit.rank = this.ranks[this.playerToEdit.rank.value];
        this.playerService.updatePlayer(this.playerToEdit).then(
            () =>{
                    this.allPlayers[this.editIndex] = this.playerToEdit;

                    this.editing = false;
                    this.inserting = true;
                    this.playerToEdit = this.emptyPlayer;
                    this.editIndex = -1;
                },
            (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})

        )
    };

        deletePlayer(playerIndex){
        var playerToDelete = this.allPlayers[playerIndex];
        this.playerService.deletePlayer(playerToDelete.id).then(
            ()=>{this.allPlayers.splice(playerIndex, 1);},
            (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000}))};


    }

    angular.module("managerControllers").controller("PlayerController", PlayerController)
}