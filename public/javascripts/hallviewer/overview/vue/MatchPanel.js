Vue.component("matchPanel", {
  template: '<div>' +
  '<ul>' +
  '<li ' +
  ' v-if="match.playerA && match.playerB" ' +
  ' v-for="match in matchList"' +
  ' draggable="true"' +
  ' v-on:dragstart="drag(match, event)">{{match.playerA.firstname + " " + match.playerA.lastname }} vs {{match.playerB.firstname + " " + match.playerB.lastname }}</li>' +
  '</ul>' +
  '</div>',
  props: ['matches'],
  data: function(){
    return {
      matchList: this.matches
    }
  },
  watch: {
    matches: function(newMatches){
      this.matchList = newMatches;
    }
  },

  methods: {
    drag: function(match, event) {
      event.dataTransfer.setData("match", JSON.stringify(match));
    }
  }

});