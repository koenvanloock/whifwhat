var header = Vue.component("tournamentBar", {
  template: '<div v-bind:style="{background: resolvedColor()}" style="height: 80px;position:absolute;top:0px;width: 100%;margin:0;left:0;' +
  '-moz-box-shadow: 4px 4px rgba(0, 0, 0, 0.4);' +
  '-webkit-box-shadow: 0 4px 4px rgba(0, 0, 0, 0.4);' +
  'box-shadow: 0 4px 4px rgba(0, 0, 0, 0.4);' +
  '" v-if="tournament.id">' +
  '<h1 style="text-align: center">{{showRoundName()}}</h1>' +
  '</div>',
  props: ["tournament", 'roundname', 'color'],
  methods: {
    resolvedColor: function () {
      return this.color ? this.color : 'white';
    },
    showRoundName: function () {
      if(this.tournament && this.roundName) {
        return (this.tournament.tournamentName === this.roundName) ? this.roundName : this.tournament.tournamentName + " " + this.roundName;
      } else {
        return this.tournament? this.tournament.tournamentName : "";
      }
    }
  }


});