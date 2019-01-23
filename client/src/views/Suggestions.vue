<template>
<main>
  <h1 class="heading">Предложить</h1>
  <router-link to="/suggest/lesson">Урок</router-link>
  <router-link to="/suggest/creature">Монстр</router-link>
  <router-link to="/suggest/club">Клуб</router-link>
  <router-link to="/suggest/travel">Путешествие</router-link>
  <router-link to="/suggest/library">Библиотека</router-link>
  <router-link to="/suggest/infirmary">Медкабинет</router-link>
  <h1 class="heading">Мои предложения</h1>
  <ShowMorePaginator :items="approved" :page="page" :per-page="10" @show-more="loadNext"> 
    <article slot-scope="{ item }">
      <h4>{{ item.lesson || item.club || item.creature || (item.travel && "Путешествую...") }}</h4>
      {{ item.gender === 'Female' ? 'Ж' : 'М' }} — {{ item.text }}
      <strong>{{ item.heartCount }}</strong>
    </article>
  </ShowMorePaginator>
</main>
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
