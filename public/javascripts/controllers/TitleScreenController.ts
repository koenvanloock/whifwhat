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
            this.authservice.logout();

        }
        gotoTournamentWizard(){
            this.$location.path("/createTournament");}

        gotoTournamentList(){
            this.$location.path("/tournamentList")
        }

        gotoPlayerManagement(){
            this.$location.path("/playerManagement");
        }

        gotoPlayTournament(){
            this.$location.path("/playTournament")
        }


        loadTournament(){this.$location.path("/loadTournament")}
    }

    angular.module("managerControllers").controller("TitleScreenController", TitleScreenController)
}