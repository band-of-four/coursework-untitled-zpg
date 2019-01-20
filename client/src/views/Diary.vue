<template>
<ShowMorePaginator :item-count="noteCount" :page="diary.page" :per-page="diary.perPage" @show-more="loadNext">
  <h1>Дневник</h1>
  <section v-show="diary.stale">
    <a href="#" @click.prevent="refresh">Обновить</a> 
  </section>
  <section v-for="s in diary.sections">
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
import Note from '/components/Note.vue';
import ShowMorePaginator from '/components/ShowMorePaginator.vue';
import { mapState, mapGetters, mapActions } from 'vuex';

export default {
  name: 'Diary',
  components: { Note, ShowMorePaginator },
  data: () => ({
    sections: []
  }),
  created() {
    this.loadNext();
  },
  methods: mapActions({ loadNext: 'diary/loadNext', refresh: 'diary/refresh' }),
  computed: {
    ...mapState(['diary']),
    noteCount() {
      return this.diary.sections.reduce((acc, { notes }) => acc + notes.length, 0);
    }
  }
}
</script>
