module TournamentManagement{


    import ILocationService = angular.ILocationService;
    

    class MenuController{

        static $inject= ["$location", "authService"];
        private theme: string;
        
        constructor(private $location: ILocationService, private authservice: AuthService){
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