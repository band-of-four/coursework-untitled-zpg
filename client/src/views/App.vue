<template>
  <section v-if="loading">Загрузка...</section>
  <section v-else>
    <section v-if="signedIn && student">
      <router-link to="/">Обзор</router-link>
      <router-link to="/diary">Дневник</router-link>
      <router-link to="/suggest">Предложить</router-link>
      <a href="/auth/signout">Выйти</a>
    </section>
    <router-view></router-view>
  </section>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'App',
  computed: mapState(['loading', 'signedIn', 'student'])
}
</script>
