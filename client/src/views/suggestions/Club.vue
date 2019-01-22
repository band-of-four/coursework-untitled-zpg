<template>
<section>
  <h1>Предложить записку для клуба</h1>
  <FilterList :items="clubs" @selected="selectClub"/>
  <form v-show="selectedClub" @submit.prevent="submit">
    <h3>{{ selectedClub }}</h3>
    <textarea v-model="text" placeholder="Текст записки"></textarea>
    <label><input type="radio" id="female" value="Female" v-model="gender">Для женских персонажей</label>
    <label><input type="radio" id="male" value="Male" v-model="gender">Для мужских персонажей</label>
    <p>{{ error }}</p>
    <button type="submit">Отправить</button>
  </form>
</section>
</template>

<script>
import FilterList from '/components/FilterList.vue';
import { getClubNames, postText } from '/api/suggestions.js';

export default {
  name: 'SuggestionsClub',
  components: { FilterList },
  created() {
    getClubNames().then((clubs) => this.clubs = clubs);
  },
  data: () => ({
    clubs: [],
    error: null,
    selectedClub: null,
    text: '',
    gender: 'Female'
  }),
  methods: {
    selectClub(name) {
      this.selectedClub = name;
    },
    async submit() {
      if (this.text.length < 3) {
        this.error = 'Текст записки должен содержать минимум 3 символа';
      }
      else {
        this.error = null;
        await postText({ text: this.text, gender: this.gender, clubName: this.selectedClub });
        this.$router.push({ path: '/', query: { flash: 'suggest' } });
      }
    }
  }
}
</script>
