module TournamentManagement{
    import IHttpResponseTransformer = angular.IHttpResponseTransformer;

    class TournamentOverViewController {
        static $inject = ["$scope"];

        tournament:Tournament;
        query:any;

        constructor(private $scope:angular.IScope) {

        }



    }
    angular.module("managerControllers").controller("TournamentOverviewController", TournamentOverViewController);
}