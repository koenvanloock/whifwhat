module TournamentRunner{
    
    import Tournament = TournamentManagement.Tournament;
    import Series = TournamentManagement.Series;
    import SeriesRound = TournamentManagement.SeriesRound;
    import IRootScopeService = angular.IRootScopeService;
    class OverviewController{

        private tournament: Tournament;
        private error: string;
        private seriesList: Array<Series>;
        private selectedSeries: Series;
        private activeSeriesRoundList: Array<SeriesRound>;
        private selectedRound: SeriesRound;

        constructor(private loadingService: LoadingService, private $rootScope: IRootScopeService){
            loadingService.getActiveTournament().then((response) => {
                this.tournament= response.data;
                loadingService.getSeriesOfTournament(this.tournament.id).then((response) => {
                    this.seriesList = response.data;
                    if(this.seriesList.length > 0){ this.selectedSeries = this.seriesList[0]}
                });

            },
                (response) => this.error = response.data);
            
            this.$rootScope.$watch( () =>this.selectedSeries, (newVal, oldVal) => { if(newVal != oldVal){ this.loadRoundsOfSeries(newVal.id)}} )
        };
        
        
        loadRoundsOfSeries(seriesId){
            this.loadingService.getRoundsOfActiveSeries(seriesId).then( (response) => {
                if(response.data.length > 0){ this.selectedRound = response.data[0];}
                this.activeSeriesRoundList = response.data });
        };


        activeBracketRound(){
            return this.selectedRound && this.selectedRound.roundType == "B";
        };

        activeRobinRound(){
            return this.selectedRound && this.selectedRound.roundType == "R";
        };

        showRoundOption(round){
            var nr = round.roundNr + ". ";
            return (round.roundType === "B") ? nr + "Tabel" : nr + "Poules";
        };


        updateRound(roundId, match){
            this.loadingService.updateSeriesRound(roundId, match).then( (response) => {
                
                this.activeSeriesRoundList.map(  (round, index) => {if(round.id === response.data.id){
                    this.activeSeriesRoundList[index] = response.data;
                    if(response.data.id === this.selectedRound.id){
                        this.selectedRound = response.data;
                    }
                } })
            });
        };
    }
    
    
    
    angular.module("tournamentRunner").controller("OverviewController",['loadingService', '$rootScope', (loadingService, rootScope) => new OverviewController(loadingService, rootScope)])
}