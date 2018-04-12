var TournamentManagement;
(function (TournamentManagement) {
    var RoundElementController = (function () {
        function RoundElementController(seriesRoundService, $rootScope) {
            var _this = this;
            this.seriesRoundService = seriesRoundService;
            this.$rootScope = $rootScope;
            this.roundTypes = [
                { "type": "R", "name": "Pouleronde" },
                { "type": "B", "name": "Tabel" }
            ];
            this.numberOfRobinsList = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
            this.bracketRounds = [
                { "name": "finale", "value": 1 },
                { "name": "halve finales", "value": 2 },
                { "name": "kwartfinales", "value": 3 },
                { "name": "achtste finales", "value": 4 },
                { "name": "zestiende finales", "value": 5 }
            ];
            $rootScope.$watch(function () { return _this.selectedRound; }, function (newval, oldval) {
                if (newval != undefined) {
                    _this.selectType(newval.roundType);
                    _this.setBracketRounds();
                }
            });
        }
        RoundElementController.prototype.showSeriesRoundType = function () {
            if (this.selectedRound.roundType.type == "R") {
                return this.roundTypes[0];
            }
            else if (this.selectedRound.roundType.type == "B") {
                return this.roundTypes[1];
            }
            else {
                return {};
            }
        };
        ;
        RoundElementController.prototype.setBracketRounds = function () {
            if (this.selectedRound.numberOfBracketRounds) {
                this.selectedNumberOfBracketRounds = this.bracketRounds[this.selectedRound.numberOfBracketRounds - 1];
                this.selectedRound.numberOfBracketRounds = null;
            }
        };
        ;
        RoundElementController.prototype.selectType = function (roundType) {
            var _this = this;
            this.roundTypes.map(function (type) {
                if (type.type === roundType)
                    _this.selectedRound.roundType = type;
            });
        };
        RoundElementController.prototype.updateRound = function () {
            var _this = this;
            var roundToUpdate = {
                roundType: this.selectedRound.roundType.type,
                id: this.selectedRound.id,
                numberOfBracketRounds: this.selectedNumberOfBracketRounds ? this.selectedNumberOfBracketRounds.value : 0,
                numberOfRobinGroups: this.selectedRound.numberOfRobinGroups ? parseInt(this.selectedRound.numberOfRobinGroups.toString()) : 0,
                seriesId: this.selectedRound.seriesId,
                roundNr: this.selectedRound.roundNr
            };
            this.seriesRoundService.updateSeriesRoundConfig(roundToUpdate).then(function (result) {
                _this.selectedRound = {
                    roundType: (result.data.roundType.type != undefined || result.data.roundType.type == "B") ? _this.roundTypes[1] : _this.roundTypes[0],
                    id: result.data.id,
                    numberOfBracketRounds: result.data.numberOfBracketRounds,
                    numberOfRobinGroups: result.data.numberOfRobins,
                    seriesId: result.data.seriesId,
                    roundNr: result.data.roundNr
                };
            }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        ;
        RoundElementController.prototype.showMoveUp = function () {
            return (this.selectedRound && this.selectedRound.roundNr) ? this.selectedRound.roundNr > 1 : false;
        };
        ;
        RoundElementController.prototype.showMoveDown = function () {
            return (this.selectedRound && this.selectedRound.roundNr) ? this.selectedRound.roundNr < this.seriesRoundService.getRoundCountOfSeries(this.selectedRound.seriesId) : false;
        };
        ;
        return RoundElementController;
    }());
    RoundElementController.$inject = ["SeriesRoundService", "$rootScope"];
    angular.module("managerControllers").controller("roundElementController", RoundElementController);
})(TournamentManagement || (TournamentManagement = {}));
