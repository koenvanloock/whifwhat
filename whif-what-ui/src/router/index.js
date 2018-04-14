import Vue from 'vue'
import Router from 'vue-router'
import Players from '@/components/players/Players'
import TournamentCreateFlow from '@/components/tournament/TournamentCreateFlow'
import TournamentList from '@/components/tournament/TournamentList'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/managePlayers',
      name: 'Players',
      component: Players
    },
    {
      path: '/createTournament',
      name: 'tournament-create-flow',
      props: { createFlow: true},
      component: TournamentCreateFlow
    },
    {
      path: '/editTournament/:tournamentId',
      name: 'edit-tournament',
      props: {createFlow: false},
      component: TournamentCreateFlow
    },
    {
      path: '/tournaments',
      name: 'tournaments',
      component: TournamentList
    }
  ]
})
