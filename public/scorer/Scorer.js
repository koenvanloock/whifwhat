
var scorer = new Vue({
  el: '#scoreApp',
  template: '<div v-if="activeTournament.id">' +
  '<tournament-bar :tournament="activeTournament" :roundname="roundToShow.name" :color="roundToShow.color" ></tournament-bar>'+
  '<div style="margin-top: 200px; display: flex"> '+
  '<round-view :round="roundToShow"></round-view>'+
    '<completed-match-panel :matchlist="matchList"></completed-match-panel>' +
  '</div>'+
  '<div style="">'+
  '<button v-on:click="startStream(activeTournament.id)">Start scores volgen</button>'+
'<button v-on:click="stopStream">Stop scores volgen</button>'+
'</div>'+
'</div>'+
'<div v-else>'+
'<h3>Er is geen actief tornooi.</h3>'+
'</div>',
  data: {
    activeTournament: { tournamentName: 'test'},
    roundToShow: { name: '', color: 'white'},
    matchList: []
  },
  methods:{
    logError: function(errorResponse){ console.log(errorResponse.body)},
    startStream: function(tournamentId){ this.$http.get(window.location.origin + "/startScore/"+tournamentId)},
    stopStream: function(){ this.$http.get(window.location.origin + "/stopScore")},
    errorListener: function(e) {
      if (e.eventPhase == EventSource.CLOSED) {
        console.log("Connection was closed on error: " + e);
      } else {
        console.log("Error occurred while streaming: " + e);
      }
    },
    addMatchToList: function (matchToAdd) {
      if(this.matchList.length < 10){
        this.matchList.push(matchToAdd)
      } else {
        for(var i = 1; i< 11; i++){
          Vue.set(this.matchList,i-1,this.matchList[i]);
        }
        Vue.set(this.matchList,9,matchToAdd);
      }
    }
  },
  mounted: function () {
    this.$http.get(window.location.origin + "/activeTournament").then(function (response) {
      this.activeTournament = response.data;
    }, this.logError);

    var eventSource = new EventSource(window.location.origin + "/scorestream");
    eventSource.addEventListener('message', function (e) {
      var data = JSON.parse(e.data);
     console.log(data);
      if(data.roundToShow) {
        this.roundToShow = data.roundToShow;
      } else if(data.completedMatch){
        this.addMatchToList(data.completedMatch);
        console.log(this.matchList)
      }
    }.bind(this), false);

    var tournamentSource = new EventSource(window.location.origin + "/eventstream");
    tournamentSource.addEventListener('message', function (e) {
      var data = JSON.parse(e.data);
      if(data.tournament) {
        this.activeTournament = data.tournament;
      }
    }.bind(this), false);

    eventSource.addEventListener('error', this.errorListener, false);
    tournamentSource.addEventListener('error', this.errorListener, false);
  },
  watch: {
    roundToShow: function (newRound) {
      this.roundToShow = newRound;
    }
  }
});