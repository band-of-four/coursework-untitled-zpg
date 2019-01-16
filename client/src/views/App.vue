<template>
  <section v-if="loading">Загрузка...</section>
  <section v-else>
    <router-link to="/">Обзор</router-link>
    <router-link to="/diary">Дневник</router-link>
    <router-link to="/suggest">Предложить</router-link>
    <router-view></router-view>
  </section>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'App',
  computed: mapState(['loading'])
}
</script>
