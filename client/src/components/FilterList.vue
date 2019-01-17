<template>
<div>
  <input type="text" v-model="filter" placeholder="Поиск...">
  <ul>
    <li v-for="item in filteredItems">
      <a href="#" @click.prevent="selectItem(item)" :class="{ selected: selected === item }">{{ item }}</a>
    </li>
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
    filter: '',
    selected: null
  }),
  computed: {
    filteredItems() {
      return this.items.filter((i) =>
        i.toLowerCase().includes(this.filter.toLowerCase()));
    }
  },
  methods: {
    selectItem(item) {
      this.selected = item;
      this.$emit('selected', item);
    }
  }
}
</script>
