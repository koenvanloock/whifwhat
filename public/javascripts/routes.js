angular.module('tournamentManager')
    .config(function ($routeProvider, $httpProvider, jwtInterceptorProvider) {
        $routeProvider
            .when('/', {
                controller: "TitleScreenController",
                controllerAs: 'ctrl',
                requiresLogin: true
            })
            .when('/login', {
                controller: "LoginController",
                controllerAs: 'loginCtrl',
                templateUrl: 'assets/partials/login.html',
                requiresLogin: false
            })
            .when('/createTournament', {
                templateUrl: "assets/partials/tournamentSetup.html",
                controller: 'CreateTournamentController',
                controllerAs: 'createTournamentCtrl',
                requiresLogin: true
            })
            .when('/tournament/:tournamentId/seriesSetup', {
                templateUrl: "assets/partials/createSeries.html",
                controller: 'SeriesSetupController',
                controllerAs: 'seriesSetupCtrl',
                requiresLogin: true
            })
            .when('/tournament/:tournamentId/playerSubscription', {
                templateUrl: "assets/partials/playerSubscription.html",
                controller: 'PlayerSubscriptionController',
                controllerAs: 'playerSubscrCtrl',
                access: {
                    requiresLogin: true
                }
            })
            .when('/tournament/:tournamentId/roundsSetup', {
                templateUrl: "assets/partials/seriesRoundSetup.html",
                controller: 'SeriesRoundSetupController',
                controllerAs: 'roundCtrl',
                access: {
                    requiresLogin: true
                }
            })
            .when('/tournamentList', {
                templateUrl: "assets/partials/tournamentList.html",
                controller: 'TournamentListController',
                controllerAs: 'tournamentListCtrl',
                requiresLogin: true
            })
            .otherwise({
                requiresLogin: true
            });

        jwtInterceptorProvider.tokenGetter = ['$cookies', function ($cookies) {
            return $cookies.get('auth_token');
        }];
        $httpProvider.interceptors.push('jwtInterceptor')
    })
    .
    run(['$rootScope', '$location', 'authService',
        function ($rootScope, $location, authService) {

            $rootScope.$on('$routeChangeStart', function (event, next) {
                if (next.$$route != undefined && next.$$route.requiresLogin) {
                    if (!authService.isAuthenticated()) {
                        event.preventDefault();
                        $location.path('/login');
                    }
                }
            })
        }]);