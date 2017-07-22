Vue.component("matchBar", {
  template: '<div style="background: #46ac60;">' +
  '<h2 style="color: white; font-weight: bold; padding: 10px; margin:0;  border-right: 1px solid white" >{{ activeTournament.tournamentName }}</h2>' +
  '<button v-on:click="toggleMatches"><i class="fa fa-eye"></i> Toon wedstrijden</button>' +
  '<button v-on:click="togglePlayers"><i class="fa fa-eye"></i> Toon spelers</button>' +
  '</div>',
  props: ['activeTournament'],
  data: function(){
    return {
      matchPanelOpen: false,
      isMatches: false
    }
  },
  methods: {
    toggleMatches: function(){
      this.matchPanelOpen = this.isMatches? !this.matchPanelOpen : true;
      this.isMatches = true;
      this.$emit("toggleMatchPanel", this.matchPanelOpen);
    },
    togglePlayers: function(){
      this.matchPanelOpen = this.isMatches? true : !this.matchPanelOpen;
      this.isMatches = false;
      this.$emit("togglePlayerPanel", this.matchPanelOpen);
    }
  }
});