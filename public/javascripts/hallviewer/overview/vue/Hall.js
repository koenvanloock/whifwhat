Vue.component("hall-overview", {
  template: '<div style="display: flex">' +
  '<div id="matchOverlayPlaceHolder" style="height: 100%; min-height: 500px;" v-bind:class="{matchesPresent: matchPanelOpen}"></div>' +
  '<match-panel :matches="matchList" style="min-height: 500px; position: absolute; left:0px;top:80px; background: #46ac60" v-bind:class="{matchesPresent: matchPanelOpen}" v-if="matchPanelOpen"></match-panel>' +
  '<div>' +
  ' <div v-for="rowNr in rowList" style="min-width:100%;" v-bind:style="{width: canvasWidth()}">' +
  '   <table-row :table-row="hall.tables.filter(function(table){ return table.row === rowNr;})"></table-row>' +
  ' </div>' +
  '</div>' +
  '<match-bar v-on:toggleMatchPanel="togglePanel" :activeTournament="tournament" style="height: 80px; position: absolute; top: 0px; width: 100%; border-top: 1px solid white;"></match-bar>' +
  //'<tournament-bar v-on:toggleMatchPanel="togglePanel" :activeTournament="tournament"  style="position: absolute; bottom: 0; width: 100%; border-top: 1px solid white;"></tournament-bar>' +
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
      this.matchList = this.tournament.remainingMatches
    }.bind(this),this.logError);

    this.$http.get(window.location.origin + "/activehall").then(function (hallResponse) {
      this.hall = hallResponse.data;
      this.rowList = Array.apply(null, Array(this.hall.rows)).map(function(row, index) { return index + 1 });
    }.bind(this), this.logError);

    var eventSource = new EventSource(window.location.origin + "/eventstream");
    eventSource.addEventListener('message', function (e) {
      var data = JSON.parse(e.data);
      if(data.tournament) {
        this.tournament = data.tournament;
        var activeRoundId = this.matchList && this.matchList[0] ? this.matchList[0].roundId : undefined;
                     this.matchList = this.tournament.remainingMatches;
      }
      if(data.hall){
        this.hall = this.hall = data.hall;
        this.rowList = Array.apply(null, Array(this.hall.rows)).map(function(row, index) { return index + 1 });
      }
    }.bind(this), false);
    eventSource.addEventListener('error', this.errorListener, false);
  },
  methods: {
    errorListener: function(e) {
        if (e.eventPhase == EventSource.CLOSED) {
          console.log("Connection was closed on error: " + e);
        } else {
          console.log("Error occurred while streaming: " + e);
        }
      },
    updateHall: function (hall) {
      this.hall = hall;
      this.rowList = Array.apply(null, Array(this.hall.rows)).map(function(row, index) { return index + 1 });
    }.bind(this),
    logError: function(errorResponse){ console.log(errorResponse.body)},
    togglePanel: function(isOpen){
      this.matchPanelOpen = isOpen;
    },
    canvasWidth: function(){ return this.hall.tablesPerRow * 600 + 'px'}

    }
});
