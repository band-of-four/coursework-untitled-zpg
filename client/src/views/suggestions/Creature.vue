<template>
<section>
  <h1>Предложить монстра</h1>
  <form @submit.prevent="submit">
    <label>Имя<input type="text" v-model="creatureName"></label>
    <div v-for="(n, i) in notes">
      <textarea v-model="n.text" placeholder="Текст записки"></textarea>
      <label><input type="radio" value="Female" v-model="n.gender">Ж</label>
      <label><input type="radio" value="Male" v-model="n.gender">М</label>
      <br>
      <label><input type="radio" value="Fight" v-model="n.stage">В бою</label>
      <label><input type="radio" value="FightWon" v-model="n.stage">Победа</label>
      <label><input type="radio" value="FightLost" v-model="n.stage">Поражение</label>
      <button v-if="i >= 6" @click="removeNote(i)">-</button>
      <p>{{ n.error }}</p>
    </div>
    <button @click.prevent="addNote">+</button>
    <button type="submit">Отправить</button>
    <p>{{ error }}</p>
  </form>
</section>
</template>

<script>
import { postCreature } from '/api/suggestions.js';

export default {
  name: 'SuggestionsLesson',
  data: () => ({
    creatureName: '',
    notes: [
      { text: '', gender: 'Female', stage: 'Fight', error: null },
      { text: '', gender: 'Male', stage: 'Fight', error: null },
      { text: '', gender: 'Female', stage: 'FightWon', error: null },
      { text: '', gender: 'Male', stage: 'FightWon', error: null },
      { text: '', gender: 'Female', stage: 'FightLost', error: null },
      { text: '', gender: 'Male', stage: 'FightLost', error: null },
    ],
    error: null
  }),
  methods: {
    addNote() {
      this.notes.push({ text: '', gender: 'Female', stage: 'Fight' });
    },
    removeNote(index) {
      this.notes.splice(index, 1);
    },
    async submit() {
      if (this.creatureName.length < 3) {
        this.error = 'Имя должно содержать минимум 3 символа';
        return;
      }
      else this.error = null;

      for (let n of this.notes) {
        if (n.text.length < 3) {
          n.error = 'Текст записки должен содержать минимум 3 символа';
          return;
        }
        n.error = null;
      }

      await postCreature({ name: this.creatureName, notes: this.notes });
      this.$router.push({ path: '/', query: { flash: 'suggestion-sent' } });
    }
  }
}
</script>
