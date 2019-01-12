<template>
  <section v-if="loading">Загрузка...</section>
  <router-view v-else></router-view>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'App',
  computed: mapState(['loading'])
}
</script>
