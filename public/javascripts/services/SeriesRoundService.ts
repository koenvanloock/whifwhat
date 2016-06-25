module TournamentManagement {


    export class SeriesRoundService {
        private roundsOfSelectedSeries:Array<SeriesRound>;

        constructor(private $http:angular.IHttpService, private base:any) {
        }

        createSeriesRound(seriesRoundJson) {
            return this.$http.post(this.base.url + "/seriesround", seriesRoundJson)
        }

        updateSeriesRound(seriesRoundJson) {
            return this.$http.put(this.base.url + "/seriesround", seriesRoundJson)
        }

        loadRoundsOfSeries(seriesId) {
            return this.$http.get(this.base.url + "/seriesrounds/" + seriesId);
        }

        setRoundsOfSeries(roundsOfSeries) {
            this.roundsOfSelectedSeries = roundsOfSeries;
        }

        getRoundsOfSeries() {
            return this.roundsOfSelectedSeries;
        }

        getRoundCount() {
            return this.roundsOfSelectedSeries.length;
        }

        moveSeriesUp(roundToMove) {
            if (roundToMove.roundNr > 1) {
                var roundNr = roundToMove.roundNr;
                var seriesToMoveUp = this.roundsOfSelectedSeries[roundNr - 1];
                seriesToMoveUp.roundNr -= 1;
                var seriesToMoveDown = this.roundsOfSelectedSeries[roundNr - 2];
                seriesToMoveDown.roundNr += 1;
                this.roundsOfSelectedSeries[roundNr - 2] = this.convertRoundType(seriesToMoveUp);
                this.roundsOfSelectedSeries[roundNr - 1] = this.convertRoundType(seriesToMoveDown);
                this.updateSeriesRound(seriesToMoveUp);
                this.updateSeriesRound(seriesToMoveDown);
            }
        }

        moveSeriesDown(roundToMove) {
            var roundNr = roundToMove.roundNr;
            var seriesToMoveDown = this.roundsOfSelectedSeries[roundNr - 1];
            seriesToMoveDown.roundNr += 1;
            var seriesToMoveUp = this.roundsOfSelectedSeries[roundNr];
            seriesToMoveUp.roundNr -= 1;
            this.updateSeriesRound(seriesToMoveUp);
            this.updateSeriesRound(seriesToMoveDown);
            this.roundsOfSelectedSeries[roundNr - 1] = this.convertRoundType(seriesToMoveUp);
            this.roundsOfSelectedSeries[roundNr] = this.convertRoundType(seriesToMoveDown);
        }

        convertRoundType(round) {
            if (round.roundType) {
                switch (round.roundType) {
                    case "B":
                    default:
                        round.roundType = {type: "B", name: "Tabel"};
                        break;
                    case "R":
                        round.roundType = {type: "R", name: "Poulerounde"};
                }
            }
            return round;
        }

    }

    angular.module("managerServices").factory("SeriesRoundService", ['$http', 'base', ($http, base) => {
        return new SeriesRoundService($http, base);
    }]);
}