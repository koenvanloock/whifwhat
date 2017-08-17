var bracketRound = Vue.component("bracket-round", {
  template: '<div>' +
  '<bracket-node id="bracket-root" v-if="round.round" :node="round.round.bracketRounds"></bracket-node>' +
  '</div>',
  data: function(){
      return {};
    },
  props: ['round'],
  methods: {}

});