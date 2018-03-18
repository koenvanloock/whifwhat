var TournamentManagement;
(function (TournamentManagement) {
    var MenuController = (function () {
        function MenuController($scope, $location, authservice, tournamentService) {
            this.$scope = $scope;
            this.$location = $location;
            this.authservice = authservice;
            this.tournamentService = tournamentService;
        }
        MenuController.prototype.openMenu = function ($mdOpenMenu, ev) {
            $mdOpenMenu(ev);
        };
        ;
        MenuController.prototype.closeMenu = function ($mdMenuClose, ev) {
            $mdMenuClose(ev);
        };
        ;
        MenuController.prototype.themeSwitch = function () {
            if (this.theme || this.theme === 'default') {
                this.theme = 'light';
            }
            else {
                this.theme = 'default';
            }
        };
        MenuController.prototype.gotoHome = function () {
            return this.$location.path("/");
        };
        MenuController.prototype.logout = function () {
            this.authservice.logout();
        };
        return MenuController;
    }());
    MenuController.$inject = ["$scope", "$location", "authService", "TournamentService"];
    angular.module("managerControllers").controller("menuController", MenuController);
})(TournamentManagement || (TournamentManagement = {}));
