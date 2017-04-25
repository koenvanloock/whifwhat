Vue.component("table-row", {
  template: '<div><span v-for="table in tableRow" style="color:white; margin-left: 20px; margin-right: 20px; width: 500px;height: 500px;">' +
  '<vue-pingpong-table :table="table"></vue-pingpong-table> ' +
  '</span>' +
  '</div>',
      props: ['tableRow']
});