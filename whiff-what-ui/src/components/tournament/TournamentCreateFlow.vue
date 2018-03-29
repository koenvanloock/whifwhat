<template>
  <div>
    <div id="header-block">
      <span class="step" :class="{active: currentStep === 1}">1</span>
      &gt;
      <span class="step" :class="{active: currentStep === 2}">2</span>
      &gt;
      <span class="step" :class="{active: currentStep === 3}">3</span>
      &gt;
      <span class="step" :class="{active: currentStep === 4}">4</span>
    </div>
    <div>
      <div class="card" v-if="currentStep === 1">
        <label>Tornooinaam</label>
        <input type="text"/>
        <label>TornooiDatum</label>
        <input type="date">
      </div>

      <div class="card" v-if="currentStep === 2">
        <table>
          <tr>
            <th class="table-header">Reeksnaam</th>
            <th class="table-header">Reekskleur</th>
            <th class="table-header">set doelscore</th>
            <th class="table-header">aantal te winnen sets</th>
            <th class="table-header">spelen met voorgift</th>
            <th class="table-header">extra voorgift voor recreanten</th>
            <th class="table-header">toon scheidsrechters</th>
            <th class="table-header"></th>
          </tr>
          <tr v-for="series in seriesRounds">
            <td>{{series.seriesname}}</td>
            <td>{{series.seriesColor}}</td>
            <td>{{series.setTargetScore}}</td>
            <td>{{series.numberOfSetsToWin}}</td>
            <td>{{series.playingWithHandicaps}}</td>
            <td>{{series.extraHandicapForRecs}}</td>
            <td>{{series.showReferees}}</td>
            <td></td>
          </tr>
        </table>
      </div>
    </div>
    <div>
      <button v-on:click="previousStep"><i class="fa fa-chevron-left"></i></button>
      <button v-on:click="nextStep"><i class="fa fa-chevron-right"></i></button>
    </div>
  </div>
</template>

<script>
  import axios from 'axios'

export default {
  name: "tournament-create-flow",
  data () {
    return {
      currentStep: 1,
      tournament: { seriesRounds: []}
    }
  },
  methods: {
    createTournament () {
      axios.post("http://localhost:900/tournament")
        .then(this.tournament = tournament)
    },
    updateTournament () {
      axios.post("http://localhost:900/tournament")
        .then(this.tournament = tournament)
    },
    previousStep () {
      if(this.currentStep > 1) {
        this.currentStep -= 1
      }
    },
    nextStep () {
      if(this.currentStep < 4) {
        this.currentStep += 1
      }
    }
  },
  watch: {
    "currentStep": (from, to) => {

    }
  }
}
</script>

<style scoped>
  #header-block {
    padding: 10px;
  }
  .step {
    padding: 4px 8px;
    border-radius: 50%;
    background: #31b531;
    color: white;
    font-weight: bold;
  }

  .active {
    background: #F79F66;
  }
</style>
