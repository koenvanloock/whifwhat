var completedMatchPanel = Vue.component("completedMatchPanel", {
  template: "<div>" +
  "<ul>" +
  "<li v-for='match in matchlist'><completed-match :match='match'></completed-match></li>" +
  "</ul>" +
  "</div>",
  props: ['matchlist']
});