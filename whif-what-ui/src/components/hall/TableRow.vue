<template>
    <div>
        <div v-for="table in tableRow" :key="table.id" style="color:white;width: 500px;height: 600px;display: inline-block">
        <simple-table :table="table"></simple-table>
        </div>
    </div>
</template>

<script>
    import SimpleTable from './SimpleTable'

    export default {
      name: 'TableRow',
      components: {SimpleTable},
      props: ['tableRow']
    }
</script>