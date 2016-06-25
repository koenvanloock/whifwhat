module TournamentManagement{
    class PlayerController{

        static $inject = ["PlayerService"];

        private ranks= [];
        private inserting: boolean;
        private editing: boolean;
        private playerToEdit: Player;
        private allPlayers: Array<Player>;
        private editIndex = null;
        private emptyPlayer = {
            'playerId': null,
            'firstname': null,
            'lastname': null,
            'rank': null
        };



        constructor(private playerService: PlayerService){
            this.allPlayers = [];
            var allPlayers = this.allPlayers;
            playerService.getRanks().then(
                (result: any) => {
                    result.data.map( (x) => {
                        this.ranks.push({'name': x.name, 'value': x.value})
                    });
                }
            );

            playerService.getAllPlayers().then(
                function (result: any) {
                    result.data.map((player) => {
                        allPlayers.push({
                            'playerId': player.playerId,
                            'firstname': player.firstname,
                            'lastname': player.lastname,
                            'rank': player.rank
                        });

                    });
                });

        }


        addPlayer() {
         this.playerService.postPlayer(this.playerToEdit).then( (insertedPlayerResponse: any) => {
                this.allPlayers.push(insertedPlayerResponse.data);
                this.playerToEdit = this.emptyPlayer;
            });
        };

        startEditPlayer(playerindex){
        if(this.editIndex != playerindex) {
            this.playerToEdit = {
                "playerId": this.allPlayers[playerindex].playerId,
                "firstname": this.allPlayers[playerindex].firstname,
                "lastname": this.allPlayers[playerindex].lastname,
                "rank": {
                    "value": this.allPlayers[playerindex].rank.value,
                    "name": this.allPlayers[playerindex].rank.name

                }
            };
            this.editIndex = playerindex;
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
        this.playerService.updatePlayer(this.playerToEdit).then(() =>{
            this.allPlayers[this.editIndex] = this.playerToEdit;

            this.editing = false;
            this.inserting = true;
            this.playerToEdit = this.emptyPlayer;
            this.editIndex = -1;
        })
    };

        deletePlayer(playerIndex){
        var playerToDelete = this.allPlayers[playerIndex];
        this.playerService.deletePlayer(playerToDelete.playerId).then(()=>{
            this.allPlayers.splice(playerIndex, 1);
        })
    };
    }

    angular.module("managerControllers").controller("PlayerController", PlayerController)
}