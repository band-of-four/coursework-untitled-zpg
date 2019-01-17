<template>
<section>
  <h1>Предложить записку для урока</h1>
  <FilterList :items="lessons" @selected="selectLesson"/>
  <form v-show="selectedLesson" @submit.prevent="submit">
    <h3>{{ selectedLesson }}</h3>
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
import { getLessonNames, postText } from '/api/suggestions.js';

export default {
  name: 'SuggestionsLesson',
  components: { FilterList },
  created() {
    getLessonNames().then((lessons) => this.lessons = lessons);
  },
  data: () => ({
    lessons: [],
    error: null,
    selectedLesson: null,
    text: '',
    gender: 'Female'
  }),
  methods: {
    selectLesson(name) {
      this.selectedLesson = name;
    },
    async submit() {
      if (this.text.length < 3) {
        this.error = 'Текст записки должен содержать минимум 3 символа';
      }
      else {
        this.error = null;
        await postText({ text: this.text, gender: this.gender, lessonName: this.selectedLesson });
        this.$router.push({ path: '/', query: { flash: 'suggest' } });
      }
    }
  }
}
</script>
