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
        static $inject = ["SeriesRoundService","$rootScope"];

        private roundTypes:Array<RoundType>;
        private numberOfRobinsList:Array<number>;
        private bracketRounds: Array<BracketRound>;
        private selectedRound: SeriesRound;

        constructor(private seriesRoundService: SeriesRoundService, private $rootScope: ng.IRootScopeService) {
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

            $rootScope.$watch( () => this.selectedRound, ( newval: any, oldval) => {
                if(newval!= undefined){ this.selectType(newval.roundType)}} );
        }


        showSeriesRoundType() {
            if (this.selectedRound.roundType.type == "R") {
                return this.roundTypes[0];
            } else if (this.selectedRound.roundType.type == "B") {
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

        selectType(roundType: string){

            this.roundTypes.map( (type) => {
                if(type.type === roundType)
                this.selectedRound.roundType = type;
            });

        }

        updateRound() {
            var roundToUpdate = {
                roundType: this.selectedRound.roundType.type,
                seriesRoundId: this.selectedRound.seriesRoundId,
                numberOfBracketRounds: this.selectedRound.numberOfBracketRounds ? parseInt(this.selectedRound.numberOfBracketRounds) : 0,
                numberOfRobinGroups: this.selectedRound.numberOfRobinGroups ? parseInt(this.selectedRound.numberOfRobinGroups.toString()) : 0,
                seriesId: this.selectedRound.seriesId,
                roundNr: this.selectedRound.roundNr
            };
            this.seriesRoundService.updateSeriesRound(roundToUpdate).then(
                (result: any) => {
                    this.selectedRound = {
                        roundType: (result.data.roundType.type != undefined || result.data.roundType.type == "B") ? this.roundTypes[1] : this.roundTypes[0],
                        seriesRoundId: result.data.seriesRoundId,
                        numberOfBracketRounds: result.data.numberOfBracketRounds,
                        numberOfRobinGroups: result.data.numberOfRobins,
                        seriesId: result.data.seriesId,
                        roundNr: result.data.roundNr
                    };
                })
        };


        showMoveUp() {
            return (this.selectedRound && this.selectedRound.roundNr) ? this.selectedRound.roundNr > 1 : false;
        };

        showMoveDown() {
            return (this.selectedRound && this.selectedRound.roundNr) ? this.selectedRound.roundNr < this.seriesRoundService.getRoundCount() : false;
        };

        moveSeriesRoundUp() {
            this.seriesRoundService.moveSeriesUp(this.selectedRound);
        };

        moveSeriesRoundDown() {
            this.seriesRoundService.moveSeriesDown(this.selectedRound);
        }


    }

    angular.module("managerControllers").controller("roundElementController", RoundElementController)
}