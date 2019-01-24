<template>
<main>
  <div class="page-section">
    <div class="page-section-header">
      <div class="page-section-header__icon icon--flasks"></div>
      <span class="page-section-header__text page-section-header__text--large">Отношения</span>
    </div>
    <section v-show="relationships.stale">
      <a href="#" class="action-link" @click.prevent="refresh">Обновить</a> 
    </section>
    <ShowMorePaginator :items="relationships.items" :page="relationships.page" :per-page="relationships.perPage" @show-more="loadNext">
      <section slot-scope="{ item }">
        <strong>{{ item.studentName }}</strong>
        <p>
          Отношения: {{ item.relationship }}, последнее изменение: {{ item.delta }}
        </p>
      </section>
    </ShowMorePaginator>
  </div>
</main>
</template>

<script>
import ShowMorePaginator from '/components/ShowMorePaginator.vue';
import { mapState, mapActions } from 'vuex';

export default {
  name: 'Relationships',
  components: { ShowMorePaginator },
  created() {
    this.refresh();
  },
  methods: mapActions('relationships', ['loadNext', 'refresh']),
  computed: mapState(['relationships'])
}
</script>
