<template>
  <router-view v-if="$route.matched.length"></router-view>
  <section v-else-if="!isSignedIn">
    <router-link to="/auth">Войти</router-link>
  </section>
  <section v-else>
    <span>Logged in!</span>
  </section>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'App',
  computed: mapState({
    isSignedIn: ({ user }) => user.isSignedIn
  })
}
</script>
