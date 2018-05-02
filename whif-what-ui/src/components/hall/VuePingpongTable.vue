<template>
    <div style="background: gray">
        <div class="tableNr" v-if="!table.hidden">
            <div v-if="notUpdating" v-on:click="startUpdating" style="float: left; width: 400px; height: 20px">
                {{ tableData.tableName }}
            </div>
            <div v-else><input v-model="tableData.tableName" style="float: left" v-on:blur="setNotUpdating"/></div>
            <button v-if="tableData.pingpongMatch" v-on:click="removeMatch" style="float: right"><i
                    class="fa fa-close"></i></button>
        </div>
        <div v-bind:style="{flexDirection: flexDirection()}">
            <div>
                <div class="pingpongTable" v-on:dragover="allowDrop" v-on:drop="dropMatch">

                    <!-- vertical-->
                    <div class="tableContainer" v-if="!(table.hidden || table.horizontal)">
                        <div class="playerBox-top"
                             v-bind:class="{ playerBox: table.isGreen, playerBoxBlue: !table.isGreen}">
                            <span v-if="tableData.pingpongMatch">
  <p style="font-weight: bold; text-align: center">{{tableData.pingpongMatch.playerA.firstname + " " + tableData.pingpongMatch.playerA.lastname}}</p>
  <input type="number" v-for="(game,index) in tableData.pingpongMatch.games" :key="index"
         v-bind:tabindex="1000* table.row + 10*table.column + (index * 2 + 1)"
         v-model.number="tableData.pingpongMatch.games[index].pointA"
         style="margin-left: 10px; width:40px; font-weight:bold"/>
  </span>
                        </div>
                        <div class="net"></div>
                        <div class="playerBox-bottom"
                             v-bind:class="{ playerBox: table.isGreen, playerBoxBlue: !table.isGreen}">
                            <span v-if="tableData.pingpongMatch">
  <p style="font-weight: bold; text-align: center; margin-top: 0">{{tableData.pingpongMatch.playerB.firstname + " " + tableData.pingpongMatch.playerB.lastname}}</p>
  <input type="number" v-for="(game,index) in tableData.pingpongMatch.games" :key="index"
         v-bind:tabindex="1000* table.row + 10*table.column + (index * 2 + 2)"
         v-model.number="tableData.pingpongMatch.games[index].pointB"
         style="margin-left: 10px; width:40px; font-weight:bold"/>
  </span>
                        </div>
                    </div>
                    <!-- horizontal-->
                    <div class="horizontal-table" v-if="!tableData.hidden && tableData.horizontal">

                        <div class="playerBox-left"
                             v-bind:class="{ playerBoxHorizontal: table.isGreen, playerBoxHorizontalBlue: !table.isGreen}">
                            <span v-if="tableData.pingpongMatch">
  <p style="font-weight: bold; text-align: center;">{{tableData.pingpongMatch.playerA.firstname + " " + tableData.pingpongMatch.playerA.lastname}}</p>
  <input type="number" v-for="(game,index) in tableData.pingpongMatch.games" v-bind:tabindex="(index * 2 + 1)" :key="index"
         v-model.number="tableData.pingpongMatch.games[index].pointA"
         style="margin-left: 10px; width:40px; font-weight:bold"/>
  </span>
                        </div>
                        <div class="horizonal-net"></div>
                        <div class="playerBox-right"
                             v-bind:class="{ playerBoxHorizontal: table.isGreen, playerBoxHorizontalBlue: !table.isGreen}">
                            <span v-if="tableData.pingpongMatch">
  <p style="font-weight: bold; text-align: center">{{tableData.pingpongMatch.playerB.firstname + " " + tableData.pingpongMatch.playerB.lastname}}</p>
  <input type="number" v-for="(game,index) in tableData.pingpongMatch.games" v-bind:tabindex="(index * 2 + 2)" :key="index"
         v-model.number="tableData.pingpongMatch.games[index].pointB"
         style="margin-left: 10px; width:40px; font-weight:bold"/>
  </span>
                        </div>
                    </div>
                </div>
            </div>
            <referee-box :horizontal="tableData.horizontal" :referee="tableData.referee" :hallId="tableData.hallId"
                         :row="tableData.row" :column="tableData.column" :visible="!tableData.hidden"></referee-box>
        </div>
    </div>
</template>

<script>
    import axios from 'axios'
    import RefereeBox from './RefereeBox'

    export default {
      name: 'VuePingpongTable',
      components: {RefereeBox},
      props: ['table'],
      data: function(){
        return {tableData: this.table, notUpdating: true}
      },
      methods: {
        setNotUpdating (){ this.notUpdating = true;},
        startUpdating (){ this.notUpdating = false; },
        dropMatch (ev) {
          ev.preventDefault();
          let matchToSet = JSON.parse(ev.dataTransfer.getData("match"));
          axios.patch("hall/"+ this.table.hallId+'/'+ this.table.row + '/' +  this.table.column, matchToSet).then(function(){});
        },
        allowDrop (event){
          event.preventDefault();
        },
        removeMatch (){
          axios.patch("hallMatch/"+ this.table.hallId+'/'+ this.table.row + '/' +  this.table.column, this.tableData.pingpongMatch).then(function(){}, function(){});
        },
        flexDirection () {
          return this.table.horizontal? "column":"row";
        }
      },
      watch: {
        table: (newTable) => {
          this.tableData = newTable;
        }
      }
    }
</script>

<style>
    .pingpongTable{
        background: grey;
        width: 400px;
        height: 550px;
        padding: 5px;
        display: inline-block;
    }

    .tableContainer{
        height: 500px;
        width: 340px;
        background: gray;
    }

    .net{
        height: 10px;
        background: white;
        width: 100%
    }

    .playerBox{
        height: 245px;
        width: 290px;
        margin-left: 20px;
        margin-right: 20px;
        background: darkgreen;
        border-left: solid white 5px;
        border-right: solid white 5px;
    }

    .playerBoxBlue{
        height: 245px;
        width: 290px;
        margin-left: 20px;
        margin-right: 20px;
        background: darkblue;
        border-left: solid white 5px;
        border-right: solid white 5px;
    }

    .playerBox-top{
        border-top: solid white 5px;
    }

    .playerBox-bottom{
        border-bottom: solid white 5px;
    }

    .playerBox-left{
        border-left: solid white 5px;
    }

    .playerBox-right{
        border-right: solid white 5px;
    }


    .tableRow{
        background: gray;
        margin-bottom:60px;
    }

    .tableNr{
        height: 25px;
        width: auto;
        -webkit-border-radius: 10px;
        -moz-border-radius: 10px;
        border-radius: 10px;
        font-weight: bold;
        color: white;
        background: darkred;
        padding: 10px;
    }

    .horizontal-table{
        width: 500px;
        height: 340px;
    }

    .horizonal-net{
        width: 10px;
        background: white;
        float: left;
        height: 100%
    }
    .playerBoxHorizontal{
        float: left;
        width: 240px;
        height: 290px;
        margin-top: 20px;
        margin-bottom: 20px;
        display: inline-block;
        background: darkgreen;
        border-top: solid white 5px;
        border-bottom: solid white 5px;
    }

    .playerBoxHorizontalBlue{
        float: left;
        width: 240px;
        height: 290px;
        margin-top: 20px;
        margin-bottom: 20px;
        display: inline-block;
        background: darkblue;
        border-top: solid white 5px;
        border-bottom: solid white 5px;
    }

    .matchesPresent{
        min-width: 500px;
    }

    .rotate {
        /* Safari */
        -webkit-transform: rotate(90deg);
        /* Firefox */
        -moz-transform: rotate(90deg);
        /* IE */
        -ms-transform: rotate(90deg);
        /* Opera */
        -o-transform: rotate(90deg);
    }

    .flex-2 {
        /* This will be twice as big as the small item. */
        -webkit-flex: 2 0 0;
        flex: 2 0 0;
    }
    .flex-1 {
        -webkit-flex: 1 0 0;
        flex: 1 0 0;
    }

    .result-table{
        font-weight: bold;
        color: white;
        text-align: center;
        border-collapse: collapse;
        padding: 2px;
    }

    .result-line:nth-child(even), .result-head {
        background-color: rgba(157, 152, 156, 0.34);
    }
</style>