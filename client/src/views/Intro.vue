<template>
<div class="sprite-form">
  <div class="sprite-form__sprite">
    <div class="intro-form-sprite"></div>
  </div>
  <div class="sprite-form__form">
    <form class="form" @submit.prevent="create">
      <p class="form-info">Смотрю, ты нашел новую душу в нашу школу, Хранитель? Представь же ее мне.</p>
      <input class="input input--form" type="text" v-model="name" placeholder="Имя персонажа">
      <div class="form-radiobutton-group">
        <label class="radiobutton__label">
          <input type="radio" class="radiobutton__input" id="female" value="Female" v-model="gender" />
          <span class="radiobutton__button">
            <span class="radiobutton__icon fa-venus-mask"></span>
          </span>
        </label>
        <label class="radiobutton__label">
          <input type="radio" class="radiobutton__input" id="male" value="Male" v-model="gender"/>
          <span class="radiobutton__button">
            <span class="radiobutton__icon fa-mars-mask"></span>
          </span>
        </label>
      </div>
      <button class="button button--form" @click="create">Продолжить</button>
      <p class="form-info" v-if="status">{{ status }}</p>
    </form>
  </div>
</div>
</template>

<script>
import { STUDENT_NAME_TAKEN } from '/api/student.js';

export default {
  name: 'Intro',
  data: () => ({
    name: '',
    gender: 'Female',
    status: null
  }),
  components: {},
  methods: {
    async create() {
      const result = await this.$store.dispatch('createStudent', { name: this.name, gender: this.gender });
      if (result === STUDENT_NAME_TAKEN)
        this.status = 'Имя персонажа уже занято, придумай другое ;)';
      else {
        this.$router.replace('/');
        this.$store.commit('loaded');
      }
    }
  }
}
</script>
