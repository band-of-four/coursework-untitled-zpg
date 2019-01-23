<template>
<main>
  <div class="page-section">
    <div class="page-section-header">
      <div class="page-section-header__icon icon--ball"></div>
      <span class="page-section-header__text page-section-header__text--large">Предложить</span>
    </div>
    <router-link to="/suggest/lesson">Урок</router-link>
    <router-link to="/suggest/creature">Монстр</router-link>
    <router-link to="/suggest/club">Клуб</router-link>
    <router-link to="/suggest/travel">Путешествие</router-link>
    <router-link to="/suggest/library">Библиотека</router-link>
    <router-link to="/suggest/infirmary">Медкабинет</router-link>
  </div>
  <div class="page-section">
    <div class="page-section-header">
      <div class="page-section-header__icon icon--pumpkin"></div>
      <span class="page-section-header__text">Мои предложения</span>
    </div>
    <ShowMorePaginator :items="approved" :page="page" :per-page="10" @show-more="loadNext"> 
      <article slot-scope="{ item }">
        {{ item.gender === 'Female' ? 'Ж' : 'М' }}, {{noteStageLabel(item)}} — {{ item.text }}
        <strong>{{ item.heartCount }}</strong>
      </article>
    </ShowMorePaginator>
  </div>
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
    },
    noteStageLabel(note) {
      return note.lesson || note.club ||
           ( note.stage === 'Travel' ? 'Путешествия по школе'
           : note.stage === 'Infirmary' ? 'Медкабинет'
           : note.stage === 'Library' ? 'Библиотека'
           : note.stage === 'Fight' ? `Битва с монстром ${note.creature}`
           : note.stage === 'FightWon' ? `Победа над монстром ${note.creature}`
           : note.stage === 'FightLost' ? `${note.creature} победил`
           : '' )
    }
  }
}
</script>
