var TournamentManagement;
(function (TournamentManagement) {
    var TournamentService = (function () {
        function TournamentService($http, base) {
            this.$http = $http;
            this.base = base;
        }
        TournamentService.prototype.addTournament = function (name, date, hasMultiple, maxEntries, showClub, setTargetScore, numberOfSetsToWin, playingWithHandicap, extraHandicapForRecs, seriesColor, showReferees) {
            return this.$http.post(this.base.url + '/tournaments', {
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
        };
        TournamentService.prototype.getTournament = function (id) {
            return this.$http.get(this.base.url + '/tournaments/' + id);
        };
        TournamentService.prototype.getAllTournaments = function () {
            return this.$http.get(this.base.url + '/tournaments');
        };
        TournamentService.prototype.getTwoDigitNumberString = function (number) {
            return (number < 10) ? "0" + number : number;
        };
        TournamentService.prototype.getDate = function (date) {
            return date.getFullYear() + "-" + this.getTwoDigitNumberString(date.getMonth() + 1) + "-" + this.getTwoDigitNumberString(date.getDate());
        };
        TournamentService.prototype.setCurrentTournament = function (tournament) {
            this.selectedTournament = tournament;
        };
        TournamentService.prototype.getCurrentTournament = function () {
            return this.selectedTournament;
        };
        return TournamentService;
    }());
    TournamentManagement.TournamentService = TournamentService;
    angular.module("managerServices").factory("TournamentService", ['$http', 'base', function ($http, base) {
            return new TournamentService($http, base);
        }]);
})(TournamentManagement || (TournamentManagement = {}));
