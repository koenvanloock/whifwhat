'use strict';

var tournamentmanager = angular.module("tournamentManager", [
    "managerControllers",
    "managerServices",
    'ngRoute',
    'ngCookies',
    'ngMaterial',
    'angular-jwt',
    'ngFileUpload',
    'ngImgCrop']);



angular.module("managerControllers", []);

angular.module("managerServices", [])
    .constant('base', {
    url: window.location.origin
});

tournamentmanager.directive("tournamentOverview", function () {
    return {
        restrict: 'E',
        scope: {tournament: '='},
        templateUrl: "assets/directives/tournamentOverview.html"
    }
});

tournamentmanager.directive("seriesRoundSetup", function(){
    return{
        restrict: 'E',
        scope: {round: '='},
        templateUrl: "assets/directives/SeriesRoundSetup.html",
        controller: "roundElementController",
        controllerAs: "ctrl"
    }
});

tournamentmanager.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('teal')
        .accentPalette('orange')
        .dark();
});