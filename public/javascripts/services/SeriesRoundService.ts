module TournamentManagement {


    export class SeriesRoundService {
        private roundsMap: Object;

        constructor(private $http:angular.IHttpService, private base:any) {
            this.roundsMap = {}
        }

        createSeriesRound(seriesId: string) {
            return this.$http.post(this.base.url + "/seriesrounds/" + seriesId,{})
        }

        updateSeriesRound(seriesRoundJson){
            return this.$http.put(this.base.url + "/seriesrounds", seriesRoundJson)
        }

        updateSeriesRoundConfig(seriesRoundJson) {
            return this.$http.put(this.base.url + "/seriesroundsconfig", seriesRoundJson)
        }

        deleteSeriesRound(roundId){
            return this.$http.delete(this.base.url + "/seriesrounds/"+ roundId)
        }

        loadRoundsOfSeries(seriesId) {
            return this.$http.get(this.base.url + "/seriesrounds/" + seriesId).then((result) => {
                this.roundsMap[seriesId] = result.data;
                 return this.roundsMap[seriesId] ? this.roundsMap[seriesId] : [];
            });

        }


        getRoundsOfSeries(seriesId) {
            return this.roundsMap[seriesId] ? this.roundsMap[seriesId] : [];
        }

        getRoundCountOfSeries(seriesId){
            return this.roundsMap[seriesId] ? this.roundsMap[seriesId].length : 0;
        }

        /*
        moveSeriesUp(roundToMove) {
            if (roundToMove.roundNr > 1) {
                var roundNr = roundToMove.roundNr;
                var seriesToMoveUp = this.roundsOfSelectedSeries[roundNr - 1];
                seriesToMoveUp.roundNr -= 1;
                var seriesToMoveDown = this.roundsOfSelectedSeries[roundNr - 2];
                seriesToMoveDown.roundNr += 1;
                this.roundsOfSelectedSeries[roundNr - 2] = this.convertRoundType(seriesToMoveUp);
                this.roundsOfSelectedSeries[roundNr - 1] = this.convertRoundType(seriesToMoveDown);
                this.updateSeriesRoundConfig(seriesToMoveUp);
                this.updateSeriesRoundConfig(seriesToMoveDown);
            }
        }

        moveSeriesDown(roundToMove) {
            var roundNr = roundToMove.roundNr;
            var seriesToMoveDown = this.roundsOfSelectedSeries[roundNr - 1];
            seriesToMoveDown.roundNr += 1;
            var seriesToMoveUp = this.roundsOfSelectedSeries[roundNr];
            seriesToMoveUp.roundNr -= 1;
            this.updateSeriesRoundConfig(seriesToMoveUp);
            this.updateSeriesRoundConfig(seriesToMoveDown);
            this.roundsOfSelectedSeries[roundNr - 1] = this.convertRoundType(seriesToMoveUp);
            this.roundsOfSelectedSeries[roundNr] = this.convertRoundType(seriesToMoveDown);
        }*/

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