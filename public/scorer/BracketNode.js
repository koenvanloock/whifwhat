var bracketNode = Vue.component("bracket-node", {
  template: '<div style="display: flex; justify-content: center">' +
    '<div style="display: flex; flex-direction: column">'+
  '<bracket-node v-if="node.left" :node="node.left"/>' +
  '<bracket-node v-if="node.right" :node="node.right"/>' +
  '</div>' +
  '<div style="display: flex; flex-direction: column; justify-content: center">' +
  '<bracket-leaf :pingpongmatch="node.value"></bracket-leaf>' +
  '</div></div>',
  data: function(){
    return {};
  },
  props: ['node'],
  methods: {}

});