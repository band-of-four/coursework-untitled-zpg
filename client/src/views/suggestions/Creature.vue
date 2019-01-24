<template>
<main>
  <div class="page-section">
    <div class="page-section-header">
      <div class="page-section-header__icon icon--ball"></div>
      <span class="page-section-header__text page-section-header__text--large">
        Предложить нового монстра
      </span>
    </div>
    <p class="suggest-form-info">
      Создание своего монстра — занятие увлекательное, но непростое. Помимо имени,
      тебе нужно придумать по три заметки (для боя, победы, и поражения) для
      персонажей мужского и женского пола.
    </p>
    <form @submit.prevent="submit">
      <input type="text" class="input input--wide" v-model="creatureName" placeholder="Имя монстра" />

      <div v-for="(n, i) in notes">
        <br v-if="i == 0"><!-- shh, don't tell anyone -->
        <p class="suggest-creature-note-label">
          {{ noteLabel(n) }}
          <a href="#" class="action-link action-link--inline" v-if="i >= 6" @click="removeNote(i)">(удалить)</a>
        </p>
        <p class="suggest-form-info" v-show="n.error">{{ n.error }}</p>
        <textarea class="input input--note-text input--note-text--compact" v-model="n.text" placeholder="Минимум 3 символа"></textarea>
      </div>
      <button class="button" type="submit">Отправить</button>
      <p class="suggest-form-info">{{ error }}</p>
      <p class="suggest-form-info">Ты можешь добавить еще пару заметок, чтобы сделать монстра интереснее:</p>
      <div>
        <a href="#" class="action-link action-link--left" @click.prevent="addNote('Fight', 'Female')">+ девочки, бой</a>
        <a href="#" class="action-link action-link--left" @click.prevent="addNote('Fight', 'Male')">+ мальчики, бой</a>
        <a href="#" class="action-link action-link--left" @click.prevent="addNote('FightWon', 'Female')">+ девочки, победа</a>
        <a href="#" class="action-link action-link--left" @click.prevent="addNote('FightWon', 'Male')">+ мальчики, победа</a>
        <a href="#" class="action-link action-link--left" @click.prevent="addNote('FightLost', 'Female')">+ девочки, поражение</a>
        <a href="#" class="action-link action-link--left" @click.prevent="addNote('FightLost', 'Male')">+ мальчики, поражение</a>
      </div>
    </form>
  </div>
</main>
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
    addNote(stage, gender) {
      this.notes.push({ text: '', stage, gender });
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
    },
    noteLabel({ gender, stage }) {
      const genderText = gender === 'Female' ? 'девочки' : 'мальчики',
            stageText = stage === 'Fight' ? 'дерутся с монстром'
                      : stage === 'FightWon' ? 'побеждают монстра'
                      : stage === 'FightLost' ? 'падают от руки монстра' : '';

      return `Когда ${genderText} ${stageText}, они говорят:`;
    }
  }
}
</script>
