'use strict';

var tournamentmanager = angular.module("tournamentManager", [
    "managerControllers",
    "managerServices",
    'ngRoute',
    'ngCookies',
    'ngMaterial',
    'angular-jwt']);



angular.module("managerControllers", []);

angular.module("managerServices", [])
    .constant('base', {
    url: window.location.origin
});

tournamentmanager.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('grey')
        .accentPalette('teal')
        .dark();
});