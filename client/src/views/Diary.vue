<template>
<ShowMorePaginator :item-count="noteCount" :per-page="10" @next-page="loadPage">
  <h1>Дневник</h1>
  <section v-for="s in sections">
    <p><strong>
      {{ s.heading.lesson || s.heading.club || s.heading.creature || (s.heading.travel && "Путешествую...") }}
      —
      {{ s.heading.date }}
    </strong></p>
    <Note v-for="n in s.notes" :key="n.id" :id="n.id" :text="n.text" :init-hearts="n.heartCount" :init-toggled="n.isHearted" />
  </section>
</ShowMorePaginator>
</template>

<script>
import { getDiarySections } from '/api/note.js';
import Note from '/components/Note.vue';
import ShowMorePaginator from '/components/ShowMorePaginator.vue';

export default {
  name: 'Diary',
  components: { Note, ShowMorePaginator },
  data: () => ({
    sections: []
  }),
  created() {
    this.loadPage(0);
  },
  methods: {
    async loadPage(page) {
      this.sections = this.sections.concat(await getDiarySections(page));
    }
  },
  computed: {
    noteCount() {
      return this.sections.reduce((acc, { notes }) => acc + notes.length, 0);
    }
  }
}
</script>
