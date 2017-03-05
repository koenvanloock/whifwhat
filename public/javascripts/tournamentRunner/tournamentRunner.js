var tournamentRunner = angular.module("tournamentRunner", []).constant('base', {
  url: window.location.origin
});


tournamentRunner.directive("editableBracketMatch", function () {
  return {
    restrict: 'E',
    scope: {
      match: '=',
      updatematch: '&',
      seriesRoundId: "="
    },
    templateUrl: "assets/javascripts/tournamentRunner/overview/editableBracketMatch.html"
  }

});

tournamentRunner.directive("editableBracketNode", ['$compile', function ($compile) {

  return {
    restrict: 'E',
    replace: true,
    scope: {
      node: '=',
      updatematch: '&',
      seriesRoundId: '='
    },
    template: '<div layout="row" layout-align="center center">' +
    '<div layout="column">' +
    '<div ng-if="node.left"><editable-bracket-node node="node.left" updatematch="updatematch({roundId: roundId, match: match})" series-round-id="seriesRoundId"></editable-bracket-node></div>' +
    '<div ng-if="node.right"><editable-bracket-node node="node.right" updatematch="updatematch({roundId: roundId, match: match})" series-round-id="seriesRoundId"></editable-bracket-node></div>' +
    '</div>' +
    '<editable-bracket-match match="node.value" updatematch="updatematch({roundId: roundId, match: match})" series-round-id="seriesRoundId"></editable-bracket-match>' +
    '</div>',
    compile: function (element, link) {
      // Normalize the link parameter
      if (angular.isFunction(link)) {
        link = {post: link};
      }

      // Break the recursion loop by removing the contents
      var contents = element.contents().remove();
      var compiledContents;
      return {
        pre: (link && link.pre) ? link.pre : null,
        /**
         * Compiles and re-adds the contents
         */
        post: function (scope, element) {
          // Compile the contents
          if (!compiledContents) {
            compiledContents = $compile(contents);
          }
          // Re-add the compiled contents to the element
          compiledContents(scope, function (clone) {
            element.append(clone);
          });

          // Call the post-linking function, if any
          if (link && link.post) {
            link.post.apply(null, arguments);
          }
        }
      };
    }
  };
}]);

tournamentRunner.directive("keepFocus", ['$timeout', function ($timeout) {
  /*
   Intended use:
   <input keep-focus ng-model='someModel.value'></input>
   */
  return {
    restrict: 'A',
    require: 'ngModel',
    link: function ($scope, $element, attrs, ngModel) {

      ngModel.$parsers.unshift(function (value) {
        $timeout(function () {
          $element[0].focus();
        });
        return value;
      });

    }
  };
}]);

tournamentRunner.directive("editableRobinMatches", function () {
  return {
    restrict: 'E',
    scope: {
      robinMatches: '=',
      updateFn: '&',
      seriesRoundId: '=',
      listView: '='
    },
    templateUrl: "assets/javascripts/tournamentRunner/overview/editableRobinMatches.html"
  }
});

tournamentRunner.directive("editableRobinMatch", function () {
  return {
    restrict: 'E',
    scope: {
      match: '=',
      updateFn: '&',
      seriesRoundId: '=',
      listView: '='
    },
    templateUrl: "assets/javascripts/tournamentRunner/overview/editableRobinMatch.html"
  }
});

tournamentRunner.directive("editableRobinRound", function () {
  return {
    restrict: 'E',
    scope: {
      robinround: '=',
      updateFn: '&',
      seriesRoundId: '='
    },
    templateUrl: "assets/javascripts/tournamentRunner/overview/editableRobinRound.html"
  }
});

tournamentRunner.directive("finalRanking", function () {
  return {
    restrict: 'E',
    scope: {
      finalRanking: '='
    },
    templateUrl: "assets/javascripts/tournamentRunner/overview/finalRanking.html"
  }
});




