var roundView = Vue.component("round-view",{
  template: '<div>' +
  '<robin-round v-if="isRobinRound" :round="round"></robin-round>' +
  '<bracket-round v-if="isBracketRound" :round="round"></bracket-round>' +
  '</div>',
  props: ['round'],
  methods: {
    isRobinRound: function () {
      return this.round.round && this.round.round.roundType === "R"
    },
    isBracketRound: function () {
      return this.round.round && this.round.round.roundType === "B"
    }
  },
  mounted: function () {

  }
});