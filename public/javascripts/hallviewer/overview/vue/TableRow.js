Vue.component("table-row", {
  template: '<div style="display: flex"><div v-for="table in tableRow" style="color:white; flex: auto; margin-left: 20px; margin-right: 20px; width: 500px;height: 600px;">' +
  '<vue-pingpong-table :table="table"></vue-pingpong-table> ' +
  '</div>' +
  '</div>',
      props: ['tableRow']
});