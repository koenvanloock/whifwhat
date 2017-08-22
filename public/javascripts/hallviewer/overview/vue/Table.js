Vue.component("vue-pingpong-table", {
  template: '<div style="background: gray">' +
  '<div class="tableNr" v-if="!table.hidden">' +
  '<div v-if="notUpdating" v-on:click="startUpdating" style="float: left; width: 400px; height: 20px">{{ tableData.tableName }}</div>' +
    '<div v-else><input v-model="tableData.tableName" style="float: left" v-on:blur="setNotUpdating" /></div>' +
  '<button v-if="tableData.pingpongMatch" v-on:click="removeMatch" style="float: right"> <i class="fa fa-close"></i></button>' +
  '</div>'+
  '<div style="display: flex" v-bind:style="{flexDirection: flexDirection()}" >' +
  '<div style="display: flex;flex-direction: row">' +
  '<div class="pingpongTable" v-on:dragover="allowDrop" v-on:drop="dropMatch">'+

  '<!-- vertical-->'+
  '<div class="table" v-if="!(table.hidden || table.horizontal)">'+
  '<div class="playerBox-top" v-bind:class="{ playerBox: table.isGreen, playerBoxBlue: !table.isGreen}">' +
  '<span v-if="tableData.pingpongMatch">' +
  '<p style="font-weight: bold; text-align: center">{{tableData.pingpongMatch.playerA.firstname + " " + tableData.pingpongMatch.playerA.lastname}}</p>' +
  '<input type="number" v-for="(game,index) in tableData.pingpongMatch.games" v-bind:tabindex="1000* table.row + 10*table.column + (index * 2 + 1)" v-model.number="tableData.pingpongMatch.games[index].pointA" style="margin-left: 10px; width:40px; font-weight:bold"/>' +
  '</span>'+
  '</div>'+
  '<div class="net"></div>' +
  '<div class="playerBox-bottom" v-bind:class="{ playerBox: table.isGreen, playerBoxBlue: !table.isGreen}">' +
  '<span v-if="tableData.pingpongMatch">' +
  '<p style="font-weight: bold; text-align: center; margin-top: 0">{{tableData.pingpongMatch.playerB.firstname + " " + tableData.pingpongMatch.playerB.lastname}}</p>' +
  '<input type="number" v-for="(game,index) in tableData.pingpongMatch.games" v-bind:tabindex="1000* table.row + 10*table.column + (index * 2 + 2)" v-model.number="tableData.pingpongMatch.games[index].pointB" style="margin-left: 10px; width:40px; font-weight:bold"/>' +
  '</span>'+
  '</div>'+
  '</div>'+
  '<!-- horizontal-->'+
  '<div class="horizontal-table" v-if="!tableData.hidden && tableData.horizontal">'+

  '<div class="playerBox-left" v-bind:class="{ playerBoxHorizontal: table.isGreen, playerBoxHorizontalBlue: !table.isGreen}">' +
  '<span v-if="tableData.pingpongMatch">' +
  '<p style="font-weight: bold; text-align: center;">{{tableData.pingpongMatch.playerA.firstname + " " + tableData.pingpongMatch.playerA.lastname}}</p>' +
  '<input type="number" v-for="(game,index) in tableData.pingpongMatch.games" v-bind:tabindex="(index * 2 + 1)" v-model.number="tableData.pingpongMatch.games[index].pointA" style="margin-left: 10px; width:40px; font-weight:bold"/>' +
  '</span>'+
  '</div>'+
  '<div class="horizonal-net"></div>'+
  '<div class="playerBox-right" v-bind:class="{ playerBoxHorizontal: table.isGreen, playerBoxHorizontalBlue: !table.isGreen}">' +
  '<span v-if="tableData.pingpongMatch">' +
  '<p style="font-weight: bold; text-align: center">{{tableData.pingpongMatch.playerB.firstname + " " + tableData.pingpongMatch.playerB.lastname}}</p>' +
  '<input type="number" v-for="(game,index) in tableData.pingpongMatch.games" v-bind:tabindex="(index * 2 + 2)" v-model.number="tableData.pingpongMatch.games[index].pointB" style="margin-left: 10px; width:40px; font-weight:bold"/>' +
  '</span>'+
  '</div>'+
  '</div>'+
  '</div>'+
  '</div>'+
  '<referee-box :horizontal="tableData.horizontal" :referee="tableData.referee" :hallId="tableData.hallId" :row="tableData.row" :column="tableData.column" :visible="!tableData.hidden"></referee-box>'+
  '</div>'+
  '</div>',
  props: ['table'],
  data: function(){
    return {tableData: this.table, notUpdating: true}
  },
  methods: {
    setNotUpdating: function(){ this.notUpdating = true;},
    startUpdating: function(){ this.notUpdating = false; },
    dropMatch: function(ev) {
      ev.preventDefault();
      var matchToSet = JSON.parse(ev.dataTransfer.getData("match"));
      Vue.http.patch("hall/"+ this.table.hallId+'/'+ this.table.row + '/' +  this.table.column, matchToSet).then(function(){});
    },
    allowDrop: function(event){
      event.preventDefault();
    },
    removeMatch: function(){
      Vue.http.patch("hallMatch/"+ this.table.hallId+'/'+ this.table.row + '/' +  this.table.column, this.tableData.pingpongMatch).then(function(){}, function(){});
    },
    flexDirection: function () {
      return this.table.horizontal? "column":"row";
    }
  },
  watch: {
     table: function(newTable){
       this.tableData = newTable;
     }
  }
});