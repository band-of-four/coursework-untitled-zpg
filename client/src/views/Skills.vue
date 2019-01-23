<template>
<main>
  <h1 class="heading">Навыки обращения с существами</h1>
  <section v-show="skills.stale">
    <a href="#" @click.prevent="refresh">Обновить</a> 
  </section>
  <ShowMorePaginator :items="skills.items" :page="skills.page" :per-page="skills.perPage" @show-more="loadNext">
    <section slot-scope="{ item }">
      <strong>{{ item.creatureName }}</strong>
      <p>Бонус: {{ item.modifier }}</p>
    </section>
  </ShowMorePaginator>
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
