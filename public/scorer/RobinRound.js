var robinRound = Vue.component("robin-round",{
  template: '<div v-if="round.round">' +
  '<div v-for="robin in round.round.robinRounds">' +
  '<ul>' +
  '<li v-for="player in robin.robinPlayers">{{player.player.firstname+" "+player.player.lastname}}</li>' +
  '</ul>' +
  '</div>'+
  '</div>',
  props: ['round']
});