module TournamentRunner{
    
    export class LoadingService{
        
        constructor(private $http: ng.IHttpService, private base: Object){}
        
        loadTournaments = function(){
            return this.$http.get(this.base.url + "/tournaments");
        };
        
        activateTournament = function(tournamentId){
            return this.$http.get(this.base.url + "/activateTournament/"+tournamentId)
        }
    }
    
    angular.module("tournamentRunner").factory("loadingService", [ '$http','base', ($http, base) => new LoadingService($http, base)]);
    
}