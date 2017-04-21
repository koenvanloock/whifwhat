module TournamentRunner {

    import Tournament = TournamentManagement.Tournament;
    import Series = TournamentManagement.Series;
    import SeriesRound = TournamentManagement.SeriesRound;
    import IRootScopeService = angular.IRootScopeService;
    import ILocationService = angular.ILocationService;
    import SeriesRoundService = TournamentManagement.SeriesRoundService;
    import Player = TournamentManagement.Player;
    import AlertService = TournamentManagement.AlertService;
    class OverviewController {

        private tournament:Tournament;
        private error:string;
        private seriesList:Array<Series>;
        private selectedSeries:Series;
        private selectedRound:SeriesRound;
        private finalRanking:Array<Player>;
        private lastRound:boolean;

        constructor(private loadingService:LoadingService, private $rootScope:IRootScopeService, private $location:ILocationService, private seriesRoundService:SeriesRoundService, private alertService:AlertService, private $mdDialog:ng.material.IDialogService) {
            loadingService.isThereActiveTournament().then((result) => {
                if (result.data.hasTournament) {
                    loadingService.getActiveTournament().then(
                        (response) => {
                            this.tournament = response.data;
                            loadingService.getSeriesOfTournament(this.tournament.id).then(
                                (response) => {
                                    this.seriesList = response.data;
                                    if (this.seriesList.length > 0) {
                                        this.selectedSeries = this.seriesList[0]
                                    }
                                },
                                (errorResponse) => alertService.addAlert({
                                    type: "error",
                                    msg: errorResponse.data,
                                    timeout: 3000
                                })
                            );
                        },
                        (errorResponse) => alertService.addAlert({
                            type: "error",
                            msg: errorResponse.data,
                            timeout: 3000
                        })
                    )
                } else {
                    $location.path('/playTournament')
                }
            });

            this.$rootScope.$watch(() =>this.selectedSeries, (newVal, oldVal) => {
                if (newVal != oldVal) {
                    this.loadRoundsOfSeries(newVal.id)
                }
            })
        };


        loadRoundsOfSeries(seriesId) {
            this.loadingService.getCurrentRoundOfSeries(seriesId).then(
                (response) => {
                    if (this.selectedSeries.id === response.data.seriesId) {
                        this.selectedRound = response.data;
                    }
                },
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            );
        };


        activeBracketRound() {
            return this.selectedRound && this.selectedRound.roundType == "B";
        };

        activeRobinRound() {
            return this.selectedRound && this.selectedRound.roundType == "R";
        };

        showRoundOption(round) {
            var nr = round.roundNr + ". ";
            return (round.roundType === "B") ? nr + "Tabel" : nr + "Poules";
        };


        updateRound(roundId, match) {
            this.loadingService.updateSeriesRound(roundId, match).then(
                (response) => {
                    if (response.data.id === this.selectedRound.id) {
                        if (response.data.robinRounds) {
                            response.data.robinRounds.map((responseRobinGroup) => {
                                var roundToUpdate = this.search(this.selectedRound.robinRounds, responseRobinGroup.robinGroupId, 'robinGroupId');
                                roundToUpdate.robinPlayers = responseRobinGroup.robinPlayers;
                                responseRobinGroup.robinMatches.map((responseMatch) => {
                                    var matchToUpdate = this.search(roundToUpdate.robinMatches, responseMatch.id, 'id');
                                    matchToUpdate.wonSetsA = responseMatch.wonSetsA;
                                    matchToUpdate.wonSetsB = responseMatch.wonSetsB;
                                });
                            });

                        } else {
                            this.selectedRound = response.data;
                        }
                    }
                },

                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            );
        };

        roundSpecificPadding():Object {
            if (this.activeBracketRound()) {
                switch (this.selectedRound.numberOfBracketRounds) {
                    case 5:
                        return {marginLeft: "250px"};
                    case 4:
                        return {marginLeft: "250px"};
                    case 3:
                        return {marginLeft: "250px"};
                    case 0:
                    case 1:
                    case 2:
                    default:
                        return {};
                }
            }

        }

        hasPreviousRound() {
            return this.selectedRound && this.selectedRound.roundNr > 1;
        }

        gotoNextRound() {
            this.seriesRoundService.proceedToNextRound(this.selectedSeries.id, this.selectedRound.roundNr + 1).then(
                (response) => {
                    this.isLastRound();
                    if (response.data.id != null) {
                        this.finalRanking = [];
                        this.selectedRound = response.data;
                        this.selectedSeries.currentRoundNr = response.data.roundNr;
                    } else {
                        this.finalRanking = response.data;
                    }
                },
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            )
        }

        gotoPreviousRound() {

            var roundNrToGet = this.lastRound ? this.selectedRound.roundNr : this.selectedRound.roundNr - 1;

            this.seriesRoundService.previousRound(this.selectedSeries.id, roundNrToGet).then(
                (response) => {
                    this.finalRanking = [];
                    this.lastRound = false;
                    this.selectedRound = response.data;
                },
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            )
        }

        showFinalRanking() {
            return this.finalRanking && this.finalRanking.length > 0;
        }

        isLastRound() {
            this.loadingService.isLastRound(this.selectedSeries.id).then(
                response => {
                    console.log(response.data.lastRound);
                    this.lastRound = response.data.lastRound;
                },
                (errorResponse) => this.alertService.addAlert({
                    type: "error",
                    msg: errorResponse.data,
                    timeout: 3000
                })
            );
        }

        search(array, key, prop) {
            // Optional, but fallback to key['name'] if not selected
            prop = (typeof prop === 'undefined') ? 'id' : prop;

            for (var i = 0; i < array.length; i++) {
                if (array[i][prop] === key) {
                    return array[i];
                }
            }
        }

        releaseActiveTournament(ev) {
            var confirm = this.$mdDialog.confirm()
                .title('Wil je het actieve tornooi stoppen?')
                .textContent('Deze actie sluit het huidige tornooi')
                .ariaLabel('stopTornooi')
                .targetEvent(ev)
                .ok('OK')
                .cancel('annuleren');

            this.$mdDialog.show(confirm).then(
                () => {
                    this.loadingService.releaseActiveTournament().then(
                        () => this.$location.path("/playTournament"),
                        (errorResponse) => this.alertService.addAlert({
                            type: "error",
                            msg: "Kon actief tornooi niet stoppen.",
                            timeout: 3000
                        })
                    )
                })
        }
    }


    angular.module("tournamentRunner").controller("OverviewController", ['loadingService', '$rootScope', '$location', 'SeriesRoundService', 'alertService', '$mdDialog', (loadingService, rootScope, $location, seriesRoundService, alertService, $mdDialog) => new OverviewController(loadingService, rootScope, $location, seriesRoundService, alertService, $mdDialog)])
}