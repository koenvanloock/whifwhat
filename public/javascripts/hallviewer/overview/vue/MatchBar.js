Vue.component("matchBar", {
  template: '<div style="background: #46ac60">' +
  '<h2 style="color: white; font-weight: bold; padding: 10px; margin:0;  border-right: 1px solid white" >{{ activeTournament.tournamentName }}</h2>' +
  '<button v-on:click="toggleRound"><i class="fa fa-eye"></i> Toon wedstrijden</button>' +
  '</div>',
  props: ['activeTournament'],
  data: function(){
    return {
      matchPanelOpen: false
    }
  },
  methods: {
    toggleRound: function(){
      this.matchPanelOpen = !this.matchPanelOpen;
      this.$emit("toggleMatchPanel", this.matchPanelOpen);
    }
  }
});