Vue.component("roundListPopup", {
  template: '<div v-on:click="toggleRobinMatches(selectedSeries)" style="cursor:pointer; color:white; border-right: 1px solid white; padding-left: 10px; padding-right: 10px">' +
  '<h2 style="display: inline-flex;">{{selectedRound.roundName ? selectedRound.roundName : activeRoundName}}</h2> ' +
  '<i v-if="!matchPanelOpen" class="fa fa-eye"></i>' +
  '<i v-if="matchPanelOpen" class="fa fa-eye-slash"></i>',
  props: ['selectedSeries', 'selectedRound','matchPanelOpen'],
  data: function() {
    return {activeRoundName: "wedstrijden "}
  },

  methods: {
    toggleRobinMatches: function(selectedSeries){
      this.$emit("toggleMatches", selectedSeries.rounds[0])
    }
  }

});