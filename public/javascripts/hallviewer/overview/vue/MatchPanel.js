Vue.component("matchPanel", {
  template: '<div style="height: 85%">' +
  '<div style="position: relative">' +
  '<div class="input-container" style="padding: 2px;"> ' +
  '<input v-model="matchQuery" class="form-control" v-bind:class="{hasValue: matchQuery.lengh>0}" />' +
  '<label>Filter wedstrijden</label>' +
  '</div>' +
  '</div>' +
  '<div style="overflow-y: scroll; height: 95%">' +
  '<ul style="list-style-type: none; padding-left: 10px;">' +
  '<li' +
  ' v-if="match.playerA && match.playerB" ' +
  ' v-for="match in filterMatches(matchList, matchQuery)"' +
  ' draggable="true"' +
  ' v-on:dragstart="drag(match, event)">' +
  '<match-list-elem :match="match" style="margin: 10px 10px 0 0;"></match-list-elem>' +
  '</li>' +
  '</ul>' +
  '</div>' +
  '</div>',
  props: ['matches'],
  data: function () {
    return {
      matchQuery: '',
      matchList: this.matches
          .filter( function(viewableMatch){ return !( viewableMatch.isWon || viewableMatch.isOccupied)})
          .map(function(viewableMatch){return viewableMatch.pingpongMatch})
    }
  },
  watch: {
    matches: function (newMatches) {
      this.matchList = newMatches
          .filter( function(viewableMatch){ return !( viewableMatch.isWon || viewableMatch.isOccupied)})
          .map(function(viewableMatch){return viewableMatch.pingpongMatch});
    }
  },

  methods: {
    drag: function (match, event) {
      event.dataTransfer.setData("match", JSON.stringify(match));
    },
    filterMatches: function (matchList, query) {
      return matchList.filter(function (match) {
        return match.playerA && match.playerB && (contains(match.playerA.firstname + ' ' + match.playerA.lastname, query) ||
          contains(match.playerA.lastname + ' ' + match.playerA.firstname,query) ||
          contains(match.playerB.lastname + ' ' + match.playerB.firstname,query) ||
          contains(match.playerB.lastname + ' ' + match.playerB.firstname, query));
      });
    }


  }

});

function contains(stringToSearch, stringToMatch) {
  return stringToSearch.toLowerCase().indexOf(stringToMatch.toLowerCase()) > -1;
}