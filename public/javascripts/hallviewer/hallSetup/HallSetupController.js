var HallOverview;
(function (HallOverview) {
    var HallOverviewController = (function () {
        function HallOverviewController(hallService, alertService, $location) {
            var _this = this;
            this.hallService = hallService;
            this.alertService = alertService;
            this.$location = $location;
            hallService.getHalls().then(function (success) {
                _this.halls = success.data;
            }, function (error) { _this.alertService.addAlert({ type: "error", msg: error.data, timeout: 3000 }); });
        }
        HallOverviewController.prototype.createHall = function () {
            var _this = this;
            this.hallService.createHall(this.hallName, this.numberOfTableRows, this.numberOfTables, this.isGreen === 'green').then(function (result) { _this.addHall(result.data); _this.clearInputs(); }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        HallOverviewController.prototype.deleteLayout = function (hallId) {
            var _this = this;
            this.hallService.deleteLayout(hallId).then(function (result) {
                _this.halls.map(function (hall, index) {
                    if (hall.id == hallId) {
                        _this.halls.splice(index, 1);
                    }
                });
            });
        };
        HallOverviewController.prototype.addHall = function (createdHall) {
            this.halls.push(createdHall);
        };
        HallOverviewController.prototype.clearInputs = function () {
            this.hallName = "";
            this.numberOfTableRows = 0;
            this.numberOfTables = 0;
        };
        HallOverviewController.prototype.chooseHallForLayout = function (id) {
            this.$location.path("/hallLayout/" + id);
        };
        HallOverviewController.prototype.showHall = function (id) {
            var _this = this;
            this.hallService.activateHall(id).then(function (okResponse) { return _this.$location.path("/hallOverview/"); }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        return HallOverviewController;
    }());
    angular.module("hallViewer").controller("HallSetupController", ["hallService", "alertService", "$location", function (hallService, alertService, $location) { return new HallOverviewController(hallService, alertService, $location); }]);
})(HallOverview || (HallOverview = {}));
