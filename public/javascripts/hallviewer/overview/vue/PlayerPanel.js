Vue.component("playerPanel", {
  template: '<div>' +
  '<div style="position: relative">' +
  '<div class="input-container" style="padding: 2px;"> ' +
  '<input v-model="playerQuery" class="form-control" v-bind:class="{hasValue: playerQuery.lengh>0}" />' +
  '<label>Filter spelers</label>' +
  '</div>' +
  '</div>' +
  '<div style="overflow-y: scroll; height: 95%">' +
  '<ul style="list-style-type: none; padding-left: 10px;">' +
  '<li' +
  ' style="border-radius: 20px; border: 1px solid white; background: gray; margin-bottom: 10px; margin-right: 10px; color: white; font-weight: bold"' +
  ' v-for="player in filterPlayers(playerList, playerQuery)"' +
  ' draggable="true"' +
  ' v-on:dragstart="drag(player, event)">' +
  '<p style="text-align: center"> {{player.firstname + " " + player.lastname}}</p>' +
  '</li>' +
  '</ul>' +
  '</div>' +
  '</div>',
  props: ['players'],
  data: function () {
    return {
      playerQuery: '',
      playerList: this.players
    }
  },
  watch: {
    players: function (newPlayers) {
      this.playerList = newPlayers;
    }
  },

  methods: {
    drag: function (referee, event) {
      event.dataTransfer.setData("referee", JSON.stringify(referee));
    },
    filterPlayers: function (playerList, query) {
      return playerList.filter(function (player) {
        return contains(player.firstname + ' ' + player.lastname, query) ||
          contains(player.lastname + ' ' + player.firstname,query)
      });
    }
  }

});

function contains(stringToSearch, stringToMatch) {
  return stringToSearch.toLowerCase().indexOf(stringToMatch.toLowerCase()) > -1;
}