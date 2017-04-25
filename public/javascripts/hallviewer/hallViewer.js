var hallViewer = angular.module("hallViewer", []).constant('base', {
  url: window.location.origin,
  ws: 'ws://'+ document.location.host
});

hallViewer.directive("pingpongTable", function(){
  return {
    restrict: 'E',
    scope: {table: '='},
    templateUrl: "assets/javascripts/hallViewer/pingpongTable/pingpongTable.html"
  }
});

hallViewer.directive("vueWrapper", function(){
  return {
    restrict: 'E',
    link: function(scope, $element) {
      scope.vm = new Vue({
        el: 'hall-overview'
      })
    }
  }

});