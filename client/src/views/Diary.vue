<template>
<div>
  <h1>Дневник</h1>
  <section v-for="s in sections">
    <p><strong>
      {{ s.heading.lesson || s.heading.club || s.heading.creature || (s.heading.travel && "Путешествую...") }}
      —
      {{ s.heading.date }}
    </strong></p>
    <Note v-for="n in s.notes" :key="n.id" :id="n.id" :text="n.text" :init-hearts="n.heartCount" :init-toggled="n.isHearted" />
  </section>
</div>
</template>

<script>
import { getDiarySections } from '/api/note.js';
import Note from '/components/Note.vue';

export default {
  name: 'Diary',
  components: { Note },
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
