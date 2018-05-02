<template>
    <div style="height: 85%">
        <div style="position: relative">
            <div>
                <span v-for="series in seriesFilters" :key="series.id">
  <label v-bind:style="{background: series.color}">{{series.name}}</label>
  <input type="checkbox" v-model="series.checked">
  </span>
            </div>
            <div class="input-container" style="padding: 2px;"> 
                <input v-model="matchQuery" class="form-control" v-bind:class="{hasValue: matchQuery.lengh>0}"/>
                <label>Filter wedstrijden</label>
            </div>
        </div>
        <div style="overflow-y: scroll; height: 95%">
            <ul style="list-style-type: none; padding-left: 10px;">
                <li
                v-if="match.playerA && match.playerB"
                v-for="match in filterMatches(seriesFilters, matchList, matchQuery)" :key="match.id"
                draggable="true"
                v-on:dragstart="drag" v-on:mousedown="selectMatch(match)">
                <match-list-elem :color="getColorOf(match.roundId)" :match="match"
                                 style="margin: 10px 10px 0 0;"></match-list-elem>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
  export default {
    name: 'MatchPanel',
    props: ['matches', 'seriesMap'],
    data () {
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
          return {
            id: series.seriesId,
            color: series.seriesColor,
            name: series.seriesName,
            rounds: series.rounds,
            checked: true
          }
        })
      }
    },
    mounted: function () {
      this.seriesFilters = this.seriesMap.map(function (series) {
        return {
          id: series.seriesId,
          color: series.seriesColor,
          name: series.seriesName,
          rounds: series.rounds,
          checked: true,
          selectedMatch: {}
        }
      })
    },

    methods: {
      selectMatch: function (match) {
        this.selectedMatch = match;
      },
      drag: function (event) {
        event.dataTransfer.setData("match", JSON.stringify(this.selectedMatch));
      },
      filterMatches: function (seriesFilters, matchList, query) {
        return matchList.filter(function (match) {
          return this.seriesRoundIsVisible(seriesFilters, match.roundId) &&
            match.playerA && match.playerB && (this.contains(match.playerA.firstname + ' ' + match.playerA.lastname, query) ||
            this.contains(match.playerA.lastname + ' ' + match.playerA.firstname, query) ||
            this.contains(match.playerB.lastname + ' ' + match.playerB.firstname, query) ||
            this.contains(match.playerB.lastname + ' ' + match.playerB.firstname, query));
        });
      },

      getColorOf: function (roundId) {
        return (this.seriesMap.length > 0)
          ? this.seriesMap
            .filter(function (series) {
              return series.rounds
                  .filter(function (seriesRound) {
                    return seriesRound.roundId === roundId
                  }).length > 0
            })[0].seriesColor
          : 'grey';
      },
      contains(stringToSearch, stringToMatch) {
        return stringToSearch.toLowerCase().indexOf(stringToMatch.toLowerCase()) > -1;
      },
      seriesRoundIsVisible (seriesFilters, roundId){
        let existingRounds = seriesFilters
          .filter(function (series) {
            return series.rounds
                .filter(function (seriesRound) {
                  return seriesRound.roundId === roundId
                }).length > 0
          });
        return existingRounds.length > 0 && existingRounds[0].checked;
      }
    }
  }
</script>