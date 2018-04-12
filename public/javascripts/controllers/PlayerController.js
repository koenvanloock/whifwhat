var TournamentManagement;
(function (TournamentManagement) {
    var PlayerController = (function () {
        function PlayerController(playerService, Upload, $timeout, base, alertService, $rootScope) {
            var _this = this;
            this.playerService = playerService;
            this.Upload = Upload;
            this.$timeout = $timeout;
            this.base = base;
            this.alertService = alertService;
            this.$rootScope = $rootScope;
            this.ranks = [];
            this.editIndex = null;
            this.emptyPlayer = {
                'id': "",
                'firstname': "",
                'lastname': "",
                'rank': null
            };
            this.inserting = true;
            this.allPlayers = [];
            playerService.getRanks().then(function (result) {
                result.data.map(function (x) {
                    _this.ranks.push({ 'name': x.name, 'value': x.value });
                });
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            this.query = {
                limit: 5,
                page: 1
            };
            playerService.getAllPlayers().then(function (result) {
                result.data.map(function (player) {
                    _this.allPlayers.push({
                        'id': player.id,
                        'firstname': player.firstname,
                        'lastname': player.lastname,
                        'rank': player.rank,
                        'imagepath': player.imagepath
                    });
                });
            });
            $rootScope.$watch(function () { return _this.searchString; }, function (newValue, oldValue) {
                if (newValue != oldValue) {
                    playerService.getPlayersBySearch(_this.searchString).then(function (success) { _this.allPlayers = success.data; }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
                }
            });
        }
        PlayerController.prototype.getImagePath = function (player) {
            return (player.imagepath) ? player.imagepath : "../assets/images/noImageIcon.jpg";
        };
        PlayerController.prototype.getRankOfValue = function (value) {
            return this.ranks.filter(function (rank) { return rank.value === value; })[0];
        };
        PlayerController.prototype.addPlayer = function () {
            var _this = this;
            this.playerToEdit.rank = this.getRankOfValue(this.playerToEdit.rank.value);
            this.playerService.postPlayer(this.playerToEdit).then(function (insertedPlayerResponse) {
                _this.allPlayers.push(insertedPlayerResponse.data);
                _this.playerToEdit = _this.emptyPlayer;
            });
        };
        ;
        PlayerController.prototype.startEditPlayer = function (playerindex) {
            if (this.editIndex != playerindex) {
                var indexToEdit = this.query.limit * (this.query.page - 1) + playerindex;
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
            }
            else {
                this.playerToEdit = this.emptyPlayer;
                this.editIndex = -1;
                this.editing = false;
                this.inserting = true;
            }
        };
        ;
        PlayerController.prototype.editPlayer = function () {
            var _this = this;
            this.playerToEdit.rank = this.ranks[this.playerToEdit.rank.value];
            this.playerService.updatePlayer(this.playerToEdit).then(function () {
                _this.allPlayers[_this.editIndex] = _this.playerToEdit;
                _this.editing = false;
                _this.inserting = true;
                _this.playerToEdit = _this.emptyPlayer;
                _this.editIndex = -1;
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        ;
        PlayerController.prototype.deletePlayer = function (playerIndex) {
            var _this = this;
            var playerToDelete = this.allPlayers[playerIndex];
            this.playerService.deletePlayer(playerToDelete.id).then(function () { _this.allPlayers.splice(playerIndex, 1); }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        ;
        return PlayerController;
    }());
    PlayerController.$inject = ["PlayerService", "Upload", "$timeout", "base", "alertService", "$rootScope"];
    angular.module("managerControllers").controller("PlayerController", PlayerController);
})(TournamentManagement || (TournamentManagement = {}));
