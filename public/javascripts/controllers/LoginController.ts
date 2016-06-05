module TournamentManagement{
    class LoginController {
        static $inject = ["LoginRegisterService", "alertService", "authService", "$location"];
        loginFailed: boolean;
        credentials: LoginCredentials;
        iid: string;

        constructor(
            private Login: LoginRegisterService,
            private alertService: AlertService,
            private authService: AuthService,
            private $location: ng.ILocationService
        ) {
            this.loginFailed = false;
            this.credentials = new LoginCredentials(undefined,undefined);
        }

        loginAction() {
            this.loginFailed = false;
            this.Login.customSave(this.credentials).then(
                (response) => {
                    var token = response.data.toString();
                    this.authService.saveToken(token);
                    this.authService.setCurrentAuthUser(this.authService.tokenToAuthUser(token));
                    this.$location.path("/");
                },
                () => {
                    var alert: IAlert = {
                        type: "danger",
                        msg: "Ongeldige logingegevens",
                        timeout: 3000
                    };
                    this.alertService.addAlert(alert);
                    this.loginFailed = true;
                }
            );
        }
    }

    export class LoginCredentials {
        constructor(private username: string, private password: string) {}
    }

    angular.module("managerControllers").controller("LoginController", LoginController);

}