module TournamentRunner{
    
    export class LoadingService{
        
        constructor(private $http: ng.IHttpService, private base: Object){}
        
        loadTournaments = function(){
            return this.$http.get(this.base.url + "/tournaments");
        };
        
        activateTournament = function(tournamentId){
            return this.$http.put(this.base.url + "/activeTournament/"+tournamentId);
        };

        getActiveTournament = function(){
            return this.$http.get(this.base.url + "/activeTournament");
        };
        
        getSeriesOfTournament = function(tournamentId){
            return this.$http.get(this.base.url + "/series/"+tournamentId);
        };

        getRoundsOfActiveSeries = function(seriesId){
            return this.$http.get(this.base.url + "/seriesrounds/"+ seriesId);
        };

        updateSeriesRound = function(roundId, match){
            return this.$http.patch(this.base.url + "/seriesrounds/" + roundId, match);
        };
        isThereActiveTournament = function(){
            return this.$http.get(this.base.url + '/hasActiveTournament')
        }
    }
    
    angular.module("tournamentRunner").factory("loadingService", [ '$http','base', ($http, base) => new LoadingService($http, base)]);
    
}