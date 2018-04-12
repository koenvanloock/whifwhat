<template>
   <div>
    <v-btn @click="addEmptySeries"><v-icon>playlist_add</v-icon></v-btn>
    <v-data-table
            :headers="headers"
            :items="seriesList">
        <template slot="items" slot-scope="props">
            <series-row :series="props.item"/>
        </template>
    </v-data-table>
    </div>
</template>

<script>
import axios from 'axios'
import SeriesRow from './SeriesRow'
export default {
  name: 'CreateSeries',
  components: {SeriesRow},
  props: ['tournament'],
  data () {
    return {
      headers: [
        {
          text: 'Reeksnaam',
          align: 'left',
          sortable: true,
          value: 'seriesName'
        },
        { text: 'Kleur', value: 'seriesColor' },
        { text: 'DoelScore', value: 'setTargetScore' },
        { text: 'te winnen sets', value: 'numberOfSetsToWin' },
        { text: 'Met voorgift', value: 'playingWithHandicaps' },
        { text: 'Extra vg REC', value: 'extraHandicapForRecs' },
        { text: 'toon scheids', value: 'showReferee' }
      ],
      seriesList: []
    }
  },
  methods: {
    addEmptySeries () {
      let newSeries = {
        "id": "",
        "seriesName": 'a',
        "seriesColor": "#ffffff",
        "setTargetScore": 21,
        "numberOfSetsToWin": 2,
        "playingWithHandicaps": false,
        "extraHandicapForRecs": 0,
        "showReferees": false,
        "currentRoundNr": 1,
        "tournamentId": this.tournament.id
      }
      axios.post('http://localhost:9000/series', newSeries).then(response => {
        this.seriesList.push(response.data)
        this.$emit('seriesAdded', response.data)
      })
    }
  }
}
</script>

<style>

</style>