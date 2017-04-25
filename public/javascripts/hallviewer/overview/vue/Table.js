Vue.component("vue-pingpong-table", {
  template: '<div class="pingpongTable">'+

  '<div class="tableNr" v-on:click="startUpdating" v-if="!table.hidden">' +
    '<div v-if="notUpdating" style="float: left">{{ tableData.tableName }}</div>' +
    '<div v-else><input v-model="tableData.tableName" style="float: left" v-on:blur="setNotUpdating" /></div>' +
  '</div>'+
  '<div>'+
  '<!-- vertical-->'+
  '<div class="table" v-if="!(table.hidden || table.horizontal)">'+

  '<div class="playerBox playerBox-top">'+
  '</div>'+
  '<div class="net"></div>' +
  '<div class="playerBox playerBox-bottom"></div>'+

  '</div>'+
  '<!-- horizontal-->'+
  '<div class="horizontal-table" v-if="!table.hidden && table.horizontal">'+

  '<div class="playerBox-horizontal playerBox-left"></div>'+
  '<div class="horizonal-net"></div>'+
  '<div class="playerBox-horizontal playerBox-right"></div>'+

  '</div>'+

  '</div>'+
  '</div>',
  props: ['table'],
  data: function(){
    return {tableData: this.table, notUpdating: true}
  },
  methods: {
    setNotUpdating: function(){ this.notUpdating = true;},
    startUpdating: function(){ this.notUpdating = false;}
  }
});