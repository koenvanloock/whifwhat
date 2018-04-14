<template>
    <v-stepper v-model="el">
      <v-stepper-header>
        <v-stepper-step step="1" :complete="el > 1">Maak tornooi</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="2" :complete="el > 2">Stel reeksen in</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="3" :complete="el > 3">Schrijf spelers in</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="4">Configureer de rondes</v-stepper-step>
      </v-stepper-header>
      <v-stepper-items style="height: 800px;">
        <v-stepper-content step="1">
          <v-flex xs8>
            <v-text-field
                    v-model="tournament.tournamentName"
                    name="input-1"
                    label="Tornooinaam"
                    id="testing"
            ></v-text-field>
            <v-date-picker v-model="tournament.tournamentDate"></v-date-picker>
          </v-flex>
          <v-flex xs8>
            <v-checkbox
              v-model="tournament.hasMultipleSeries"
              label="Verschillende reeksen"
            />
            <v-checkbox
                    v-model="tournament.showClub"
                    label="Toon clublabel"
            />
            <v-select
                    label="Maximum aantal reeksdeelnames"
                    v-model="tournament.maximumNumberOfSeriesEntries"
                    :items="[1,2,3,4,5,6,7,8]"
            >
            </v-select>
          </v-flex>
          <v-btn color="primary" @click.native="createOrUpdateTournament">Continue</v-btn>
        </v-stepper-content>
        <v-stepper-content step="2">
          <create-series :tournament="tournament" @seriesAdded="addSeries"></create-series>
          <v-btn color="primary" @click.native="el = 3">Continue</v-btn>
        </v-stepper-content>
        <v-stepper-content step="3">
          <subscribe-players :tournament="tournament" :seriesList="seriesList"></subscribe-players>
          <v-btn color="primary" @click.native="el = 4">Continue</v-btn>
          <v-btn flat>Cancel</v-btn>
        </v-stepper-content>
        <v-stepper-content step="4">
          <round-setup :tournament="tournament" :seriesList="seriesList"></round-setup>
          <v-btn color="primary" @click.native="el = 1">Continue</v-btn>
        </v-stepper-content>

      </v-stepper-items>
    </v-stepper>
</template>

<script>
import axios from 'axios'
import CreateSeries from "./CreateSeries";
import SubscribePlayers from "./SubscribePlayers";
import RoundSetup from "./RoundSetup";
export default {
  components: {
    RoundSetup,
    SubscribePlayers,
    CreateSeries},
  name: 'tournament-create-flow',
  props: ['createFlow'],
  data () {
    return {
      el: 1,
      seriesList: [],
      tournament: {
        id: null,
        tournamentName: null,
        tournamentDate: null,
        showClub: false,
        hasMultipleSeries: false,
        maximumNumberOfSeriesEntries: null
      }
    }
  },
  methods: {
    createOrUpdateTournament () {
      (this.createFlow
       ? axios.post('http://localhost:9000/tournaments', this.tournament)
       : axios.put('http://localhost:9000/tournaments', this.tournament))
        .then(response => {
          this.tournament = response.data
          this.el = 2
        })
    },
    addSeries (series) {
      this.seriesList.push(series)
    }
  }
}
</script>
