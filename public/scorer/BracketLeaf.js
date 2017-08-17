var bracketLeaf = Vue.component("bracket-leaf",{
  template: '<span style="display: flex; flex-direction: column; background: #132c4b; margin: 10px;height: 100px; min-width: 250px;color:white; font-size: 28px; border-radius: 5px; border: 2px solid rgba(182,182,182,0.25)">' +
' <div style="height: 50px; padding: 0 25px;width:300px;display: flex"><div style="width: 250px;" v-if="pingpongmatch.playerA">{{pingpongmatch.playerA.firstname +" " +pingpongmatch.playerA.lastname}} </div><div v-else style="width: 250px;"></div><span class="bracket-set-box">{{pingpongmatch.wonSetsA}}</span></div>' +
' <div style="border-top: solid 2px lightgray"></div>' +
' <div style="height: 50px; padding: 0 25px;width:300px;display: flex"><div style="width: 250px;" v-if="pingpongmatch.playerB">{{pingpongmatch.playerB.firstname +" " +pingpongmatch.playerB.lastname}} </div><div v-else style="width: 250px;"></div><span class="bracket-set-box">{{pingpongmatch.wonSetsB}}</span></div>' +
'</span>',
  props: ['pingpongmatch']
});