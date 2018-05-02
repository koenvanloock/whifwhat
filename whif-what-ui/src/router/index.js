import Vue from 'vue'
import Router from 'vue-router'
import Players from '@/components/players/Players'
import TournamentCreateFlow from '@/components/tournament/TournamentCreateFlow'
import TournamentList from '@/components/tournament/TournamentList'
import Hall from '@/components/hall/Hall'
import HallList from '@/components/hall/HallList'
import HallUpdate from '@/components/hall/HallUpdate'

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
    },{
      path: '/hallOverView',
      name: 'hallOverview',
      component: Hall
    },{
      path: '/hall',
      name: 'hall',
      component: HallList
    },{
      path: '/hallUpdate',
      name: 'hallUpdate',
      component: HallUpdate,
      props: true
    }
  ]
})
