var completedMatch = Vue.component('completedMatch', {
  template: '<div>' +
  '<div>{{match.playerA.firstname+ " "+ match.playerA.lastname}}</div>' +
  '<div>{{match.playerB.firstname+ " "+ match.playerB.lastname}}</div>' +
  '</div>',
  props: ['match']
});