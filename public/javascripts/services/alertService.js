var TournamentManagement;
(function (TournamentManagement) {
    'use strict';
    var AlertService = (function () {
        function AlertService($mdToast) {
            this.$mdToast = $mdToast;
            this.alerts = new Array();
        }
        AlertService.prototype.addAlert = function (alert) {
            var errorInList;
            if (this.alerts != undefined) {
                errorInList = this.alerts
                    .map(function (currentAlert) {
                    return currentAlert.msg == alert.msg;
                })
                    .reduce(function (prev, current) {
                    return prev || current;
                }, false);
            }
            else {
                errorInList = false;
            }
            if (!errorInList) {
                this.showError(alert);
            }
        };
        AlertService.prototype.getAlerts = function () {
            return this.alerts;
        };
        AlertService.prototype.closeAlert = function (index) {
            this.alerts.splice(index, 1);
        };
        AlertService.prototype.showError = function (alert) {
            this.$mdToast.show({
                hideDelay: alert.timeout,
                controller: 'alertController',
                position: 'top left',
                locals: { msg: alert.msg },
                templateUrl: '../assets/directives/errorPopup.html'
            });
        };
        return AlertService;
    }());
    TournamentManagement.AlertService = AlertService;
    angular.module("managerControllers").controller("alertController", ["$scope", "msg", function ($scope, msg) {
            $scope.msg = msg;
            console.log(msg);
        }]);
    angular.module("managerServices").factory("alertService", ["$mdToast", function ($mdToast) {
            return new AlertService($mdToast);
        }]);
})(TournamentManagement || (TournamentManagement = {}));
