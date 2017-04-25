var dynamicHall = {
  hallName: "testhal",
  tables: [{
    row: 1,
    column: 1
  }]
};

Vue.component("hall-overview", {
  template: '<div><h1 style="color:white">Huidige zaal: <span id="hallName">{{ hall.hallName }} {{hall.tables.length}}</span></h1>' +
  '<div v-for="rowNr in rowList">' +
  ' <table-row :table-row="hall.tables.filter(function(table){ return table.row === rowNr;})"></table-row>' +
  '</div>' +
  '</div>',
  data: function () {
    return {
      rowList: [],
      hall: dynamicHall
    }
  },
  mounted: function () {
    var source = new EventSource(window.location.origin + "/stream");
    source.addEventListener('message', function (e) {
      this.hall = JSON.parse(e.data);
      this.rowList = Array.apply(null, Array(this.hall.rows)).map(function(row, index) { return index + 1 });
    }.bind(this), false);

    source.addEventListener('error', function (e) {
      if (e.eventPhase == EventSource.CLOSED) {
        console.log("Connection was closed on error: " + e);
      } else {
        console.log("Error occurred while streaming: " + e);
      }
    }, false);
  }
});

