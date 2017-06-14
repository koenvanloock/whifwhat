Vue.component("match-list-elem", {
  props: ["match"],
  template: '<div style="background: gray; border-radius: 20px; border: 1px solid white; display:flex; flex-direction: row">' +
  '<div style="width: 200px;display: flex; flex-direction: column; padding: 5px 5px 5px 20px; font-weight: bold; color: white;">' +
  '<span>{{match.playerA.firstname +" " +match.playerA.lastname}}</span>' +
  '<span>{{match.playerB.firstname +" " +match.playerB.lastname}}</span>' +
  '</div>' +
  '<div style="display: flex; flex-direction: column; padding: 5px 5px 5px 20px; font-weight: bold; color: white;">' +
  '<span style="height: 20px"> <span v-if="!match.isHandicapForB">+{{match.handicap}}</span></span>' +
  '<span v-if="match.isHandicapForB">+{{match.handicap}}</span></div>' +
  '</div>'

});