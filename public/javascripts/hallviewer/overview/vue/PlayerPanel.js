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
  ' v-on:dragstart="drag" v-on:mousedown="selectPlayerForDrag(player)">' +
  '<div style="text-align: center">' +
  '<p> {{player.player.firstname + " " + player.player.lastname}}</p>' +
  '<div>' +
  '<span><i class="fa fa-balance-scale" aria-hidden="true"></i> {{player.refereeInfo.numberOfRefs}}</span>' +
  '<span style="min-width: 20px; height: 10px;">&nbsp;</span>' +
  '<span><i class="fa fa-bolt" aria-hidden="true"></i> {{player.refereeInfo.matchesToPlay}}</span>' +
  '</div>' +
  '</div>' +
  '</li>' +
  '</ul>' +
  '</div>' +
  '</div>',
  props: ['players'],
  data: function () {
    return {
      playerQuery: '',
      playerList: this.players.filter(function(viewablePlayer){ return !viewablePlayer.occupied })
    }
  },
  watch: {
    players: function (newPlayers) {
      this.playerList = newPlayers.filter(function(viewablePlayer){ return !viewablePlayer.occupied });
    }
  },

  methods: {
    selectPlayerForDrag: function(player) {
      selectedPlayer = player.player},
    drag: function (event) {
      event.dataTransfer.setData("referee", JSON.stringify(selectedPlayer));
    },
    filterPlayers: function (playerList, query) {
      return playerList.filter(function (player) {
        return contains(player.player.firstname + ' ' + player.player.lastname, query) ||
          contains(player.player.lastname + ' ' + player.player.firstname,query)
      });
    }
  }

});

function contains(stringToSearch, stringToMatch) {
  return stringToSearch.toLowerCase().indexOf(stringToMatch.toLowerCase()) > -1;
}

var selectedPlayer = {};