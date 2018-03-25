<template>
  <div class="nav">
    <ul id="nav-list">
      <li v-on:click="goto('createTournament')" v-bind:class="{ selected: isChecked('createTournament') }"><i class="fa fa-plus"></i><span class="nav-label">Add tournament</span></li>
      <li v-on:click="goto('editTournament')" v-bind:class="{ selected: isChecked('editTournament') }"><i class="fa fa-pencil"></i><span class="nav-label">Edit tournament</span></li>
      <li v-on:click="goto('managePlayers')" v-bind:class="{ selected: isChecked('managePlayers') }"><i class="fa fa-users"></i><span class="nav-label">Players</span></li>
    </ul>
  </div>
</template>

<script>
import router from '../router'

export default {
  name: 'LeftNav',
  props: ['activeTab'],
  data () {
    return {}
  },
  methods: {
    goto: (route) => router.push('/' + route),
    isChecked (route) {
      return this.activeTab === '/' + route
    }
  }
}
</script>

<style>
  .selected {
    border-left: 2px yellow solid;
    padding: 5px;
    background: #278527;
    cursor: pointer;
  }
  #nav-list li:hover {
    background: #278527;
  }
  .nav {
    color: white;
    font-weight: bold;
    text-align: left;
    transition: width 0.3s, left 0.3s;
    position: fixed;
    z-index: 400;
    width: 220px;
    top: 0;
    bottom: 0;
    left: 0;
    box-shadow: inset -2px 0 0 #c3c3c3;
    transform: translate3d(0, 0, 0);
    background: #31b531;
    padding-left: 0;
    list-style: none;
  }
  .nav-label {
    display: inline-block;
    margin-left: 20px;
  }
  #nav-list li {
    padding: 5px 5px 5px 7px;
    cursor: pointer;
  }

  #nav-list {
    padding: 0;
    list-style-type: none;
  }
</style>
