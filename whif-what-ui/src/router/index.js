import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import Players from '@/components/players/Players'
import TournamentCreateFlow from '@/components/tournament/TournamentCreateFlow'
import UpdateTournament from '@/components/tournament/UpdateTournament'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/managePlayers',
      name: 'Players',
      component: Players
    },
    {
      path: '/createTournament',
      name: 'tournament-create-flow',
      component: TournamentCreateFlow
    },
    {
      path: '/updateTournament',
      name: 'update-tournament',
      component: UpdateTournament
    },
    {
      path: '/editTournament/:tournamentId',
      name: 'edit-tournament',
      component: TournamentCreateFlow
    }
  ]
})
