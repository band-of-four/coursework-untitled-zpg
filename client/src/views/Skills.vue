<template>
<ShowMorePaginator :item-count="skills.items.length" :page="skills.page" :per-page="skills.perPage" @show-more="loadNext">
  <h1>Навыки обращения с существами</h1>
  <section v-show="skills.stale">
    <a href="#" @click.prevent="refresh">Обновить</a> 
  </section>
  <section v-for="s in skills.items">
    <strong>{{ s.creatureName }}</strong>
    <p>Бонус: {{ s.modifier }}</p>
  </section>
</ShowMorePaginator>
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
