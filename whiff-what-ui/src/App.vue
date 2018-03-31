<template>
  <div id="app" class="container">
    <v-app>
    <left-nav id="sidebar" :active-tab="activeTab"/>
    <div class="main-content">
      <router-view/>
    </div>
    </v-app>
  </div>
</template>

<script>
import LeftNav from './components/LeftNav'

export default {
  components: {LeftNav},
  name: 'App',
  data () {
    return {
      activeTab: null
    }
  },
  methods: {
    toggelNav: () => {
      console.log('on')
    }
  },
  mounted () {
    this.activeTab = this.$route.path
  },
  watch: {
    '$route': function (to, from) {
      this.activeTab = to.path
    }
  }
}
</script>

<style>
  html, body {
    padding: 0;
    margin: 0;
  }

  #app {
    margin: 0;
    padding: 0;
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  }

  #sidebar {
    width: 200px;
  }

  .main-content {
    margin-left: 205px;
    padding: 0;
  }
</style>
