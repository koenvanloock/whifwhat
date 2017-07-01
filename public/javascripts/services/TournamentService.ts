module TournamentManagement {

    export class TournamentService {

        private selectedTournament: Tournament;

        constructor(private $http:angular.IHttpService, private base:any) {
        }


        addTournament(name, date, hasMultiple, maxEntries, showClub, setTargetScore, numberOfSetsToWin, playingWithHandicap, extraHandicapForRecs, seriesColor, showReferees) {
            return this.$http.post(this.base.url + '/tournaments',
                {
                    'tournamentName': name,
                    'tournamentDate': this.getDate(date),
                    'hasMultipleSeries': hasMultiple ? hasMultiple : false,
                    'maximumNumberOfSeriesEntries': parseInt(maxEntries) ? parseInt(maxEntries) : 1,
                    'showClub': showClub ? showClub : false,
                    'setTargetScore': setTargetScore,
                    'numberOfSetsToWin': numberOfSetsToWin,
                    'playingWithHandicaps': playingWithHandicap,
                    'extraHandicapForRecs': extraHandicapForRecs,
                    'seriesColor': seriesColor,
                    'showReferees': showReferees
                });
        }

        getTournament(id) {
            return this.$http.get(this.base.url + '/tournaments/' + id);
        }

        getAllTournaments(){
            return this.$http.get(this.base.url + '/tournaments');
        }

        getTwoDigitNumberString(number: number){
            return (number <10) ? "0"+ number: number;
        }

        getDate(date: Date){
            return  date.getFullYear() +"-"+ this.getTwoDigitNumberString(date.getMonth()+1)+"-"+ this.getTwoDigitNumberString(date.getDate());
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