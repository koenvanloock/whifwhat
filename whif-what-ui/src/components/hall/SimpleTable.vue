<template>
    <div class="pingpongTable">
        <!-- vertical-->
        <div class="tableContainer" v-if="!(tableData.hidden || tableData.horizontal)">
            <div class="playerBox-top"
                 v-bind:class="{ playerBox: tableData.isGreen, playerBoxBlue: !tableData.isGreen}">
            </div>
            <div class="net"></div>
            <div class="playerBox-bottom"
                 v-bind:class="{ playerBox: tableData.isGreen, playerBoxBlue: !tableData.isGreen}">
            </div>
        </div>
        <!-- horizontal-->
        <div class="tableContainer" v-if="!tableData.hidden && tableData.horizontal">
            <div class="playerBox-left"
                 v-bind:class="{ playerBoxHorizontal: tableData.isGreen, playerBoxHorizontalBlue: !tableData.isGreen}">
            </div>
            <div class="horizonal-net"></div>
            <div class="playerBox-right"
                 v-bind:class="{ playerBoxHorizontal: tableData.isGreen, playerBoxHorizontalBlue: !tableData.isGreen}">

            </div>
        </div>
        <div style="display: flex;">
            <v-btn @click="rotate()">
                <v-icon>rotate_90_degrees_ccw</v-icon>
            </v-btn>
            <v-btn @click="hide()">
                <v-icon v-if="this.tableData.hidden">visibility</v-icon>
                <v-icon v-else>visibility_off</v-icon>
            </v-btn>
            <v-switch v-model="tableData.isGreen" color="teal"></v-switch>
        </div>

    </div>

</template>

<script>
    export default {
      name: 'SimpleTable',
      props: ['table'],
      data () {
        return {
            'tableData': {}
        }
      },
      mounted () {
        this.tableData = this.table;
      },
      methods: {
        rotate () {
          this.tableData.horizontal = !this.tableData.horizontal;
        },
        hide () {
          this.tableData.hidden = !this.tableData.hidden;
        }
      }

    }
</script>

<style>
    .pingpongTable{
        background: grey;
        width: 550px;
        height: 550px;
        padding: 5px;
        display: inline-block;
    }

    .net{
        height: 10px;
        background: white;
        width: 340px;
    }

    .playerBox{
        height: 245px;
        width: 290px;
        background: #0D7E83;
        border-left: solid white 5px;
        border-right: solid white 5px;
    }

    .playerBoxBlue{
        height: 245px;
        width: 290px;
        background: #2b28bb;
        border-left: solid white 5px;
        border-right: solid white 5px;
    }

    .playerBox-top{
        border-top: solid white 5px;
    }

    .playerBox-bottom{
        border-bottom: solid white 5px;
    }

    .playerBox-left{
        display: inline-block;
        border-left: solid white 5px;
    }

    .playerBox-right{
        display: inline-block;
        border-right: solid white 5px;
    }
    .tableContainer{
        height: 500px;
        width: 500px;
    }

    .horizonal-net{
        width: 10px;
        background: white;
        float: left;
        height:340px;
    }

    .playerBoxHorizontal{
        float: left;
        width: 240px;
        height: 290px;
        margin-top: 20px;
        margin-bottom: 20px;
        display: inline-block;
        background: #0D7E83;
        border-top: solid white 5px;
        border-bottom: solid white 5px;
    }

    .playerBoxHorizontalBlue{
        float: left;
        width: 240px;
        height: 290px;
        margin-top: 20px;
        margin-bottom: 20px;
        display: inline-block;
        background: #2b28bb;
        border-top: solid white 5px;
        border-bottom: solid white 5px;
    }
</style>