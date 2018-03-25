<template>
  <div class="card standard-table">
    <table>
      <tr>
        <th>Voornaam</th>
        <th>Achternaam</th>
        <th>Klassement</th>
        <th>Acties</th>
      </tr>
      <player-line v-for="player in players" v-bind:key="player.id" :player="player" :ranks="ranks"/>
    </table>
    <pagination :current-page="currentPage" :number-of-items="numberOfItems" :page-size="pageSize"/>
  </div>
</template>

<script>
import axios from 'axios'
import PlayerLine from './PlayerLine'
import Pagination from '../abstract/Pagination'

export default {
  components: {
    Pagination,
    PlayerLine
  },
  'name': 'Players',
  data: () => {
    return {
      players: [],
      ranks: [],
      currentPage: 1,
      numberOfItems: 27,
      pageSize: 5
    }
  },
  mounted () {
    axios.get('http://localhost:9000/players', {
      crossDomain: true
    }).then(response => {
      this.players = response.data
    })
    axios.get('http://localhost:9000/ranks', {
      crossDomain: true
    }).then(response => {
      this.ranks = response.data
    })
  }
}
</script>
<style>
  .full-width {
    width: 100%
  }
</style>
