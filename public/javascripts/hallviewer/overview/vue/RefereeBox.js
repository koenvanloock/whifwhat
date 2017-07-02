Vue.component("referee-box",{
  template: '<div style="height: 200px; width: 200px;" v-on:dragover="allowDrop" v-on:drop="dropRef">' +
  '<button v-if="referee" v-on:click="removeReferee" style="float: right"> <i class="fa fa-close"></i></button>' +
  '<p style="margin-top: 90px;float:right;position: relative;z-index: 90;right:-40px;font-weight:bold" class="rotate" v-if="referee">{{referee.firstname +" "+referee.lastname}}</p>' +
  '<div style="position:relative; top: 50px; left:50px; background: blue; width: 50px; 50px; height: 100px;"></div>'+
  '</div>',

  props: ['referee', 'horizontal', 'hallId', 'row','column'],
  methods: {
    dropRef: function(ev) {
      ev.preventDefault();
      var referee = JSON.parse(ev.dataTransfer.getData("referee"));
      Vue.http.post("/hallRef/"+ this.hallId+'/'+ this.row + '/' +  this.column, referee).then(function(){});
    },
    allowDrop: function(event){
      event.preventDefault();
    },
    removeReferee: function () {
      Vue.http.patch("/hallRef/"+ this.hallId+'/'+ this.row + '/' +  this.column, this.referee).then(function(){});
    }
  }

});