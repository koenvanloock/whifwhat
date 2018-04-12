var TournamentManagement;
(function (TournamentManagement) {
    var PlayerSubscriptionController = (function () {
        function PlayerSubscriptionController(tournamentService, seriesService, playerService, $location, $routeParams, alertService) {
            var _this = this;
            this.tournamentService = tournamentService;
            this.seriesService = seriesService;
            this.playerService = playerService;
            this.$location = $location;
            this.$routeParams = $routeParams;
            this.alertService = alertService;
            this.allPlayers = [];
            this.inserting = true;
            this.editing = false;
            this.ranks = [];
            this.seriesList = [];
            this.checkList = [];
            this.playerSubscriptions = [];
            this.query = {
                page: 1,
                limit: 5
            };
            this.playerSelection = {
                "isDisabled": false,
                "selectedItem": null
            };
            this.subscriptionList = "";
            this.playerService.getRanks().then(function (result) {
                result.data.map(function (x) {
                    _this.ranks.push({ 'name': x.name, 'value': x.value });
                });
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            playerService.getAllPlayers().then(function (result) { _this.allPlayers = result.data; }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            if (tournamentService.getCurrentTournament() && tournamentService.getCurrentTournament().id == $routeParams["tournamentId"]) {
                this.tournament = tournamentService.getCurrentTournament();
                this.init();
            }
            else {
                tournamentService.getTournament($routeParams["tournamentId"]).then(function (response) {
                    _this.tournament = response.data;
                    _this.init();
                }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            }
        }
        PlayerSubscriptionController.prototype.init = function () {
            var _this = this;
            this.tournament.series.map(function (series) {
                series.query = { page: 1, limit: 5 };
                _this.seriesList.push(series);
            });
            this.tournament.series.map(function (series) {
                _this.seriesService.fetchSeriesPlayers(series.id).then(function (seriesPlayers) {
                    series.seriesPlayers = [];
                    seriesPlayers.data.map(function (seriesPlayer) {
                        series.seriesPlayers.push(seriesPlayer.player);
                    });
                }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            });
        };
        PlayerSubscriptionController.prototype.getPlayerSubscriptions = function () {
            var _this = this;
            if (this.playerSelection.selectedItem) {
                console.log(this.playerSelection.selectedItem);
                this.playerService.getSeriesSubscriptionsOfPlayer(this.playerSelection.selectedItem.id, this.tournament.id).then(function (response) {
                    _this.playerSubscriptions = response.data;
                    _this.checkList = [];
                    _this.seriesList.map(function (series) { return _this.checkList.push(_this.isSubscribed(_this.playerSubscriptions, series)); });
                }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
            }
        };
        PlayerSubscriptionController.prototype.isSubscribed = function (subscriptionList, series) {
            return subscriptionList.filter(function (subscription) { return subscription.id === series.id; }).length > 0;
        };
        PlayerSubscriptionController.prototype.calculateTournamentPlayers = function () {
            if (this.tournament && this.tournament.series) {
                return this.tournament.series
                    .map(function (series) { return (series.seriesPlayers && series.seriesPlayers.length) ? series.seriesPlayers.length : 0; })
                    .reduce(function (acc, newVal) { return acc + newVal; }, 0);
            }
            else {
                return 0;
            }
        };
        PlayerSubscriptionController.prototype.querySearch = function (query) {
            return query ? this.allPlayers.filter(this.createFilterFor(query)) : this.allPlayers;
        };
        PlayerSubscriptionController.prototype.createFilterFor = function (query) {
            var lowercaseQueryList = angular.lowercase(query).split(' ');
            return function filterFn(person) {
                var contains = false;
                lowercaseQueryList.map(function (queryPart) {
                    contains = contains || (angular.lowercase(person.firstname).indexOf(queryPart) === 0) || (angular.lowercase(person.lastname).indexOf(queryPart) === 0);
                });
                return contains;
            };
        };
        PlayerSubscriptionController.prototype.enterPlayer = function () {
            var _this = this;
            this.playerSubscriptions = [];
            this.checkList.map(function (subscribeTo, index) {
                if (subscribeTo) {
                    _this.playerSubscriptions.push(_this.seriesList[index]);
                }
            });
            this.createSubscription();
        };
        ;
        PlayerSubscriptionController.prototype.createSubscription = function () {
            var _this = this;
            var subscriptionList = this.playerSubscriptions.map(function (series) { return series.id; });
            this.playerService.subscribePlayer(subscriptionList, this.tournament.series.map(function (series) { return series.id; }), this.playerSelection.selectedItem).then(function (resp) {
                _this.tournament.series.map(function (series) {
                    _this.seriesService.fetchSeriesPlayers(series.id).then(function (seriesPlayers) {
                        series.seriesPlayers = seriesPlayers.data.map(function (result) { return result.player; });
                    }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
                });
                _this.playerSelection.searchText = null;
                _this.playerSelection.selectedItem = null;
            });
        };
        ;
        PlayerSubscriptionController.prototype.lessThanMaxEntries = function () {
            return this.checkList.filter(function (isSubscribed) { return isSubscribed; }).length < this.tournament.maximumNumberOfSeriesEntries;
        };
        PlayerSubscriptionController.prototype.gotoSeriesSetup = function () {
            this.$location.path("tournament/" + this.$routeParams["tournamentId"] + "/seriesSetup");
        };
        ;
        PlayerSubscriptionController.prototype.gotoRoundsSetup = function () {
            this.$location.path("/tournament/" + this.$routeParams['tournamentId'] + "/roundsSetup");
        };
        ;
        return PlayerSubscriptionController;
    }());
    PlayerSubscriptionController.$inject = ["TournamentService", "SeriesService", "PlayerService", "$location", "$routeParams", "alertService"];
    angular.module("managerControllers").controller("PlayerSubscriptionController", PlayerSubscriptionController);
})(TournamentManagement || (TournamentManagement = {}));
