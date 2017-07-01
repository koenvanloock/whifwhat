module TournamentManagement{


    import ILocationService = angular.ILocationService;
    import IScope = angular.IScope;
    

    class MenuController{

        static $inject= ["$scope", "$location", "authService","TournamentService"];
        private theme: string;
        private currentTournamentName: string;
        
        constructor(private $scope: IScope, private $location: ILocationService, private authservice: AuthService, private tournamentService: TournamentService){
        }


        openMenu($mdOpenMenu, ev) {
            $mdOpenMenu(ev);
        };
        closeMenu($mdMenuClose, ev) {
            $mdMenuClose(ev);
        };
        
        themeSwitch(){
            if(this.theme || this.theme === 'default'){
                this.theme = 'light';
            }else{
                this.theme = 'default';
            }
        }

        gotoHome(){
            return this.$location.path("/");
        }

        logout(){
            this.authservice.logout();

        }
    }

    angular.module("managerControllers").controller("menuController", MenuController);
    
}