Vue.component("referee-box", {
  template: '<div v-if="visible">' +
  '<div v-if="!horizontal" style="position: relative;top: 160px; right: left: 20px;height: 200px; width: 200px; float right" v-on:dragover="allowDrop" v-on:drop="dropRef">' +
  '<button v-show="referee" v-on:click="removeReferee" style="position: relative; top: 50px; left: 10px;"> <i class="fa fa-close"></i></button>' +
  '<p style="margin-top: 90px;float:right;position: relative;z-index: 90;left: -35px;font-weight:bold;word-wrap: break-word;" class="rotate" v-if="referee">{{referee.firstname +" "+referee.lastname}}</p>' +
  '<div style="position:relative; top: 25px; left:40px; background: blue; width: 50px; 50px; height: 100px;"></div>' +
  '</div>' +
  '<div v-if="horizontal"  style="position: relative; bottom: 150px; left: 250px;height: 200px; width: 200px;" v-on:dragover="allowDrop" v-on:drop="dropRef">' +
  '<button v-show="referee" v-on:click="removeReferee" style="position: relative; left: 31px;"> <i class="fa fa-close"></i></button>' +
  '<div style="position:relative; left: -40px; background: blue; width: 100px; 50px; height: 50px;"></div>' +
  '<p style="float:right;position: relative;z-index: 90;right:130px;font-weight:bold;word-wrap: break-word;" v-if="referee">{{referee.firstname +" "+referee.lastname}}</p>' +
  '</div>' +
  '</div>',

  props: ['referee', 'horizontal', 'hallId', 'row', 'column', 'visible'],
  methods: {
    dropRef: function (ev) {
      ev.preventDefault();
      var referee = JSON.parse(ev.dataTransfer.getData("referee"));
      Vue.http.post("/hallRef/" + this.hallId + '/' + this.row + '/' + this.column, referee).then(function () {
      });
    },
    allowDrop: function (event) {
      event.preventDefault();
    },
    removeReferee: function () {
      Vue.http.patch("/hallRef/" + this.hallId + '/' + this.row + '/' + this.column, this.referee).then(function () {
      });
    },
    floatIfVertical: function(){
      return this.horizontal ? "none" : "right";
    }
   }

});