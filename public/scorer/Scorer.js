
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
  }
});