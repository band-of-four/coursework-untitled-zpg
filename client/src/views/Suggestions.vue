<template>
<div>
  <h1>Предложить</h1>
  <router-link to="/suggest/lesson">Урок</router-link>
  <router-link to="/suggest/creature">Монстр</router-link>
  <router-link to="/suggest/club">Клуб</router-link>
  <ShowMorePaginator :item-count="approved.length" :page="page" :per-page="10" @show-more="loadNext"> 
    <h2>Мои предложения</h2>
    <article v-for="s in approved">
      <h4>{{ s.lesson || s.club || s.creature || (s.travel && "Путешествую...") }}</h4>
      {{ s.gender === 'Female' ? 'Ж' : 'М' }} — {{ s.text }}
      <strong>{{ s.heartCount }}</strong>
    </article>
  </ShowMorePaginator>
</div>
</template>

<script>
import ShowMorePaginator from '/components/ShowMorePaginator.vue';
import { getApproved } from '/api/suggestions.js';

export default {
  name: 'Suggestions',
  components: { ShowMorePaginator },
  data: () => ({
    approved: [],
    page: -1
  }),
  created() {
    this.loadNext();
  },
  methods: {
    async loadNext() {
      this.page += 1;
      this.approved = this.approved.concat(await getApproved(this.page));
    }
  }
}
</script>
