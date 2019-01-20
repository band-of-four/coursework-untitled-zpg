<template>
<ShowMorePaginator :item-count="relationships.items.length" :page="relationships.page" :per-page="relationships.perPage" @show-more="loadNext">
  <h1>Отношения</h1>
  <section v-show="relationships.stale">
    <a href="#" @click.prevent="refresh">Обновить</a> 
  </section>
  <section v-for="r in relationships.items">
    <strong>{{ r.studentName }}</strong>
    <p>
      Отношения: {{ r.relationship }}, последнее изменение: {{ r.delta }}
    </p>
  </section>
</ShowMorePaginator>
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
