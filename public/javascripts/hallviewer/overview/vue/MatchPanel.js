Vue.component("matchPanel", {
  template: '<div style="height: 85%">' +
  '<div style="position: relative">' +
  '<div>' +
  '<span v-for="series in seriesFilters">' +
  '<label v-bind:style="{background: series.color}">{{series.name}}</label>' +
  '<input type="checkbox" v-model="series.checked">' +
  '</span>' +
  '</div>' +
  '<div class="input-container" style="padding: 2px;"> ' +
  '<input v-model="matchQuery" class="form-control" v-bind:class="{hasValue: matchQuery.lengh>0}" />' +
  '<label>Filter wedstrijden</label>' +
  '</div>' +
  '</div>' +
  '<div style="overflow-y: scroll; height: 95%">' +
  '<ul style="list-style-type: none; padding-left: 10px;">' +
  '<li' +
  ' v-if="match.playerA && match.playerB" ' +
  ' v-for="match in filterMatches(seriesFilters, matchList, matchQuery)"' +
  ' draggable="true"' +
  ' v-on:dragstart="drag(match, event)">' +
  '<match-list-elem :color="getColorOf(match.roundId)" :match="match" style="margin: 10px 10px 0 0;"></match-list-elem>' +
  '</li>' +
  '</ul>' +
  '</div>' +
  '</div>',
  props: ['matches', 'seriesMap'],
  data: function () {
    return {
      matchQuery: '',
      matchList: this.matches
          .filter(function (viewableMatch) {
            return !( viewableMatch.isWon || viewableMatch.isOccupied)
          })
          .map(function (viewableMatch) {
            return viewableMatch.pingpongMatch
          }),
      seriesFilters: []
    }
  },
  watch: {
    matches: function (newMatches) {
      this.matchList = newMatches
          .filter(function (viewableMatch) {
            return !( viewableMatch.isWon || viewableMatch.isOccupied)
          })
          .map(function (viewableMatch) {
            return viewableMatch.pingpongMatch
          });
    },
    seriesMap: function (newMap) {
      this.seriesFilters = newMap.map(function (series) {
        return {id: series.seriesId, color: series.seriesColor, name: series.seriesName, rounds: series.rounds, checked: true}
      })
    }
  },
  mounted: function () {
    this.seriesFilters = this.seriesMap.map(function (series) {
      return {id: series.seriesId, color: series.seriesColor, name: series.seriesName, rounds: series.rounds, checked: true}
    })
  },

  methods: {
    drag: function (match, event) {
      event.dataTransfer.setData("match", JSON.stringify(match));
    },
    filterMatches: function (seriesFilters, matchList, query) {
      return matchList.filter(function (match) {
        return seriesRoundIsVisible(seriesFilters, match.roundId) &&
            match.playerA && match.playerB && (contains(match.playerA.firstname + ' ' + match.playerA.lastname, query) ||
            contains(match.playerA.lastname + ' ' + match.playerA.firstname, query) ||
            contains(match.playerB.lastname + ' ' + match.playerB.firstname, query) ||
            contains(match.playerB.lastname + ' ' + match.playerB.firstname, query));
      });
    },

    getColorOf: function (roundId) {
      console.log("" + this.seriesMap.filter(function (series) {
            return series.rounds.filter(function (seriesRound) {
                  return seriesRound.roundId === roundId
                }).length > 0
          })[0].seriesColor);
      return (this.seriesMap.length > 0)
          ? this.seriesMap
              .filter(function (series) {
                return series.rounds
                        .filter(function (seriesRound) {
                          return seriesRound.roundId === roundId
                        }).length > 0
              })[0].seriesColor
          : 'grey';
    }
  }

});

function contains(stringToSearch, stringToMatch) {
  return stringToSearch.toLowerCase().indexOf(stringToMatch.toLowerCase()) > -1;
}

function seriesRoundIsVisible(seriesFilters, roundId){
  return seriesFilters
      .filter(function(series){  return series.rounds
          .filter( function(seriesRound){ return seriesRound.roundId === roundId}).length > 0 })[0].checked;
}