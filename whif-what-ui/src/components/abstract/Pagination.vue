<template>
  <div class="row">

    <div>
      <span>pagina</span>
      <i class="fa fa-arrow-circle-left" v-on:click="prevPage()"></i>
      <span>{{currentPage + '/' + pageCount}}</span>
      <i class="fa fa-arrow-circle-right"></i>
    </div>
    <div>
      <label>Rijen per pagina</label>
      <select v-model="selectedSize" v-on:change="changeSize">
        <option v-bind:key="pageSize" v-for="pageSize in pageRanges" :value="pageSize">{{pageSize}}</option>
      </select>
    </div>
  </div>
</template>

<script>
export default {
  props: ['numberOfItems', 'currentPage', 'pageSize'],
  data () {
    return {
      selectedSize: this.pageSize,
      pageRanges: [5, 10, 20]
    }
  },
  methods: {
    prevPage () { if (this.currentPage > 1) this.$emit('previousPageSelected') },
    nextPage () { if (this.currentPage > this.lastPage()) this.$emit('nextPageSelected') },
    changeSize () {
      console.log(this.selectedSize)
      this.$emit('sizeChanged', this.selectedSize)
    }
  },
  computed: {
    lastPage: () => 5,
    pageCount () {
      return Math.round(this.numberOfItems / this.pageSize) + ((this.numberOfItems % this.pageSize === 0) ? 0 : 1)
    }
  }
}
</script>

<style lang="css" scoped>
  a {
    cursor: pointer;
  }
  .row {
    display: flex;
    flex-flow: row nowrap;
    justify-content: center;
  }
</style>
