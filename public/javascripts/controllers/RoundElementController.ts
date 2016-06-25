module TournamentManagement {
    export interface RoundType {
        type:string;
        name:string;
    }

    export interface BracketRound {
        name:string;
        value:number;
    }

    class RoundElementController {
        private roundTypes:Array<RoundType>;
        private numberOfRobinsList:Array<number>;
        private bracketRounds: Array<BracketRound>;
        private selectedRound: SeriesRound;

        constructor() {
            this.roundTypes = [
                {"type": "R", "name": "Pouleronde"},
                {"type": "B", "name": "Tabel"}
            ];

            this.numberOfRobinsList = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
            this.bracketRounds = [
                {"name": "zestiende finales", "value": 5},
                {"name": "achtste finales", "value": 4},
                {"name": "kwartfinales", "value": 3},
                {"name": "halve finales", "value": 2},
                {"name": "finale", "value": 1}
            ];
        }


        showSeriesRoundType() {
            if (this.selectedRound.roundType == "R") {
                return this.roundTypes[0];
            } else if (this.selectedRound.roundType == "B") {
                return this.roundTypes[1];
            } else {
                return {};
            }
        };


        showBracketRounds() {
            var numberOfBrackets = this.selectedRound.numberOfBracketRounds;
            switch (numberOfBrackets) {
                case 1:
                    return "finale (2 spelers)";
                case 2:
                    return "halve finales (4 spelers)";
                case 3:
                    return "kwartfinales (8 spelers)";
                case 4:
                    return "achtste finale (16 spelers)";
                case 5:
                    return "zestiende finales (32 spelers)";
                default:
                    return "Kies de startronde";
            }
        };

        updateRound(round) {
            var roundToUpdate = {
                roundType: round.roundType.type,
                seriesRoundId: round.seriesRoundId,
                numberOfBracketRounds: 0,
                numberOfRobinGroups: 0,
                seriesId: round.seriesId,
                roundNr: round.roundNr
            };
            seriesRoundService.updateSeriesRound(roundToUpdate).then(
                function (result) {
                    $scope.round = {
                        roundType: (result.data.roundType.type != undefined || result.data.roundType == "B") ? $scope.roundTypes[1] : $scope.roundTypes[0],
                        seriesRoundId: result.data.seriesRoundId,
                        numberOfBracketRounds: result.data.numberOfBracketRounds,
                        numberOfRobinGroups: result.data.numberOfRobins,
                        seriesId: result.data.seriesId,
                        roundNr: result.data.roundNr
                    };
                })
        };


        showMoveUp() {
            return $scope.round.roundNr > 1;
        };

        showMoveDown() {
            return $scope.round.roundNr < seriesRoundService.getRoundCount();
        };

        moveSeriesRoundUp() {
            seriesRoundService.moveSeriesUp($scope.round);
        };

        moveSeriesRoundDown() {
            seriesRoundService.moveSeriesDown($scope.round);
        }


    }

    angular.module("managerControllers").controller("roundElementController", RoundElementController)
}