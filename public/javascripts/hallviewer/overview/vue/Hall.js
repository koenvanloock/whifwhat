Vue.component("hall-overview", {
  template: '<div style="display: flex">' +
  '<div id="matchOverlayPlaceHolder" style="height: 100%; min-height: 500px;" v-bind:class="{matchesPresent: matchPanelOpen}"></div>' +
  '<match-panel :matches="matchList" style="min-height: 500px; position: absolute; left:0px;top:80px; background: teal" v-bind:class="{matchesPresent: matchPanelOpen}" v-if="matchPanelOpen"></match-panel>' +
  '<div>' +
  ' <div v-for="rowNr in rowList" style="min-width:100%;overflow:scroll;" v-bind:style="{width: canvasWidth()}">' +
  '   <table-row :table-row="hall.tables.filter(function(table){ return table.row === rowNr;})"></table-row>' +
  ' </div>' +
  '</div>' +
  '<tournament-bar v-on:toggleMatchPanel="togglePanel" :activeTournament="tournament"  style="position: absolute; bottom: 0; width: 100%; border-top: 1px solid white;"></tournament-bar>' +
  '</div>',
  data: function () {
    return {
      tournament: {},
      rowList: [],
      hall: {tables: []},
      matchPanelOpen: false,
      matchList: []
    }
  },
  mounted: function () {
    this.$http.get(window.location.origin + "/activeTournament").then(function(response){
      this.tournament = response.data;
    }.bind(this),this.logError);

    this.$http.get(window.location.origin + "/activehall").then(function (hallResponse) {
      this.hall = hallResponse.data;
      this.rowList = Array.apply(null, Array(this.hall.rows)).map(function(row, index) { return index + 1 });
    }.bind(this), this.logError);

    var hallSource = new EventSource(window.location.origin + "/hallstream");
    hallSource.addEventListener('message', function (e) {
      this.hall = JSON.parse(e.data);
      console.log(this.hall);
      this.rowList = Array.apply(null, Array(this.hall.rows)).map(function(row, index) { return index + 1 });
    }.bind(this), false);
    hallSource.addEventListener('error', this.errorListener, false);

    var tournamentSource = new EventSource(window.location.origin + "/tournamentstream");
    tournamentSource.addEventListener('message', function (e) {
      this.tournament = JSON.parse(e.data);
    }.bind(this), false);
    tournamentSource.addEventListener('error', this.errorListener, false);
  },
  methods: {
    errorListener: function(e) {
        if (e.eventPhase == EventSource.CLOSED) {
          console.log("Connection was closed on error: " + e);
        } else {
          console.log("Error occurred while streaming: " + e);
        }
      },
    updateHall: function (e) {
      this.hall = JSON.parse(e.data);
      this.rowList = Array.apply(null, Array(this.hall.rows)).map(function(row, index) { return index + 1 });
    }.bind(this),
    logError: function(errorResponse){ console.log(errorResponse.body)},
    togglePanel: function(isOpen, selectedRound){
      this.matchPanelOpen = isOpen;
      this.matchList = selectedRound.roundMatchesToPlay;
    },
    canvasWidth: function(){ return this.hall.tablesPerRow * 600 + 'px'}

    }
});
