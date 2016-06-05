module TournamentManagement {
    import ILocationService = angular.ILocationService;
    class TitleScreenController{
        static $inject = ["$location", "authService"];
        currentUser: AuthUser;

        constructor(private $location: ILocationService, private authservice: AuthService){
            if(authservice.isAuthenticated()){
                this.currentUser = authservice.currentAuthUser;
            }
        }

        hasUser(){
            return this.currentUser != undefined;
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