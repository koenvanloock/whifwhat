<template>
<div>
    <div v-if="!editMode" @click="showEdit"><p class="box">{{value}}</p></div>
    <v-text-field
            v-else
            @input="updateValue"
        :label="labelTag"
        v-model="text"
    ></v-text-field>
</div>
</template>

<script>
    export default {
      components: {},
      name: 'ClickToEdit',
      data: () => {
        return {
          text: null,
          editMode: false
        }
      },
      props: ['labelTag', 'value'],
      mounted () {
        if(this.value !== null && this.value !== '') {
          this.text = this.value
        } else {
          this.text = 'naam'
        }
      },
      methods: {
        showEdit () {
          this.editMode = true
        },
        updateValue () {
          this.$emit('input', this.text)
        }
      }
    }
</script>

<style>
    .box {
        width: 100%;
        min-height: 40px;
        display: block;
    }

</style>


