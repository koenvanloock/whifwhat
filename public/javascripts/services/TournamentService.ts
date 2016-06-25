module TournamentManagement {

    export class TournamentService {

        private selectedTournament: Tournament;

        constructor(private $http:angular.IHttpService, private base:any) {
        }


        addTournament(name, date, hasMultiple, maxEntries, showClub) {
            return this.$http.post(this.base.url + '/tournaments',
                {
                    'tournamentName': name,
                    'tournamentDate': this.getDate(date),
                    'hasMultipleSeries': hasMultiple ? hasMultiple : false,
                    'maximumNumberOfSeriesEntries': parseInt(maxEntries) ? parseInt(maxEntries) : 1,
                    'showClub': showClub ? showClub : false
                });
        }

        getTournament(id) {
            return this.$http.get(this.base.url + '/tournaments/' + id);
        }

        getAllTournaments(){
            return this.$http.get(this.base.url + '/tournaments');
        }

        getDate(date: Date){
            return { day: date.getDate(), month: date.getMonth()+1, year:date.getFullYear()};
        }

        setCurrentTournament(tournament: Tournament){
            this.selectedTournament = tournament;
        }

        getCurrentTournament(){
            return this.selectedTournament;
        }
    }

    angular.module("managerServices").factory("TournamentService", ['$http', 'base', ($http, base) => {
        return new TournamentService($http, base);
    }]);
}