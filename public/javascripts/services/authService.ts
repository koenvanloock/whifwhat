module TournamentManagement {
    'use strict';
    export class AuthUser{
        constructor(private username: string, private userId : string, private role: String){
        }

        getUserId(){
            return this.userId;
        }

        getRole(){
            return this.role;
        }
    }

    export class AuthService {

        currentAuthUser:AuthUser;

        constructor(private $http: angular.IHttpService,
                    private base:Object,
                    private jwtHelper:angular.jwt.IJwtHelper,
                    private $cookies:angular.cookies.ICookiesService,
                    private $location:angular.ILocationService
                    ){
        }


        tokenToAuthUser(token:string) {
            var tokenPayload: any = this.jwtHelper.decodeToken(token);
            return new AuthUser(tokenPayload.username, tokenPayload.id, tokenPayload.role);
        }

        getCurrentAuthUser() {
            return this.currentAuthUser;
        }

        setCurrentAuthUser(user: AuthUser) {
            this.currentAuthUser = user;
        }

        saveToken(token: string) {
            this.$cookies.putObject('auth_token', token);
        }

        isAdmin(){
            return this.currentAuthUser? (this.currentAuthUser.getRole() =="superadmin") : false;
        }

        isAuthenticated(){
            var token = this.$cookies.getObject('auth_token');
            var isValidToken = !!token;

            if (isValidToken && ! this.jwtHelper.isTokenExpired(token)) {
                this.setCurrentAuthUser(this.tokenToAuthUser(token));
                return isValidToken;
            } else {
                this.logout();
            }
        }

        logout() {
            this.$cookies.remove('auth_token');
            this.currentAuthUser = null;
            //this.alertService.alerts = [];
            this.$location.path("/login");
        }

    }


    angular.module("managerServices").factory("authService", ['$http', 'base', 'jwtHelper', '$cookies', '$location', ($http, base, jwtHelper, $cookies, $location) => {
        return new AuthService($http, base, jwtHelper, $cookies, $location);
    }]);
}