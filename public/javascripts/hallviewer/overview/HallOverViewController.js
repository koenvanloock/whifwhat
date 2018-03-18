var HallOverview;
(function (HallOverview) {
    var HallOverviewController = (function () {
        function HallOverviewController(base, $location, alertService, $scope) {
            this.base = base;
            this.$location = $location;
            this.alertService = alertService;
            this.$scope = $scope;
            this.rowList = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
        }
        return HallOverviewController;
    }());
    angular.module("hallViewer").controller("HallOverviewController", ["base", "$location", "alertService", "$rootScope", function (base, $location, alertService, $scope) { return new HallOverviewController(base, $location, alertService, $scope); }]);
})(HallOverview || (HallOverview = {}));
