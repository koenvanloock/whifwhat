Vue.component("hall-overview", {
  template: '<div><h1 style="color:white">Huidige zaal: <span id="hallName">{{ hall.hallName }} {{hall.tables.length}}</span></h1>' +
  '<div v-for="rowNr in rowList">' +
  ' <table-row :table-row="hall.tables.filter(function(table){ return table.row === rowNr;})"></table-row>' +
  '<div v-if="tournament.tournamentName"  style="position: absolute; bottom: 0; background: darkslategrey; opacity: 0.7">{{ tournament.tournamentName }}</div>' +
  '</div>' +
  '</div>',
  data: function () {
    return {
      tournament: {},
      rowList: [],
      hall: {tables: []}
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
    hallSource.addEventListener('message', this.updateHall, false);
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
    logError: function(errorResponse){ console.log(errorResponse.body)}

    }
});

