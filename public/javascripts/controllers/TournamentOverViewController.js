var TournamentManagement;
(function (TournamentManagement) {
    var TournamentOverViewController = (function () {
        function TournamentOverViewController($scope) {
            this.$scope = $scope;
        }
        return TournamentOverViewController;
    }());
    TournamentOverViewController.$inject = ["$scope"];
    angular.module("managerControllers").controller("TournamentOverviewController", TournamentOverViewController);
})(TournamentManagement || (TournamentManagement = {}));
