var header = Vue.component("tournamentBar",{
  template: '<div style="background: white; height: 80px;position:absolute;top:0px;width: 100%;margin:0;left:0;' +
  '-moz-box-shadow: 4px 4px rgba(0, 0, 0, 0.4);'+
  '-webkit-box-shadow: 0 4px 4px rgba(0, 0, 0, 0.4);'+
  'box-shadow: 0 4px 4px rgba(0, 0, 0, 0.4);' +
  '" v-if="tournament.id">' +
  '<h1 style="text-align: center">{{tournament.tournamentName}}</h1>' +
  '</div>',
  props: ["tournament"]


});