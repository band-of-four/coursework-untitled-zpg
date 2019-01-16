<template>
<div>
  <h1>Дневник</h1>
  <section v-for="entry in entries">
    {{ entry }}
  </section>
</div>
</template>

<script>
import { getDiaryEntries } from '/api/student.js';

export default {
  name: 'Diary',
  data: () => ({
    entries: []
  }),
  created() {
    getDiaryEntries().then((entries) => this.entries = entries);
  },
  computed: {
  }
}
</script>
