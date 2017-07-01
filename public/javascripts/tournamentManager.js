'use strict';

var tournamentmanager = angular.module("tournamentManager", [
    "managerControllers",
    "managerServices",
    'tournamentRunner',
    'hallViewer',
    'ngRoute',
    'ngCookies',
    'ngMaterial',
    'angular-jwt',
    'ngFileUpload',
    'ngImgCrop',
    'md.data.table'
]);



angular.module("managerControllers", []);

angular.module("managerServices", [])
    .constant('base', {
    url: window.location.origin
});

tournamentmanager.directive("tournamentOverview", function () {
    return {
        restrict: 'E',
        scope: {tournament: '=', query: '='},
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

tournamentmanager.directive("seriesPlayers", function(){
    return{
        restrict: 'E',
        scope: {query: '=', seriesPlayers: '='},
        templateUrl: "assets/directives/seriesplayers.html"
    }
});

tournamentmanager.directive("roundPlayers", function(){
    return{
        restrict: 'E',
        scope: {roundPlayers: '='},
        templateUrl: "assets/directives/roundPlayers.html"
    }
});

tournamentmanager.directive("robinMatches", function(){
    return{
        restrict: 'E',
        scope: {robinMatches: '='},
        templateUrl: "assets/directives/robinMatches.html"
    }
});

tournamentmanager.directive("robinMatch", function(){
    return{
        restrict: 'E',
        scope: {match: '='},
        templateUrl: "assets/directives/robinMatch.html"
    }
});

tournamentmanager.directive("bracketNode", ['$compile',function ($compile) {

    return {
        restrict: 'E',
        replace: true,
        scope: {
            node: '='
        },
        template: '<div layout="row">' +
        '<div layout="column">' +
            '<div ng-if="node.left"><bracket-node node="node.left"></bracket-node></div>'+
            '<div ng-if="node.right"><bracket-node node="node.right"></bracket-node></div>' +
        '</div>'+
        '<bracket-match match="node.value"></bracket-match>' +
        '</div>',
        compile: function(element, link){
            // Normalize the link parameter
            if(angular.isFunction(link)){
                link = { post: link };
            }

            // Break the recursion loop by removing the contents
            var contents = element.contents().remove();
            var compiledContents;
            return {
                pre: (link && link.pre) ? link.pre : null,
                /**
                 * Compiles and re-adds the contents
                 */
                post: function(scope, element){
                    // Compile the contents
                    if(!compiledContents){
                        compiledContents = $compile(contents);
                    }
                    // Re-add the compiled contents to the element
                    compiledContents(scope, function(clone){
                        element.append(clone);
                    });

                    // Call the post-linking function, if any
                    if(link && link.post){
                        link.post.apply(null, arguments);
                    }
                }
            };
        }
    };

}]);

tournamentmanager.directive("bracketMatch", function(){
   return{
       restrict: 'E',
       scope: { match: '='},
       templateUrl: "assets/directives/bracketMatch.html"
   } 
});

tournamentmanager.directive('economicMatchFormat', function(){
    return{
        restrict: 'E',
        scope: { match: '=', myStyle: '=', seriesName: '='},
        templateUrl: 'assets/directives/economicMatchFormat.html'
    }
});

tournamentmanager.directive('fabulousMatchFormat', function(){
    return{
        restrict: 'E',
        scope: { match: '=', myStyle: '=', seriesName: '='},
        templateUrl: 'assets/directives/fabulousMatchFormat.html'
    }
});

tournamentmanager.directive('ngEnter', function() {
  return function(scope, element, attrs) {
    element.bind("keydown keypress", function(event) {
      if(event.which === 13) {
        scope.$apply(function(){
          scope.$eval(attrs.ngEnter, {'event': event});
        });

        event.preventDefault();
      }
    });
  };
});

tournamentmanager.config(function($mdThemingProvider) {
    $mdThemingProvider.alwaysWatchTheme(true);
    $mdThemingProvider.theme('default')
        .primaryPalette('teal')
        .accentPalette('orange')
        .dark();
}).config(function($mdThemingProvider) {
    $mdThemingProvider.definePalette('white', {
        '50': 'ffffff',
        '100': 'ffffff',
        '200': 'ffffff',
        '300': 'ffffff',
        '400': 'ffffff',
        '500': 'ffffff',
        '600': 'ffffff',
        '700': 'ffffff',
        '800': 'ffffff',
        '900': 'ffffff',
        'A100': 'ffffff',
        'A200': 'ffffff',
        'A400': 'ffffff',
        'A700': 'ffffff',
        'contrastDefaultColor': 'dark'
    });
    $mdThemingProvider.theme('light')
        .primaryPalette('blue')
        .accentPalette('orange')
        .backgroundPalette("white");
    $mdThemingProvider.alwaysWatchTheme(true);
    $mdThemingProvider.setDefaultTheme('default')
});

