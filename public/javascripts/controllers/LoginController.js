var TournamentManagement;
(function (TournamentManagement) {
    var LoginController = (function () {
        function LoginController(Login, alertService, authService, $location) {
            this.Login = Login;
            this.alertService = alertService;
            this.authService = authService;
            this.$location = $location;
            this.loginFailed = false;
            this.credentials = new LoginCredentials(undefined, undefined);
        }
        LoginController.prototype.loginAction = function () {
            var _this = this;
            this.loginFailed = false;
            this.Login.customSave(this.credentials).then(function (response) {
                var token = response.data.toString();
                _this.authService.saveToken(token);
                _this.authService.setCurrentAuthUser(_this.authService.tokenToAuthUser(token));
                _this.$location.path("/");
            }, function () {
                var alert = {
                    type: "danger",
                    msg: "Ongeldige logingegevens",
                    timeout: 3000
                };
                _this.alertService.addAlert(alert);
                _this.loginFailed = true;
            });
        };
        LoginController.$inject = ["LoginRegisterService", "alertService", "authService", "$location"];
        return LoginController;
    }());
    var LoginCredentials = (function () {
        function LoginCredentials(username, password) {
            this.username = username;
            this.password = password;
        }
        return LoginCredentials;
    }());
    TournamentManagement.LoginCredentials = LoginCredentials;
    angular.module("managerControllers").controller("LoginController", LoginController);
})(TournamentManagement || (TournamentManagement = {}));
