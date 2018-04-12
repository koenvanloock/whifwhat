<template>
    <div>
    <div v-for="seriesEntry in roundsBySeriesId" :key="seriesEntry.id">
        <h3>{{seriesEntry.seriesName}}</h3>
        <v-btn @click="addRound(seriesEntry.id)"><v-icon>add</v-icon>nieuwe ronde</v-btn>
        <v-btn @click="removeLastRound(seriesEntry.id)"><v-icon>remove</v-icon>verwijder laatste ronde</v-btn>
        <v-flex>
        <div v-for="round in seriesEntry.rounds" :key="round.id" style="display: flex">
            <div>{{round.roundNr}}</div>
            <div><v-select v-model="round.roundType" item-value="value" item-text="name" label="Type" :items="roundTypes" ></v-select></div>
            <div v-if="round.roundType === 'B'"><v-select v-model="round.numberOfBracketRounds" item-value="value" item-text="name" label="Aantal tabelronden" :items="bracketRounds" ></v-select></div>
            <div v-if="round.roundType === 'R'"><v-select v-model="round.numberOfRobinGroups" item-value="value" item-text="name" label="Aantal poules" :items="numberOfRobins" ></v-select></div>

        </div>
        </v-flex>
    </div>
    </div>
</template>

<script>
  import axios from 'axios'
  export default {
    components: {},
    name: 'RoundSetup',
    data () {
      return {
        roundsBySeriesId: [],
        roundTypes: [
          {name: "Pouleronde", value: 'R'},
          {name: "Tabel", value: 'B'}],
        bracketRounds: [
          {
            name: 'achtste finales',
            value: 4
        },{
            name: 'kwartfinales',
            value: 3
        },{
            name: 'halve finales',
            value: 2
          },{
            name: 'finale',
            value: 1
          }
      ],
        numberOfRobins: [
          {
            name: '4 poules',
            value: 4
          },{
            name: '3 poules',
            value: 3
          },{
            name: '2 poules',
            value: 2
          },{
            name: '1 poule',
            value: 1
          }
        ]
      }
    },
    props: ['tournament', 'seriesList'],
    methods: {
      addRound (seriesId) {
        axios.post('http://localhost:9000/seriesrounds/' + seriesId).then(response => findElementById(this.roundsBySeriesId, seriesId).rounds.push(response.data))
      },
      removeLastRound (seriesId) {
          let roundList = findElementById(this.roundsBySeriesId, seriesId).rounds
            console.log(roundList)
            axios.delete('http://localhost:9000/seriesrounds/' + roundList[roundList.length - 1].id).then(() => roundList.splice(roundList.length - 1, 1))
        }
    },
    mounted () {
      console.log(this.seriesList)
      this.seriesList.map(series => this.roundsBySeriesId.push({ id: series.id, seriesName: series.seriesName, rounds: []}))
    }

  }

function findElementById(list, idToFind) {
   return list.find(item => item.id === idToFind)
}
</script>