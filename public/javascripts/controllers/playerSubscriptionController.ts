module TournamentManagement {
    import IRouteParamsService = angular.route.IRouteParamsService;
    class PlayerSubscriptionController {
        static $inject = ["TournamentService", "SeriesService", "PlayerService", "$location", "$routeParams", "alertService"];

        private tournament:Tournament;
        private allPlayers = [];
        private ranks:Array<Rank>;
        private inserting:boolean = true;
        private editing:boolean = false;

        private seriesList:Array<any>;
        private playerSubscriptions:Array<any>;
        private playerSelection:any;

        private checkList: Array<boolean>;
        private subscriptionList:string;
        private query:any;

        constructor(private tournamentService:TournamentService,
                    private seriesService:SeriesService,
                    private playerService:PlayerService,
                    private $location:angular.ILocationService,
                    private $routeParams:IRouteParamsService,
                    private alertService: AlertService) {
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

            this.playerService.getRanks().then(

                (result:any) => {
                    result.data.map((x) => {
                        this.ranks.push({'name': x.name, 'value': x.value})
                    });
                },
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})

            );

            playerService.getAllPlayers().then(
                (result:any) => {this.allPlayers = result.data},
                (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
            );

            if (tournamentService.getCurrentTournament() && tournamentService.getCurrentTournament().id == $routeParams["tournamentId"]) {
                this.tournament = tournamentService.getCurrentTournament();
                this.init();
            } else {
                tournamentService.getTournament($routeParams["tournamentId"]).then((response:any)=> {
                    this.tournament = response.data;
                    this.init();
                },
                    (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})

                )
            }
        }

        init() {
            this.tournament.series.map((series) => {
                series.query = {page: 1, limit: 5};
                this.seriesList.push(series)
            });
            this.tournament.series.map((series:Series) => {
                this.seriesService.fetchSeriesPlayers(series.id).then(
                    (seriesPlayers:any) => {
                        series.seriesPlayers = [];
                        seriesPlayers.data.map((seriesPlayer) => {
                            series.seriesPlayers.push(seriesPlayer.player);
                        });
                    },
                    (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
                )
            });

        }

        getPlayerSubscriptions() {
            if (this.playerSelection.selectedItem) {
                console.log(this.playerSelection.selectedItem);
                this.playerService.getSeriesSubscriptionsOfPlayer(this.playerSelection.selectedItem.id, this.tournament.id).then(

                    (response:any) => {
                    this.playerSubscriptions = response.data;
                    this.checkList = [];
                    this.seriesList.map( (series) => this.checkList.push(this.isSubscribed(this.playerSubscriptions, series)));
                },
                    (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})

                );
            }
        }

        isSubscribed(subscriptionList, series){
            return subscriptionList.filter( (subscription) => subscription.id === series.id).length > 0;
        }

        calculateTournamentPlayers() {
            if (this.tournament && this.tournament.series) {
                return this.tournament.series
                    .map((series) => (series.seriesPlayers && series.seriesPlayers.length) ? series.seriesPlayers.length : 0)
                    .reduce((acc, newVal) => acc + newVal, 0);
            } else {
                return 0;
            }
        }

        querySearch(query) {
            return query ? this.allPlayers.filter(this.createFilterFor(query)) : this.allPlayers;
        }

        createFilterFor(query) {
            let lowercaseQueryList = angular.lowercase(query).split(' ');
            return function filterFn(person) {
                let contains = false;
                lowercaseQueryList.map(function (queryPart) {
                    contains = contains || (angular.lowercase(person.firstname).indexOf(queryPart) === 0) || (angular.lowercase(person.lastname).indexOf(queryPart) === 0);
                });
                return contains;
            };
        }

        enterPlayer() {
            this.playerSubscriptions  = [];
            this.checkList.map( (subscribeTo,index) => { if(subscribeTo){
                this.playerSubscriptions.push(this.seriesList[index])
            }
            });
            this.createSubscription();
        };

        createSubscription() {
            let subscriptionList = this.playerSubscriptions.map( (series) => series.id);

            this.playerService.subscribePlayer(subscriptionList, this.tournament.series.map((series) => series.id), this.playerSelection.selectedItem).then((resp) => {
                this.tournament.series.map((series) => {
                    this.seriesService.fetchSeriesPlayers(series.id).then(
                        (seriesPlayers:any) => {
                            series.seriesPlayers = seriesPlayers.data.map((result) => result.player);
                        },
                        (errorResponse) => this.alertService.addAlert({type: "error", msg: errorResponse.data, timeout: 3000})
                    )
                });
                this.playerSelection.searchText = null;
                this.playerSelection.selectedItem = null;
            })
        };

        lessThanMaxEntries(){
            return this.checkList.filter((isSubscribed) => isSubscribed).length < this.tournament.maximumNumberOfSeriesEntries;
        }

        gotoSeriesSetup() {
            this.$location.path("tournament/" + this.$routeParams["tournamentId"] + "/seriesSetup")
        };

        gotoRoundsSetup() {
            this.$location.path("/tournament/" + this.$routeParams['tournamentId'] + "/roundsSetup");
        };
    }

    angular.module("managerControllers").controller("PlayerSubscriptionController", PlayerSubscriptionController)
}