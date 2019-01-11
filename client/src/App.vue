<template>
  <section v-if="loading">Загрузка...</section>
  <section v-else-if="!signedIn">
    <router-link to="/auth">Войти</router-link>
  </section>
  <router-view v-else-if="$route.matched.length"></router-view>
  <section v-else>
    <span>Страница персонажа</span>
  </section>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'App',
  computed: mapState(['loading', 'signedIn'])
}
</script>
