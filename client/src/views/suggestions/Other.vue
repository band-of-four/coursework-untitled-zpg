<template>
<section>
  <h1>Предложить записку для {{stage}}</h1>
  <form @submit.prevent="submit">
    <textarea v-model="text" placeholder="Текст записки"></textarea>
    <label><input type="radio" id="female" value="Female" v-model="gender">Для женских персонажей</label>
    <label><input type="radio" id="male" value="Male" v-model="gender">Для мужских персонажей</label>
    <p>{{ error }}</p>
    <button type="submit">Отправить</button>
  </form>
</section>
</template>


<script>
import { postText } from '/api/suggestions.js';
export default {
  name: 'SuggestionsOther',
  props: {
    kind: { type: String, required: true }
  },
  data() {
    return {
      error: null,
      text: '',
      gender: 'Female'
    };
  },
  computed: {
    stage() {
      return this.kind === 'Travel' ? 'путешествий'
           : this.kind === 'Library' ? 'посещений библиотеки'
           : this.kind === 'Infirmary' ? 'посещений медкабинета'
           : '';
    }
  },
  methods: {
    async submit() {
      if (this.text.length < 3) {
        this.error = 'Текст записки должен содержать минимум 3 символа';
      }
      else {
        this.error = null;
        await postText({ text: this.text, gender: this.gender, stage: this.kind });
        this.$router.push({ path: '/', query: { flash: 'suggest' } });
      }
    }
  }
}
</script>
