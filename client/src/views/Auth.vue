<template>
<div class="sprite-form">
  <div class="sprite-form__sprite">
    <div class="auth-form-sprite"></div>
  </div>
  <div class="sprite-form__form">
    <form class="form" @submit.prevent="submit">
      <div class="logo">Sumaju nikki</div>
      <transition name="fade" mode="out-in">
        <div key=1 v-if="mode === 'signin'">
          <input class="input input--form" type="email" v-model="email" placeholder="Адрес e-mail" />
          <input class="input input--form" type="password" v-model="password" placeholder="Пароль" />
          <button class="button button--form" type="submit">Войти</button>
          <a class="action-link" href="#" @click.prevent="mode = 'signup'">Впервые с нами?</a>
        </div>
        <div key=2 v-else>
          <input class="input input--form" type="email" v-model="email" placeholder="Адрес e-mail" />
          <input class="input input--form" type="password" v-model="password" placeholder="Пароль" />
          <button class="button button--form" type="submit">Познакомиться</button>
          <a class="action-link" href="#" @click.prevent="mode = 'signin'">Мы уже знакомы</a>
        </div>
      </transition>
      <a class="action-link" href="/auth/social/vk">Войти с помощью Вконтакте</a>
      <p class="form-info" v-if="status">{{ status }}</p>
    </form>
  </div>
</div>
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
