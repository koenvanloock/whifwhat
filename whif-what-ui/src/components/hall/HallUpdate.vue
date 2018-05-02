<template>
    <div style="overflow-x: scroll">
        <div v-for="rowNr in rowList" :key="rowNr" style="min-width:100%;" v-bind:style="{width: canvasWidth()}">
            <table-row :table-row="hall.tables.filter(function(table){ return table.row === rowNr;})"></table-row>
        </div>
    </div>
</template>
<script>
    import TableRow from './TableRow'
    export default {
      name: 'hallUpdate',
      components: {TableRow},
      props: ['hall'],
      data () {
        return {
          rowList: []
        }
      },
      mounted () {
        console.log(this.hall)
        if(this.hall) {
          this.rowList = Array.apply(null, Array(this.hall.rows)).map(function (row, index) {
            return index + 1
          });
        } else {
          this.$router.push('/hall')
        }
      },
      methods: {
        canvasWidth () {
          return this.hall.tablesPerRow * 600 + 'px'
        }
      }
    }
</script>