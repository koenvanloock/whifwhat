var TournamentManagement;
(function (TournamentManagement) {
    var TitleScreenController = (function () {
        function TitleScreenController($location, authservice, $scope) {
            var _this = this;
            this.$location = $location;
            this.authservice = authservice;
            this.$scope = $scope;
            this.$scope.$watch(function () { return authservice.currentAuthUser; }, function (newVal, oldVal) { if (newVal != oldVal) {
                _this.currentUser = newVal;
            } });
            authservice.isAuthenticated();
        }
        TitleScreenController.prototype.hasUser = function () {
            return this.authservice.getCurrentAuthUser() != undefined;
        };
        TitleScreenController.prototype.logout = function () {
            this.authservice.logout();
        };
        TitleScreenController.prototype.gotoTournamentWizard = function () {
            this.$location.path("/createTournament");
        };
        TitleScreenController.prototype.gotoTournamentList = function () {
            this.$location.path("/tournamentList");
        };
        TitleScreenController.prototype.gotoPlayerManagement = function () {
            this.$location.path("/playerManagement");
        };
        TitleScreenController.prototype.gotoPlayTournament = function () {
            this.$location.path("/playTournament");
        };
        TitleScreenController.prototype.gotoHallViewer = function () {
            this.$location.path("/hallSetup");
        };
        TitleScreenController.prototype.loadTournament = function () { this.$location.path("/loadTournament"); };
        return TitleScreenController;
    }());
    TitleScreenController.$inject = ["$location", "authService", "$scope"];
    angular.module("managerControllers").controller("TitleScreenController", TitleScreenController);
})(TournamentManagement || (TournamentManagement = {}));
