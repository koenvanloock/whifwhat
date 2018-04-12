<template>
    <div>
      <v-dialog v-model="dialog" max-width="500px">
        <v-btn color="primary" dark slot="activator" class="mb-2">New Item</v-btn>
        <v-card>
          <v-card-title>
            <span class="headline">{{ formTitle }}</span>
          </v-card-title>
          <v-card-text>
            <v-container grid-list-md>
              <v-layout wrap>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="Voornaam" v-model="editedItem.firstname"></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="Achternaam" v-model="editedItem.lastname"></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-select label="Klassement" v-model="editedItem.rank" :items="ranks" item-text="name"></v-select>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="Afbeeldingspad" v-model="editedItem.imagepath"></v-text-field>
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" flat @click.native="close">Cancel</v-btn>
            <v-btn color="blue darken-1" flat @click.native="save">Save</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
      <v-data-table
              :headers="headers"
              :items="players"
              hide-actions
              class="elevation-1"
      >
        <template slot="items" slot-scope="props">
          <td>{{ props.item.firstname }}</td>
          <td>{{ props.item.lastname }}</td>
          <td>{{ props.item.rank.name }}</td>
          <td>{{ props.item.imagepath }}</td>
          <td class="justify-center layout px-0">
            <v-btn icon class="mx-0" @click="editItem(props.item)">
              <v-icon color="teal">edit</v-icon>
            </v-btn>
            <v-btn icon class="mx-0" @click="deleteItem(props.item)">
              <v-icon color="red">delete</v-icon>
            </v-btn>
          </td>
        </template>
        <template slot="no-data">
          <v-btn color="primary" @click="initialize">Reset</v-btn>
        </template>
      </v-data-table>
    </div>

</template>

<script>
import axios from 'axios'
import PlayerLine from './PlayerLine'
import Pagination from '../abstract/Pagination'
export default {
  components: {
    Pagination,
    PlayerLine
  },
  'name': 'Players',
  data: () => {
    return {
      dialog: false,
      headers: [
        { text: 'Voornaam', value: 'firstname' },
        { text: 'Achternaam', value: 'lastname' },
        { text: 'Klassement', value: 'rank.number' },
        { text: 'Afbeelding', value: 'imagepath' },
        { text: 'Actions', value: 'name', sortable: false }
      ],
      players: [],
      ranks: [],
      editedIndex: -1,
      editedItem: {
        firstname: '',
        lastname: '',
        rank: null,
        imagepath: ''
      },
      defaultItem: {
        firstname: '',
        lastname: '',
        rank: null,
        imagepath: ''
      },
      currentPage: 1,
      numberOfItems: 27,
      pageSize: 5
    }
  },
  computed: {
    formTitle () {
      return this.editedIndex === -1 ? 'New Item' : 'Edit Item'
    }
  },
  methods: {
    editItem (item) {
      this.editedIndex = this.players.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialog = true
    },

    deleteItem (item) {
      const index = this.players.indexOf(item)
      confirm('Are you sure you want to delete this item?') &&
        axios.delete('http://localhost:9000/players/' + item.id).then(() => this.players.splice(index, 1))
    },

    close () {
      this.dialog = false
      setTimeout(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      }, 300)
    },

    save () {
      if (this.editedIndex > -1) {
        console.log(this.editedItem)
        this.editedItem.rank = this.ranks.find(rank => rank.value === this.editedItem.rank)
        Object.assign(this.players[this.editedIndex], this.editedItem)
      } else {
        this.players.push(this.editedItem)
      }
      this.close()
    },

    initialize () {
      axios.get('http://localhost:9000/players', {
        crossDomain: true
      }).then(response => {
        this.players = response.data
      })
      axios.get('http://localhost:9000/ranks', {
        crossDomain: true
      }).then(response => {
        this.ranks = response.data
      })
    }

  },
  watch: {
    dialog (val) {
      val || this.close()
    }
  },
  mounted () {
    this.initialize()
  }
}
</script>
