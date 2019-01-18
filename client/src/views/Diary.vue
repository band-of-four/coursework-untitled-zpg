<template>
<div>
  <h1>Дневник</h1>
  <section v-for="s in sections">
    <p><strong>
      {{ s.heading.lesson || s.heading.club || s.heading.creature || (s.heading.travel && "Путешествую...") }}
      —
      {{ s.heading.date }}
    </strong></p>
    <p v-for="n in s.notes">— {{ n.text }}</p>
  </section>
</div>
</template>

<script>
import { getDiarySections } from '/api/notes.js';

export default {
  name: 'Diary',
  data: () => ({
    sections: []
  }),
  created() {
    getDiarySections().then((sections) => this.sections = sections);
  },
  computed: {

  }
}
</script>
