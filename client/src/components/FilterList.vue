<template>
<div>
  <input type="text" v-model="filter" placeholder="Поиск...">
  <ul>
    <li v-for="item in filteredItems">{{ item }}</li>
  </ul>
</div>
</template>

<script>
export default {
  name: 'FilterList',
  props: {
    items: { type: Array, required: true }
  },
  data: () => ({
    filter: ''
  }),
  computed: {
    filteredItems() {
      return this.items.filter((i) =>
        i.toLowerCase().includes(this.filter.toLowerCase()));
    }
  }
}
</script>
