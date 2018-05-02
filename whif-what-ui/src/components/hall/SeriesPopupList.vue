<template>
    <div>
        <div v-if="!open" v-on:click="openMenu()"><h2 style="display: inline-flex;">
            {{selectedSeries.seriesName ? selectedSeries.seriesName : activeSeriesName}}</h2> <i
                class="fa fa-arrow-circle-up"></i></div>
        <div v-if="open">
            <ul style="float: left; overflow: inherit">
                <li v-for="series in seriesList" v-on:click="selectSeries(series)">{{ series.seriesName }}</li>
            </ul>
        </div>
    </div>
</template>

<script>
  export default {
    name: 'SeriesPopupList',
    props: ['seriesList', 'selectedSeries'],
    data () {
      return {activeSeriesName: 'Kies een reeks', open: false}
    },

    methods: {
      openMenu (){
        this.open = true;
      },
      selectSeries (series){
        this.open = false;
        this.$emit('seriesSelection', series)
      }
    }
  }
</script>