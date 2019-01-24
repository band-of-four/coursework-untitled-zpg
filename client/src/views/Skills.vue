<template>
<main>
  <div class="page-section">
    <div class="page-section-header">
      <div class="page-section-header__icon icon--stars"></div>
      <span class="page-section-header__text page-section-header__text--large">Навыки обращения с существами</span>
    </div>
    <section v-show="skills.stale">
      <a href="#" class="action-link" @click.prevent="refresh">Обновить</a> 
    </section>
    <ShowMorePaginator :items="skills.items" :page="skills.page" :per-page="skills.perPage" @show-more="loadNext">
      <section slot-scope="{ item }">
        <strong>{{ item.creatureName }}</strong>
        <p>Бонус: {{ item.modifier }}</p>
      </section>
    </ShowMorePaginator>
  </div>
</main>
</template>

<script>
import ShowMorePaginator from '/components/ShowMorePaginator.vue';
import { mapState, mapActions } from 'vuex';

export default {
  name: 'Skills',
  components: { ShowMorePaginator },
  created() {
    this.refresh();
  },
  methods: mapActions('skills', ['loadNext', 'refresh']),
  computed: mapState(['skills'])
}
</script>
