<template>
  <div v-if="loading">Загрузка...</div>
  <div v-else class="content">
    <section v-if="signedIn && student">
      <router-link to="/">Обзор</router-link>
      <router-link to="/diary">Дневник</router-link>
      <router-link to="/owls">Совы</router-link>
      <router-link to="/relationships">Отношения</router-link>
      <router-link to="/skills">Навыки</router-link>
      <router-link to="/suggest">Предложить</router-link>
      <a href="/auth/signout">Выйти</a>
    </section>
    <router-view></router-view>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'App',
  computed: mapState(['loading', 'signedIn', 'student'])
}
</script>
