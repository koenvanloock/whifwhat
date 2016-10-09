module TournamentManagement {
    import IRouteParamsService = angular.route.IRouteParamsService;
    class PlayerSubscriptionController {
        static $inject = ["TournamentService","SeriesService","PlayerService","$location", "$routeParams"];

        private tournament:Tournament;
        private numberOfSeriesEntries:Array<any>;
        private allPlayers = [];
        private ranks: Array<Rank>;
        private inserting:boolean = true;
        private editing:boolean = false;
        private seriesSubscriptions: Array<any>;
        private subscription: any;
        private playerSelection: any;
        private subscriptionList: string;
        private query: any;

        constructor(
            private tournamentService: TournamentService,
            private seriesService: SeriesService,
            private playerService: PlayerService,
            private $location: angular.ILocationService,
            private $routeParams:IRouteParamsService) {
            this.ranks = [];
            this.seriesSubscriptions = [{seriesName: "uitschrijven", seriesId: ""}];
            this.subscription = {
                "seriesSubscriptions": []
            };
            this.query = {
                page: 1,
                limit: 5
            };
            this.playerSelection = {
                "isDisabled": false,
                "selectedItem": null
            };
            this.subscriptionList = "";

            this.playerService.getRanks().then((result:any) => {
                    result.data.map((x) =>{
                        this.ranks.push({'name': x.name, 'value': x.value})
                    });
                }
            );

            playerService.getAllPlayers().then((result:any) => {
                this.allPlayers = result.data});

            if (tournamentService.getCurrentTournament() && tournamentService.getCurrentTournament().id == $routeParams["tournamentId"]) {
                this.tournament = tournamentService.getCurrentTournament();
                this.init();
            } else {
                tournamentService.getTournament($routeParams["tournamentId"]).then((response: any)=> {
                    this.tournament = response.data;
                    this.init();
                })
            }


        }

        init() {
            this.numberOfSeriesEntries = new Array(this.tournament.maximumNumberOfSeriesEntries);
            this.tournament.series.map((series) => {
                series.query = {page: 1, limit: 5};
                this.seriesSubscriptions.push(series)});
            this.tournament.series.map( (series: Series) => {
                this.seriesService.fetchSeriesPlayers(series.id).then(
                    (seriesPlayers: any) => {
                        series.seriesPlayers = [];
                        seriesPlayers.data.map( (seriesPlayer) => {
                        console.log(seriesPlayer.player);
                        series.seriesPlayers.push(seriesPlayer.player);});
                        console.log(this.tournament);
                    }

                )
            });
            this.createSubscriptions();

        }



        createSubscriptions(){
        for(var i=0; i< this.tournament.maximumNumberOfSeriesEntries; i++){
            this.subscription.seriesSubscriptions[i] = null;
        }
        };

        getSeriesSubscriptions(){
            this.subscriptionList = "";
            if(this.playerSelection.selectedItem) {
                this.playerService.getSeriesSubscriptionsOfPlayer(this.playerSelection.selectedItem.playerId, this.tournament.id).then( (response: any) => {

                    this.subscription.seriesSubscriptions = response.data.map((series,index) => {this.getSubscriptionOfSeries(this.subscription.seriesSubscriptions, series, index)});
                    this.subscriptionList = this.subscriptionList.substring(0,(this.subscriptionList.length-2));
                });
            }

        }

        calculateTournamentPlayers(){
            if(this.tournament && this.tournament.series) {
                return this.tournament.series
                    .map((series) => series.seriesPlayers.length ? series.seriesPlayers.length : 0)
                    .reduce((acc, newVal) => acc + newVal, 0);
            }else{
                return 0;
            }
        }

        getSubscriptionOfSeries(seriesSubscriptions, series, index){
            if(this.tournament.maximumNumberOfSeriesEntries > index){

                this.subscriptionList += series.seriesName+", ";
            }
        };

        querySearch(query){
                return query ? this.allPlayers.filter(this.createFilterFor(query)) : this.allPlayers;
        }


        createFilterFor(query) {
                var lowercaseQueryList = angular.lowercase(query).split(' ');
                return function filterFn(person) {
                    var contains = false;

                    lowercaseQueryList.map(function(queryPart){
                        contains = contains || (angular.lowercase(person.firstname).indexOf(queryPart) === 0) || (angular.lowercase(person.lastname).indexOf(queryPart) === 0);
                    });
                    return contains;
                };
            }

        enterPlayer(){
        var seriesToSubscribe = [];
         this.subscription.seriesSubscriptions.map(function(subscriptionId){
                seriesToSubscribe.push(subscriptionId)
            }
        );
        this.createSubscription();
    };

    createSubscription(){
        var selectedPlayer: any = this.playerSelection;
        var subscriptionList = this.subscription.seriesSubscriptions.map( (series) => series.id);

        this.playerService.subscribePlayer(subscriptionList, this.tournament.series.map((series) => series.id), selectedPlayer.selectedItem).then((resp) =>{
            this.tournament.series.map((series) => {
                this.seriesService.fetchSeriesPlayers(series.id).then(
                    (seriesPlayers: any) => {series.seriesPlayers = seriesPlayers.data.map( (result) => result.player);}
                )
            });
            this.playerSelection.searchText = null;
            this.playerSelection.selectedItem = null;
            this.createSubscriptions();
            this.subscriptionList = "";
        })
    };


        gotoSeriesSetup(){
        this.$location.path("tournament/" + this.$routeParams["tournamentId"] + "/seriesSetup")
    };

        gotoRoundsSetup(){
        this.$location.path("/tournament/" + this.$routeParams['tournamentId'] + "/roundsSetup");
    };



    }

    angular.module("managerControllers").controller("PlayerSubscriptionController", PlayerSubscriptionController)
}