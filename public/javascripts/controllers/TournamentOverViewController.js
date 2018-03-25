var TournamentManagement;
(function (TournamentManagement) {
    var TournamentOverViewController = (function () {
        function TournamentOverViewController($scope) {
            this.$scope = $scope;
        }
        TournamentOverViewController.$inject = ["$scope"];
        return TournamentOverViewController;
    }());
    angular.module("managerControllers").controller("TournamentOverviewController", TournamentOverViewController);
})(TournamentManagement || (TournamentManagement = {}));
