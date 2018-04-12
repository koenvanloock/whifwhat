<template>
    <v-app>
        <v-navigation-drawer
                persistent
                :mini-variant="miniVariant"
                :clipped="true"
                v-model="drawer"
                enable-resize-watcher
                fixed
                app
                dark
                style="background: #0D7E83;color:white"
        >
            <v-list style="background: #0D7E83;color:white">
                <v-list-tile
                        v-for="(item, i) in items"
                        :key="i"
                        @click="goto(item.path)"
                >
                    <v-list-tile-action>
                        <v-icon v-html="item.icon"></v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title v-text="item.title"></v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                <v-divider></v-divider>
                <v-list-tile  @click.stop="miniVariant = !miniVariant">
                    <v-list-tile-action>
                        <v-icon v-html="miniVariant ? 'chevron_right' : 'chevron_left'"></v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title>Small menu</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </v-list>
        </v-navigation-drawer>
        <v-toolbar
                app
                :clipped-left="true"
                color="primary"
                dark
        >
            <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>

            <v-toolbar-title v-text="title"></v-toolbar-title>
        </v-toolbar>
        <v-content>
            <router-view/>
        </v-content>
    </v-app>
</template>

<script>
  export default {
    name: 'App',
    components: {},
    data () {
      return {
        clipped: false,
        drawer: true,
        fixed: false,
        items: [{
          icon: 'note_add',
          title: 'Create tournament',
          path: 'createTournament'
        },
        {
            icon: 'mode_edit',
            title: 'Edit tournament',
            path: 'updateTournament'
        },
        {
            icon: 'person',
            title: 'Players',
            path: 'managePlayers'
        }],
        miniVariant: false,
        right: true,
        rightDrawer: false,
        title: 'Whiff-what'
      }
    },
    methods: {
      toggelNav: () => {
        console.log('on')
      },
      goto (route) { this.$router.push('/' + route) }
    },
    mounted () {
      this.activeTab = this.$route.path
    },
    watch: {
      '$route': function (to) {
        this.activeTab = to.path
      }
    }
  }
</script>
