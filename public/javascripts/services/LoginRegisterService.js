var TournamentManagement;
(function (TournamentManagement) {
    var LoginRegisterService = (function () {
        function LoginRegisterService($http, base) {
            this.$http = $http;
            this.base = base;
        }
        LoginRegisterService.prototype.customSave = function (credentials) {
            return this.$http.post(this.base.url + "/login", credentials);
        };
        return LoginRegisterService;
    }());
    TournamentManagement.LoginRegisterService = LoginRegisterService;
    angular.module("managerServices").factory("LoginRegisterService", ['$http', 'base', function ($http, base) {
            return new LoginRegisterService($http, base);
        }]);
})(TournamentManagement || (TournamentManagement = {}));
