<template>
    <div style="padding: 50px;">
        <match-bar
                v-on:toggleMatchPanel="toggleMatchPanel"
                v-on:togglePlayerPanel="togglePlayerPanel"
                :activeTournament="tournament"
                style="height: 80px; width: 100%; border-top: 1px solid white;"></match-bar>
        <div id="matchOverlayPlaceHolder" style="height: 100%; min-height: 500px;" v-bind:class="{matchesPresent: panelOpen}"></div>
        <match-panel :matches="matchList" :series-map="tournament.series" style="z-index: 10000;min-height: 500px; position: absolute; left:0px;top:80px; background: #46ac60" v-bind:class="{matchesPresent: panelOpen}" v-if="panelOpen && isMatchTab"></match-panel>
        <player-panel :players="freePlayers" style="z-index: 10000;min-height: 500px; position: absolute; left:0px;top:80px; background: #46ac60" v-if="panelOpen && !isMatchTab"></player-panel>
        <div style="overflow-x: scroll">
             <div v-for="rowNr in rowList" :key="rowNr" >
                   <table-row :table-row="hall.tables.filter(function(table){ return table.row === rowNr;})" class="width:100vm"></table-row>
                 </div>
            </div>
        </div>
</template>
<script>
    import axios from 'axios'
    import MatchBar from './MatchBar'
    import PlayerPanel from './PlayerPanel'
    import MatchPanel from './MatchPanel'

    export default {
      name: 'hall',
      components: {MatchBar, PlayerPanel, MatchPanel},
      data () {
        return {
          tournament: { series: []},
          freePlayers: [],
          rowList: [],
          hall: {tables: []},
          panelOpen: true,
          isMatchTab: true,
          matchList: []
        }
      },
      mounted () {
        axios.get(window.location.origin + "/activeTournament").then((response) => {
          this.tournament = response.data;
          this.tournament.series.map(function (series) {
            series.checked = false;
          });
          this.matchList = this.tournament.remainingMatches;
          this.freePlayers = this.tournament.freePlayers;
        }, this.logError);

        axios.get(window.location.origin + "/activehall").then(function (hallResponse) {
          this.hall = hallResponse.data;
          this.rowList = Array.apply(null, Array(this.hall.rows)).map(function (row, index) {
            return index + 1
          });
        }, this.logError);

        let eventSource = new EventSource(window.location.origin + "/eventstream");
        eventSource.addEventListener('message', (e) => {
          let data = JSON.parse(e.data);
          if (data.tournament) {
            this.tournament = data.tournament;
            //let activeRoundId = this.matchList && this.matchList[0] ? this.matchList[0].roundId : undefined;
            this.matchList = this.tournament.remainingMatches;
            this.freePlayers = this.tournament.freePlayers;
          }
          if (data.hall) {
            this.hall = this.hall = data.hall;
            this.rowList = Array.apply(null, Array(this.hall.rows)).map(function (row, index) {
              return index + 1
            });
          }
        }, false);
        eventSource.addEventListener('error', this.errorListener, false);
      },
      methods: {
        errorListener (e) {
          if (e.eventPhase === EventSource.CLOSED) {
            console.log("Connection was closed on error: " + e);
          } else {
            console.log("Error occurred while streaming: " + e);
          }
        },
        updateHall (hall) {
          this.hall = hall;
          this.rowList = Array.apply(null, Array(this.hall.rows)).map((row, index) => index + 1);
        },
        logError (errorResponse) {
          console.log(errorResponse.body)
        },
        toggleMatchPanel (isOpen) {
          this.panelOpen = isOpen;
          this.isMatchTab = true;
        }, togglePlayerPanel (isOpen) {
          this.panelOpen = isOpen;
          this.isMatchTab = false;
        },
        canvasWidth () {
          return this.hall.tablesPerRow * 600 + 'px'
        }
      }
    }
</script>