<template>
<form @submit.prevent="submit">
  <input type="email" v-model="email" placeholder="Адрес e-mail" />
  <input type="password" v-model="password" placeholder="Пароль" />
  <template v-if="mode === 'signin'">
    <button type="submit">Войти</button>
    <button @click.prevent="mode = 'signup'">Впервые с нами?</button>
  </template>
  <template v-else>
    <button type="submit">Познакомиться</button>
    <button @click.prevent="mode = 'signin'">Мы уже знакомы</button>
  </template>
  <a href="/auth/social/vk">Войти с помощью Вконтакте</a>
  <p v-if="status">{{ status }}</p>
</form>
</template>

<script>
export default {
  name: 'Auth',
  data: () => ({
    email: '',
    password: '',
    status: null,
    mode: 'signin'
  }),
  components: {},
  methods: {
    async submit() {
      this.status = 'Загрузка...';
      if (this.mode === 'signup') {
        const succUp = await this.$store.dispatch('signUp',
          {email: this.email, password: this.password });
        if (!succUp) {
          this.status = 'Email занят';
          return;
        }
      }
 
      const success = await this.$store.dispatch('signIn',
        { email: this.email, password: this.password });

      if (success) {
        this.$store.commit('signedIn');
        this.$router.replace('/');
      }
      else
        this.status = 'Неверный логин или пароль';
    }
  }
}
</script>
