<template>
  <tr>
    <td v-if="edit" colspan="4">
      <div>
        <div style="float:right">
          <i class="fa fa-check-circle green-hover" v-on:click="finishEdit()"></i>
          <i class="fa fa-window-close red-hover" v-on:click="cancelEdit()"></i>
        </div>
        <div style="display: flex;">
          <label>Voornaam</label><input v-model="editPlayer.firstname">
          <label>Achternaam</label><input v-model="editPlayer.lastname">
        </div>
        <div><label>Klassement</label><select v-model="editPlayer.rank">
          <option v-for="rank in ranks" :key="rank.name" :value="rank">{{rank.name}}</option>
        </select></div>
      </div>
    </td>
    <td v-if="!edit">{{player.firstname}}</td>
    <td v-if="!edit">{{player.lastname}}</td>
    <td v-if="!edit">{{player.rank.name}}</td>
    <td v-if="!edit">
      <i class="fa fa-edit orange-hover" v-on:click="openEdit()"></i>
      <i class="fa fa-trash-o red-hover" v-on:click="deletePlayer()"></i>
    </td>
  </tr>
</template>

<script>
export default {
  name: 'PlayerLine',
  props: ['player', 'ranks'],
  data: () => {
    return {
      edit: false,
      editPlayer: {}
    }
  },
  mounted () {
    this.editPlayer = this.player
  },
  methods: {
    openEdit () {
      this.edit = true
    },
    cancelEdit () {
      this.edit = false
    },
    finishEdit () {
      // send call
      this.edit = false
    },
    deletePlayer () {
      this.$emit('delete', this.player.id)
    }
  }
}
</script>

<style>
  .green-hover:hover {
    color: #31b531;
  }

  .red-hover:hover {
    color: #f64044;
  }
  .orange-hover:hover {
    color: #ff9331;
  }
</style>
