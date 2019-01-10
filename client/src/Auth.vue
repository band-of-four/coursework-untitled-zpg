<template>
<form @submit.prevent="submit">
  <input type="email" v-model="email" placeholder="Адрес e-mail" />
  <input type="password" v-model="password" placeholder="Пароль" />
  <button type="submit">Войти</button>
  <p v-if="status">{{ status }}</p>
</form>
</template>

<script>
export default {
  name: 'Auth',
  data: () => ({
    email: '',
    password: '',
    status: null
  }),
  components: {},
  methods: {
    async submit() {
      this.status = 'Загрузка...';

      const success = await this.$store.dispatch('user/signIn',
        { email: this.email, password: this.password });

      if (success) {
        this.$store.commit('user/signedIn');
        this.$router.replace('/');
      }
      else
        this.status = 'Неверный логин или пароль';
    }
  }
}
</script>
