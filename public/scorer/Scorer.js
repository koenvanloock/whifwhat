
var scorer = new Vue({
  el: '#scoreApp',
  data: {
    activeTournament: { tournamentName: 'test'}
  },
  methods:{
    logError: function(errorResponse){ console.log(errorResponse.body)},
    startStream: function(tournamentId){ this.$http.get(window.location.origin + "/startScore/"+tournamentId)},
    stopStream: function(){ this.$http.get(window.location.origin + "/stopScore")}
  },
  mounted: function () {
    this.$http.get(window.location.origin + "/activeTournament").then(function (response) {
      this.activeTournament = response.data;
    }, this.logError);

    var eventSource = new EventSource(window.location.origin + "/scorestream");
    eventSource.addEventListener('message', function (e) {
      var data = JSON.parse(e.data);
     console.log(data);
    }.bind(this), false);
    eventSource.addEventListener('error', this.errorListener, false);
  }
});