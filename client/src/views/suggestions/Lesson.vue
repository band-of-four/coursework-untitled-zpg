<template>
<div>
  <h1>Предложить урок</h1>
  <FilterList v-bind:items="['The Role of Chicken Tenders in Modern Storytelling', 'The Study of Ancient Cyberian Tribes']"/>
</div>
</template>

<script>
import FilterList from '/components/FilterList.vue';

export default {
  name: 'SuggestionsLesson',
  components: { FilterList },
  data: () => ({
  })
}
</script>
