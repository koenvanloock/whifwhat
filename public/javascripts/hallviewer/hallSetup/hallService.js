var HallOverview;
(function (HallOverview) {
    var HallService = (function () {
        function HallService($http, base) {
            this.$http = $http;
            this.base = base;
        }
        HallService.prototype.getHalls = function () {
            return this.$http.get(this.base.url + '/hall');
        };
        HallService.prototype.createHall = function (hallName, numberOfTableRows, numberOfTables, isGreen) {
            return this.$http.post(this.base.url + '/hall', { hallName: hallName, rows: numberOfTableRows, tablesPerRow: numberOfTables, isGreen: isGreen });
        };
        HallService.prototype.getHallById = function (hallId) {
            return this.$http.get(this.base.url + "/hall/" + hallId);
        };
        HallService.prototype.deleteLayout = function (hallId) {
            return this.$http.delete(this.base.url + "/hall/" + hallId);
        };
        HallService.prototype.update = function (hall) {
            return this.$http.patch(this.base.url + '/hall', hall);
        };
        HallService.prototype.activateHall = function (id) {
            return this.$http.post(this.base.url + '/activehall/' + id, {});
        };
        HallService.prototype.getActiveHall = function () {
            return this.$http.get(this.base.url + '/activehall');
        };
        return HallService;
    }());
    HallOverview.HallService = HallService;
    angular.module("hallViewer").factory("hallService", ['$http', 'base', function ($http, base) {
            return new HallService($http, base);
        }]);
})(HallOverview || (HallOverview = {}));
