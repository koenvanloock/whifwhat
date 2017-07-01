module TournamentManagement {
    'use strict';

    export class AlertService {
        alerts: Array<IAlert>;

        constructor(private $mdToast: any) {
            this.alerts = new Array<IAlert>();
        }

        addAlert(alert: IAlert) {
            var errorInList : boolean;
            if(this.alerts != undefined) {
                errorInList= this.alerts
                    .map(function (currentAlert:IAlert) {
                        return currentAlert.msg == alert.msg;
                    })
                    .reduce(function(prev, current) {
                        return prev || current}, false);
            } else{
                errorInList = false;
            }

            if(!errorInList){
                this.showError(alert);
            } 
        }

        getAlerts() {
            return this.alerts;
        }

        closeAlert(index: number) {
            this.alerts.splice(index, 1);
        }

        showError(alert: IAlert){
            this.$mdToast.show({
                hideDelay   : alert.timeout,
                controller: 'alertController',
                position    : 'top left',
                locals: { msg: alert.msg},
                templateUrl : '../assets/directives/errorPopup.html'
            });
        }
        
    }

    angular.module("managerControllers").controller("alertController", ["$scope", "msg", ($scope, msg) => {
        $scope.msg = msg;
        console.log(msg)
    }]);
    angular.module("managerServices").factory("alertService", ["$mdToast", ($mdToast) => {
        return new AlertService($mdToast);
    }]);
}
