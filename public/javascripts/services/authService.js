var TournamentManagement;
(function (TournamentManagement) {
    'use strict';
    var AuthUser = (function () {
        function AuthUser(username, userId, role) {
            this.username = username;
            this.userId = userId;
            this.role = role;
        }
        AuthUser.prototype.getUserId = function () {
            return this.userId;
        };
        AuthUser.prototype.getRole = function () {
            return this.role;
        };
        return AuthUser;
    }());
    TournamentManagement.AuthUser = AuthUser;
    var AuthService = (function () {
        function AuthService($http, base, jwtHelper, $cookies, $location) {
            this.$http = $http;
            this.base = base;
            this.jwtHelper = jwtHelper;
            this.$cookies = $cookies;
            this.$location = $location;
        }
        AuthService.prototype.tokenToAuthUser = function (token) {
            var tokenPayload = this.jwtHelper.decodeToken(token);
            return new AuthUser(tokenPayload.username, tokenPayload.id, tokenPayload.role);
        };
        AuthService.prototype.getCurrentAuthUser = function () {
            return this.currentAuthUser;
        };
        AuthService.prototype.setCurrentAuthUser = function (user) {
            this.currentAuthUser = user;
        };
        AuthService.prototype.saveToken = function (token) {
            this.$cookies.putObject('auth_token', token);
        };
        AuthService.prototype.isAdmin = function () {
            return this.currentAuthUser ? (this.currentAuthUser.getRole() == "superadmin") : false;
        };
        AuthService.prototype.isAuthenticated = function () {
            var token = this.$cookies.getObject('auth_token');
            var isValidToken = !!token;
            if (isValidToken && !this.jwtHelper.isTokenExpired(token)) {
                this.setCurrentAuthUser(this.tokenToAuthUser(token));
                return isValidToken;
            }
            else {
                this.logout();
            }
        };
        AuthService.prototype.logout = function () {
            this.$cookies.remove('auth_token');
            this.currentAuthUser = null;
            this.$location.path("/login");
        };
        return AuthService;
    }());
    TournamentManagement.AuthService = AuthService;
    angular.module("managerServices").factory("authService", ['$http', 'base', 'jwtHelper', '$cookies', '$location', function ($http, base, jwtHelper, $cookies, $location) {
            return new AuthService($http, base, jwtHelper, $cookies, $location);
        }]);
})(TournamentManagement || (TournamentManagement = {}));
