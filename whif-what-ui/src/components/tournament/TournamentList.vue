<template>
    <div>
    <div style="display: flex; flex-wrap: wrap; align-content: flex-start;">
        <v-card v-for="tournament in tournaments" :key="tournament.id" style="margin: 10px; width: 30%" class="secondary">
            <v-card-title style="color: white; font-weight: bold">{{tournament.tournamentName +' - '+ tournament.tournamentDate}}</v-card-title>
            <v-divider></v-divider>
            <ul>
                <li v-for="series in tournament.series" :key="series.id">{{series.seriesName}}</li>
            </ul>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn icon @click="startTournament(tournament)">
                    <v-icon>play_arrow</v-icon>
                </v-btn>
                <v-btn icon @click="editTournament(tournament.id)">
                    <v-icon>settings</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </div>
        <v-btn fab large class="btn btn--bottom btn--floating btn--fixed btn--right theme--dark accent" @click="createTournament">
            <v-icon>note_add</v-icon>
        </v-btn>
    </div>
</template>

<script>
    import axios from 'axios'

    export default {
      components: {},
      name: 'TournamentList',
      data () {
        return {tournaments: []}
      },
      props: [],
      methods: {
        editTournament (tournamentId) {
          this.$router.push('/editTournament/' + tournamentId)
        },
        createTournament () {
          this.$router.push('/createTournament')
        },
        startTournament (tournament) {
          this.$emit('currentChanged', tournament)
          this.$router.push('/playTournament/' + tournament.id)
        },

      },
      mounted () {
        axios.get('http://localhost:9000/tournaments').then(response => this.tournaments = response.data)
      }
    }
</script>