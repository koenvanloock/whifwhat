<template>
    <div v-bind:style="{background: selectedSeries.seriesColor}">
        <div style="display: flex;">
            <h1 style="color: white; font-weight: bold; padding: 10px; margin:0;  border-right: 1px solid white">
                {{ activeTournament.tournamentName }}</h1>
            <div>
                <div>
                    <series-list-popup :selectedSeries="selectedSeries" :seriesList="activeTournament.series"
                                       v-on:seriesSelection="selectSeries"
                                       style="color: white; padding-left: 10px; padding-right: 10px; border-right: 1px solid white"></series-list-popup>
                    +
                </div>
            </div>
            <round-list-popup v-if="selectedSeries.seriesName" :selectedSeries="selectedSeries"
                              :matchPanelOpen="matchPanelOpen" :selectedRound="selectedRound"
                              v-on:toggleMatches="toggleRound"></round-list-popup>
        </div>
    </div>
</template>

<script>
  export default {
    name: 'TournamentBar',

    props: ['activeTournament'],
    data (){
      return {
        selectedSeries: {seriesColor: 'darkSlateGrey'},
        selectedRound: {},
        matchPanelOpen: false
      }
    },

    methods: {
      selectSeries (series){
        this.selectedSeries = series;
      },
      toggleRound (round){
        this.selectedRound = round;
        this.$emit("toggleMatchPanel", this.matchPanelOpen, round);
        this.matchPanelOpen = !this.matchPanelOpen;
      }
    }
  }
</script>