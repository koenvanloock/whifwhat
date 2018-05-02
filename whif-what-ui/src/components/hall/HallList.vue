<template>
    <div style="display: flex">
        <v-card v-for="hall in halls" :key="hall.id">
            <v-card-title>{{hall.hallName}}</v-card-title>
            <v-card-text>{{hall.rows + ' x ' + hall.tablesPerRow}}</v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn icon @click="chooseHall(hall)">
                    <v-icon>my_location</v-icon>
                </v-btn>
                <v-btn icon @click="editHall(hall)">
                    <v-icon>settings</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>

    </div>

</template>

<script>
  import axios from 'axios'
  export default {
    name: 'HallList',
    data (){
      return {
        halls: []
      }
    },
    mounted () {
      axios.get('http://localhost:9000/hall').then((response) => this.halls = response.data)
    },
    methods: {
      editHall (hall){
        this.$router.push({name: 'hallUpdate', params: {hall: hall}})
      }
    }
  }
</script>