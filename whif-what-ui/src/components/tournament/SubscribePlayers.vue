<template>
    <div>
        <v-select
                autocomplete
                label="Spelernaam"
                v-model="selectedPlayer"
                :items="filteredPlayers"
                @change="fetchPlayerData"
                chips
        >
            <template slot="selection" slot-scope="data">
                <v-chip
                        @input="data.parent.selectItem(data.item)"
                        :selected="data.selected"
                        class="chip--select-multi"
                        :key="data.id"
                >
                    <v-avatar>
                        <img :src="data.item.imagepath">
                    </v-avatar>
                    {{ data.item.firstname + ' ' + data.item.lastname }}
                </v-chip>
            </template>
            <template slot="item" slot-scope="data" @click="selected = true">
                <template v-if="typeof data.item !== 'object'">
                    <v-list-tile-content v-text="data.item"></v-list-tile-content>
                </template>
                <template v-else>
                    <v-list-tile-avatar>
                        <img :src="data.item.imagepath">
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>{{data.item.firstname + ' ' + data.item.lastname}}</v-list-tile-title>
                        <v-list-tile-sub-title v-html="data.item.rank.name"></v-list-tile-sub-title>
                    </v-list-tile-content>
                </template>
            </template>
        </v-select>

        <v-checkbox v-for="(series, index) in seriesList" :key="series.id" :label="series.seriesName" v-model="subscribedSeries[index]"></v-checkbox>

        <v-btn @click="subscribePlayer"><v-icon>person_add</v-icon> schrijf speler in</v-btn>

    </div>
</template>

<script>
    import axios from 'axios'
    export default {
      components: {},
      name: 'SubscribePlayers',
      props: ['tournament', 'seriesList'],
      data () {
        return {
          filteredPlayers: [],
          selectedPlayer: {},
          subscribedSeries: [],
          players: []
        }
      },
      mounted () {
        axios.get('http://localhost:9000/players').then(response => {
          console.log(response)
          this.players = response.data
          this.filteredPlayers = response.data
        })
      },
      methods: {
        findPlayers (text) {
          console.log(text)
          this.filteredPlayers = this.players.filter(player => player.firstname.indexOf(text > -1))
        },
        subscribePlayer () {
            console.log(this.selectedPlayer)
          let subscriptions = subscriptionIds(this.seriesList, this.subscribedSeries)
          console.log('subscriptionIdsToSend', subscriptions)
          axios.post('http://localhost:9000/seriesplayers', {player: this.selectedPlayer, subscriptions: subscriptions, seriesList: this.seriesList.map(series => series.id)})
        },
        fetchPlayerData (newPlayer) {
          axios.get("http://localhost:9000/seriesofplayer/" + newPlayer.id + '/' + this.tournament.id).then(response => {
            this.subscribedSeries = []
            console.log('subscriptions', response.data)
            this.seriesList.map(series => this.subscribedSeries.push(compareById(series, response.data)))
            console.log(this.subscribedSeries)
          })
        }
      }
    }

    function compareById(series, list){
      return list.reduce((acc, elem) => acc || elem.id === series.id, false)
    }
    function subscriptionIds(list, booleans) {
      return list
        .filter((series, index) => booleans[index])
        .map(series => series.id)
    }
</script>