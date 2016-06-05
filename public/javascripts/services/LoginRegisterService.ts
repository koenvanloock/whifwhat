module TournamentManagement{

    export class LoginRegisterService{

        constructor(private $http: angular.IHttpService, private base: any){}
        customSave(credentials: LoginCredentials){
            return this.$http.post(this.base.url + "/login", credentials);
        }
    }

    angular.module("managerServices").factory("LoginRegisterService", ['$http', 'base', ($http, base) => {
        return new LoginRegisterService($http, base);
    }]);
}