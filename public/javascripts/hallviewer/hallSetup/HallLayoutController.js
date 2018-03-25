var HallOverview;
(function (HallOverview) {
    var HallLayoutController = (function () {
        function HallLayoutController(hallService, alertService, $routeParams, $location) {
            var _this = this;
            this.hallService = hallService;
            this.alertService = alertService;
            this.$routeParams = $routeParams;
            this.$location = $location;
            this.rowList = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
            var hallId = $routeParams["hallId"];
            hallService.getHallById(hallId).then(function (response) { _this.hall = response.data; console.log(_this.hall); }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        }
        HallLayoutController.prototype.saveHall = function () {
            var _this = this;
            this.hallService.update(this.hall).then(function (success) { _this.$location.path("/hallSetup"); }, function (errorResponse) { return _this.alertService.addAlert({ type: "error", msg: errorResponse.data, timeout: 3000 }); });
        };
        return HallLayoutController;
    }());
    angular.module("hallViewer").controller("HallLayoutController", ["hallService", "alertService", "$routeParams", "$location", function (hallService, alertService, $routeParams, $location) { return new HallLayoutController(hallService, alertService, $routeParams, $location); }]);
})(HallOverview || (HallOverview = {}));
