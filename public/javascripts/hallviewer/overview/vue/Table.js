Vue.component("vue-pingpong-table", {
  template: '<div class="pingpongTable" v-on:dragover="allowDrop" v-on:drop="dropMatch">'+

  '<div class="tableNr" v-on:click="startUpdating" v-if="!table.hidden">' +
    '<div v-if="notUpdating" style="float: left">{{ tableData.tableName }}</div>' +
    '<div v-else><input v-model="tableData.tableName" style="float: left" v-on:blur="setNotUpdating" /></div>' +
  '</div>'+
  '<div>'+
  '<!-- vertical-->'+
  '<div class="table" v-if="!(table.hidden || table.horizontal)">'+

  '<div class="playerBox-top" v-bind:class="{ playerBox: table.isGreen, playerBoxBlue: !table.isGreen}">' +
  '<span v-if="tableData.siteMatch">{{tableData.siteMatch.playerA.firstname + " " + tableData.siteMatch.playerA.lastname}}</span>'+
  '</div>'+
  '<div class="net"></div>' +
  '<div class="playerBox-bottom" v-bind:class="{ playerBox: table.isGreen, playerBoxBlue: !table.isGreen}">' +
  '<span v-if="tableData.siteMatch">{{tableData.siteMatch.playerB.firstname + " " + tableData.siteMatch.playerB.lastname}}</span>'+
  '</div>'+

  '</div>'+
  '<!-- horizontal-->'+
  '<div class="horizontal-table" v-if="!table.hidden && table.horizontal">'+

  '<div class="playerBox-left" v-bind:class="{ playerBoxHorizontal: table.isGreen, playerBoxHorizontalBlue: !table.isGreen}">' +
  '<span v-if="tableData.siteMatch">{{tableData.siteMatch.playerA.firstname + " " + tableData.siteMatch.playerA.lastname}}</span>'+
  '</div>'+
  '<div class="horizonal-net"></div>'+
  '<div class="playerBox-right" v-bind:class="{ playerBoxHorizontal: table.isGreen, playerBoxHorizontalBlue: !table.isGreen}">' +
  '<span v-if="tableData.siteMatch">{{tableData.siteMatch.playerB.firstname + " " + tableData.siteMatch.playerB.lastname}}</span>'+
  '</div>'+

  '</div>'+
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
    }
  },
  watch: {
     table: function(newTable){
       this.tableData = newTable;
     }
  }
});