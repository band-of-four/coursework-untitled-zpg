<template>
<div>
  <span>
    Смотрю, ты нашел новую душу в нашу школу, Хранитель? Представь же ее мне.
  </span>
  <form @submit.prevent="create">
    <input type="text" v-model="name" placeholder="Имя персонажа">
    <br>
    <input type="radio" id="female" value="Female" v-model="gender">
    <label for="female">Женщина</label>
    <br>
    <input type="radio" id="male" value="Male" v-model="gender">
    <label for="male">Мужчина</label>
    <br>
  </form>
  <button @click="create">Продолжить</button>
</div>
</template>

<script>
export default {
  name: 'Intro',
  data: () => ({
    name: '',
    gender: 'Female'
  }),
  components: {},
  methods: {
    async create() {
      await this.$store.dispatch('createStudent', { name: this.name, gender: this.gender });
      this.$router.replace('/');
    }
  }
}
</script>
