<template>
<div>
  <span>
    Смотрю, ты нашел новую душу в нашу школу, Хранитель? Представь же ее мне.
  </span>
  <form @submit.prevent="create">
    <input type="text" v-model="name" placeholder="Имя персонажа">
    <br>
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
