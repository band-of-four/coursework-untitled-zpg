<template>
<main>
  <h1 class="heading">Дневник</h1>
  <section v-show="diary.stale">
    <a href="#" @click.prevent="refresh">Обновить</a> 
  </section>
  <ShowMorePaginator :item-size="sectionSize" :items="diarySections" :page="diary.page" :per-page="diary.perPage" @show-more="loadNext">
    <section slot-scope="{ item }">
      <div class="diary-section-heading">
        <span class="diary-section-heading__text">{{ item.title }}</span>
        <Timeago class="diary-section-heading__time" :since="item.date" />
      </div>
      <div class="note" v-for="n in item.notes">
        <span>&mdash; {{ n.text }}</span>
        <Heart class="note__heart" :key="n.id" :id="n.id" :init-hearts="n.heartCount" :init-set="n.isHearted" />
      </div>
    </section>
  </ShowMorePaginator>
</main>
</template>

<script>
import Heart from '/components/Heart.vue';
import ShowMorePaginator from '/components/ShowMorePaginator.vue';
import Timeago from '/components/Timeago.vue';
import { mapState, mapGetters, mapActions } from 'vuex';

export default {
  name: 'Diary',
  components: { Heart, ShowMorePaginator, Timeago },
  created() {
    this.refresh();
  },
  methods: {
    ...mapActions('diary', ['loadNext', 'refresh']),
    sectionSize(section) {
      return section.notes.length;
    }
  },
  computed: {
    ...mapState('student', ['name', 'level', 'hp']),
    ...mapState(['diary']),
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
