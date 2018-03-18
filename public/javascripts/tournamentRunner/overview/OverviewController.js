var TournamentRunner;
(function (TournamentRunner) {
    var OverviewController = (function () {
        function OverviewController(loadingService, $rootScope, $location, seriesRoundService, alertService, $mdDialog) {
            var _this = this;
            this.loadingService = loadingService;
            this.$rootScope = $rootScope;
            this.$location = $location;
            this.seriesRoundService = seriesRoundService;
            this.alertService = alertService;
            this.$mdDialog = $mdDialog;
            loadingService.isThereActiveTournament().then(function (result) {
                if (result.data.hasTournament) {
                    loadingService.getActiveTournament().then(function (response) {
                        _this.tournament = response.data;
                        loadingService.getSeriesOfTournament(_this.tournament.id).then(function (response) {
                            _this.seriesList = response.data;
                            if (_this.seriesList.length > 0) {
                                _this.selectedSeries = _this.seriesList[0];
                            }
                        }, function (errorResponse) { return alertService.addAlert({
                            type: "error",
                            msg: errorResponse.data,
                            timeout: 3000
                        }); });
                    }, function (errorResponse) { return alertService.addAlert({
                        type: "error",
                        msg: errorResponse.data,
                        timeout: 3000
                    }); });
                }
                else {
                    $location.path('/playTournament');
                }
            });
            this.$rootScope.$watch(function () { return _this.selectedSeries; }, function (newVal, oldVal) {
                if (newVal != oldVal) {
                    _this.finalRanking = [];
                    _this.loadRoundsOfSeries(newVal.id);
                }
            });
        }
        ;
        OverviewController.prototype.loadRoundsOfSeries = function (seriesId) {
            var _this = this;
            this.loadingService.getCurrentRoundOfSeries(seriesId).then(function (response) {
                if (_this.selectedSeries.id === response.data.seriesId) {
                    _this.selectedRound = response.data;
                }
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        ;
        OverviewController.prototype.activeBracketRound = function () {
            return this.selectedRound && this.selectedRound.roundType == "B";
        };
        ;
        OverviewController.prototype.activeRobinRound = function () {
            return this.selectedRound && this.selectedRound.roundType == "R";
        };
        ;
        OverviewController.prototype.showRoundOption = function (round) {
            var nr = round.roundNr + ". ";
            return (round.roundType === "B") ? nr + "Tabel" : nr + "Poules";
        };
        ;
        OverviewController.prototype.updateRound = function (roundId, match) {
            var _this = this;
            this.loadingService.updateSeriesRound(roundId, match).then(function (response) {
                if (response.data.id === _this.selectedRound.id) {
                    if (response.data.robinRounds) {
                        response.data.robinRounds.map(function (responseRobinGroup) {
                            var roundToUpdate = _this.search(_this.selectedRound.robinRounds, responseRobinGroup.robinGroupId, 'robinGroupId');
                            roundToUpdate.robinPlayers = responseRobinGroup.robinPlayers;
                            responseRobinGroup.robinMatches.map(function (responseMatch) {
                                var matchToUpdate = _this.search(roundToUpdate.robinMatches, responseMatch.id, 'id');
                                matchToUpdate.wonSetsA = responseMatch.wonSetsA;
                                matchToUpdate.wonSetsB = responseMatch.wonSetsB;
                            });
                        });
                    }
                    else {
                        _this.selectedRound = response.data;
                    }
                }
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        ;
        OverviewController.prototype.roundSpecificPadding = function () {
            if (this.activeBracketRound()) {
                switch (this.selectedRound.numberOfBracketRounds) {
                    case 5:
                        return { marginLeft: "250px" };
                    case 4:
                        return { marginLeft: "250px" };
                    case 3:
                        return { marginLeft: "250px" };
                    case 0:
                    case 1:
                    case 2:
                    default:
                        return {};
                }
            }
        };
        OverviewController.prototype.hasPreviousRound = function () {
            return this.selectedRound && this.selectedRound.roundNr > 1;
        };
        OverviewController.prototype.showRoundResult = function () {
            var _this = this;
            this.seriesRoundService.showRoundResult(this.selectedRound.id).then(function (response) {
                var roundResult = response.data;
                _this.$mdDialog
                    .show({
                    locals: { roundResult: roundResult },
                    clickOutsideToClose: true,
                    controllerAs: 'roundResultController',
                    templateUrl: 'assets/dialogs/roundResultDialog.html',
                    controller: 'RoundResultController',
                }).then(function () { _this.gotoNextRound(); }, function () { });
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        OverviewController.prototype.gotoNextRound = function () {
            var _this = this;
            this.seriesRoundService.proceedToNextRound(this.selectedSeries.id, this.selectedRound.roundNr + 1).then(function (response) {
                _this.isLastRound();
                if (response.data.id != null) {
                    _this.finalRanking = [];
                    _this.selectedRound = response.data;
                    _this.selectedSeries.currentRoundNr = response.data.roundNr;
                }
                else {
                    _this.finalRanking = response.data;
                }
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        OverviewController.prototype.gotoPreviousRound = function () {
            var _this = this;
            var roundNrToGet = this.lastRound ? this.selectedRound.roundNr : this.selectedRound.roundNr - 1;
            this.seriesRoundService.previousRound(this.selectedSeries.id, roundNrToGet).then(function (response) {
                _this.finalRanking = [];
                _this.lastRound = false;
                _this.selectedRound = response.data;
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        OverviewController.prototype.showFinalRanking = function () {
            return this.finalRanking && this.finalRanking.length > 0;
        };
        OverviewController.prototype.isLastRound = function () {
            var _this = this;
            this.loadingService.isLastRound(this.selectedSeries.id).then(function (response) {
                console.log(response.data.lastRound);
                _this.lastRound = response.data.lastRound;
            }, function (errorResponse) { return _this.alertService.addAlert({
                type: "error",
                msg: errorResponse.data,
                timeout: 3000
            }); });
        };
        OverviewController.prototype.search = function (array, key, prop) {
            prop = (typeof prop === 'undefined') ? 'id' : prop;
            for (var i = 0; i < array.length; i++) {
                if (array[i][prop] === key) {
                    return array[i];
                }
            }
        };
        OverviewController.prototype.releaseActiveTournament = function (ev) {
            var _this = this;
            var confirm = this.$mdDialog.confirm()
                .title('Wil je het actieve tornooi stoppen?')
                .textContent('Deze actie sluit het huidige tornooi')
                .ariaLabel('stopTornooi')
                .targetEvent(ev)
                .ok('OK')
                .cancel('annuleren');
            this.$mdDialog.show(confirm).then(function () {
                _this.loadingService.releaseActiveTournament().then(function () { return _this.$location.path("/playTournament"); }, function (errorResponse) { return _this.alertService.addAlert({
                    type: "error",
                    msg: "Kon actief tornooi niet stoppen.",
                    timeout: 3000
                }); });
            });
        };
        return OverviewController;
    }());
    angular.module("tournamentRunner").controller("OverviewController", ['loadingService', '$rootScope', '$location', 'SeriesRoundService', 'alertService', '$mdDialog', function (loadingService, rootScope, $location, seriesRoundService, alertService, $mdDialog) { return new OverviewController(loadingService, rootScope, $location, seriesRoundService, alertService, $mdDialog); }]);
})(TournamentRunner || (TournamentRunner = {}));
