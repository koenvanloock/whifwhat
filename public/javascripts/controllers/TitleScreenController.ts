module TournamentManagement {
    import ILocationService = angular.ILocationService;
    import IScope = angular.IScope;
    class TitleScreenController{
        static $inject = ["$location", "authService", "$scope"];
        currentUser: AuthUser;

        constructor(private $location: ILocationService, private authservice: AuthService, private $scope: IScope){
            this.$scope.$watch(() => authservice.currentAuthUser, (newVal, oldVal) => {if(newVal != oldVal){this.currentUser = newVal}} );
            authservice.isAuthenticated();
        }

        hasUser(){
            return this.authservice.getCurrentAuthUser()!=undefined;
        }

        logout(){
            console.log("logging out");
            this.authservice.logout();

        }
        gotoTournamentWizard(){
            console.log("test");
            this.$location.path("/createTournament");}

        gotoTournamentList(){
            this.$location.path("/tournamentList")
        }

        loadTournament(){this.$location.path("/loadTournament")}
    }

    angular.module("managerControllers").controller("TitleScreenController", TitleScreenController)
}