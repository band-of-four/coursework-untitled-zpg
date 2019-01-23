<template>
<main>
  <div class="page-section">
    <div class="page-section-header">
      <div class="page-section-header__icon icon--ball"></div>
      <span class="page-section-header__text page-section-header__text--large">
        Заметка для урока
      </span>
    </div>
    <p class="suggest-form-info">Выбери предмет, для которого напишешь заметку:</p>
    <FilterList :items="lessons" @selected="selectLesson"/>
    <form v-show="selectedLesson" @submit.prevent="submit">
      <p class="suggest-subject">{{ selectedLesson }}</p>
      <p class="suggest-form-info">Текст заметки:</p>
      <textarea class="input input--note-text" v-model="text" placeholder="Минимум 3 символа"></textarea>

      <p class="suggest-form-info">Пол персонажей, которым будет выпадать заметка:</p>
      <div class="radiobutton-group">
        <label class="radiobutton__label">
          <input type="radio" class="radiobutton__input" id="female" value="Female" v-model="gender" />
          <span class="radiobutton__button"><span class="radiobutton__icon fa-venus-mask"></span></span>
        </label>
        <label class="radiobutton__label">
          <input type="radio" class="radiobutton__input" id="male" value="Male" v-model="gender"/>
          <span class="radiobutton__button"><span class="radiobutton__icon fa-mars-mask"></span></span>
        </label>
      </div>

      <p class="suggest-form-info">{{ error }}</p>
      <button class="button" type="submit">Отправить</button>
    </form>
  </div>
</main>
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
