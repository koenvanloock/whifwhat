module TournamentManagement {
    'use strict';

    export class AlertService {
        alerts: Array<IAlert>;

        constructor() {
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

            if(!errorInList) this.alerts.push(alert);
        }

        getAlerts() {
            return this.alerts;
        }

        closeAlert(index: number) {
            this.alerts.splice(index, 1);
        }
    }

    angular.module("managerServices").factory("alertService", [() => {
        return new AlertService();
    }]);
}
