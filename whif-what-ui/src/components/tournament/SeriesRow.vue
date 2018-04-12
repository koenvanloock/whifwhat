<template>
    <tr>
        <td><v-text-field v-model="series.seriesName" label="Reeksnaam"></v-text-field></td>
        <td><input type="color" v-model="series.seriesColor" /></td>
        <td><v-select v-model="series.setTargetScore" :items="targetScores"></v-select></td>
        <td><v-select v-model="series.numberOfSetsToWin" :items="numberOfSetsToWinList"></v-select></td>
        <td><v-checkbox v-model="series.playingWithHandicaps"/></td>
        <td><v-select v-model="series.extraHandicapForRecs" :items="extraHandicapForRecsList"></v-select></td>
        <td><v-checkbox v-model="series.showReferees"/></td>
        <td></td>
    </tr>
</template>

<script>
  import ClickToEdit from "./ClickToEdit";
export default {
  components: {ClickToEdit},
  name: 'SeriesRow',
data: () => {
  return {
    numberOfSetsToWinList: [1, 2, 3, 4, 5],
    extraHandicapForRecsList: [0, 1, 2, 3, 4, 5],
    targetScores: [11, 21]
  }
},
  props: ['series']
}
</script>