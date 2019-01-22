<template>
<ShowMorePaginator :item-count="noteCount" :page="diary.page" :per-page="diary.perPage" @show-more="loadNext">
  <h1>Дневник</h1>
  <section v-show="diary.stale">
    <a href="#" @click.prevent="refresh">Обновить</a> 
  </section>
  <section v-for="sec in diarySections">
    <p>
      <strong>{{ sec.title }}</strong>
      <Timeago :since="sec.date" />
    </p>
    <Note v-for="n in sec.notes" :key="n.id" :id="n.id" :text="n.text" :init-hearts="n.heartCount" :init-toggled="n.isHearted" />
  </section>
</ShowMorePaginator>
</template>

<script>
import Note from '/components/Note.vue';
import ShowMorePaginator from '/components/ShowMorePaginator.vue';
import Timeago from '/components/Timeago.vue';
import { mapState, mapGetters, mapActions } from 'vuex';

export default {
  name: 'Diary',
  components: { Note, ShowMorePaginator, Timeago },
  created() {
    this.refresh();
  },
  methods: mapActions('diary', ['loadNext', 'refresh']),
  computed: {
    ...mapState('student', ['name', 'level', 'hp']),
    ...mapState(['diary']),
    ...mapGetters({ noteCount: 'diary/noteCount' }),
    diarySections() {
      return this.diary.items.map(({ heading, notes }) => {
        const title = heading.lesson || heading.club || heading.creature ||
                      (heading.travel && 'Путешествия по школе') ||
                      (heading.infirmary && 'Медкабинет') ||
                      (heading.library && 'Библиотека') || 'Школа';

        return { date: heading.date, title, notes };
      });
    }
  }
}
</script>
