Vue.component("vue-pingpong-table", {
  template: '<div class="pingpongTable" v-on:dragover="allowDrop" v-on:drop="dropMatch">'+

  '<div class="tableNr" v-if="!table.hidden">' +
    '<div v-if="notUpdating" v-on:click="startUpdating" style="float: left; width: 400px; height: 20px">{{ tableData.tableName }}</div>' +
    '<div v-else><input v-model="tableData.tableName" style="float: left" v-on:blur="setNotUpdating" /></div>' +
    '<button v-if="tableData.siteMatch" v-on:click="removeMatch" style="float: right"> <i class="fa fa-close"></i></button>' +
  '</div>'+
  '<div>'+
  '<!-- vertical-->'+
  '<div class="table" v-if="!(table.hidden || table.horizontal)">'+

  '<div class="playerBox-top" v-bind:class="{ playerBox: table.isGreen, playerBoxBlue: !table.isGreen}">' +
  '<span v-if="tableData.siteMatch">' +
  '{{tableData.siteMatch.playerA.firstname + " " + tableData.siteMatch.playerA.lastname}}' +
  '<input v-for="(game,index) in tableData.siteMatch.games" v-bind:tabindex="(index * 2 + 1)" v-bind="tableData.siteMatch.games[index].pointA" style="margin-left: 10px; width:20px; font-weight:bold"/>' +
  '</span>'+
  '</div>'+
  '<div class="net"></div>' +
  '<div class="playerBox-bottom" v-bind:class="{ playerBox: table.isGreen, playerBoxBlue: !table.isGreen}">' +
  '<span v-if="tableData.siteMatch">' +
  '{{tableData.siteMatch.playerB.firstname + " " + tableData.siteMatch.playerB.lastname}}' +
  '<input v-for="(game,index) in tableData.siteMatch.games" v-bind:tabindex="(index * 2 + 2)" v-bind="tableData.siteMatch.games[index].pointB" style="width:20px; font-weight:bold"/>' +
  '</span>'+
  '</div>'+

  '</div>'+
  '<!-- horizontal-->'+
  '<div class="horizontal-table" v-if="!table.hidden && table.horizontal">'+

  '<div class="playerBox-left" v-bind:class="{ playerBoxHorizontal: table.isGreen, playerBoxHorizontalBlue: !table.isGreen}">' +
  '<span v-if="tableData.siteMatch">' +
  '{{tableData.siteMatch.playerA.firstname + " " + tableData.siteMatch.playerA.lastname}}' +
  '<input v-for="(game,index) in tableData.siteMatch.games" v-bind:tabindex="(index * 2 + 1)" v-bind="tableData.siteMatch.games[index].pointA" style="width:20px; font-weight:bold"/>' +
  '</span>'+
  '</div>'+
  '<div class="horizonal-net"></div>'+
  '<div class="playerBox-right" v-bind:class="{ playerBoxHorizontal: table.isGreen, playerBoxHorizontalBlue: !table.isGreen}">' +
  '<span v-if="tableData.siteMatch">' +
  '{{tableData.siteMatch.playerB.firstname + " " + tableData.siteMatch.playerB.lastname}}' +
  '<input v-for="(game,index) in tableData.siteMatch.games" v-bind:tabindex="(index * 2 + 2)" v-bind="tableData.siteMatch.games[index].pointB" style="width:20px; font-weight:bold"/>' +
  '</span>'+
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
    },
    removeMatch: function(){
      Vue.http.patch("hallMatch/"+ this.table.hallId+'/'+ this.table.row + '/' +  this.table.column, this.tableData.siteMatch).then(function(){}, function(){});
    }
  },
  watch: {
     table: function(newTable){
       this.tableData = newTable;
     }
  }
});