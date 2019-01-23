<template>
  <div class="loading" v-if="loading"></div>
  <div v-else>
    <header class="header" v-if="signedIn && student">
      <div class="header__content">
        <router-link class="header-logo" to="/">Sumaju nikki</router-link>
        <router-link class="header-link" to="/diary">Дневник</router-link>
        <router-link class="header-link" to="/owls">Совы</router-link>
        <router-link class="header-link" to="/relationships">Отношения</router-link>
        <router-link class="header-link" to="/skills">Навыки</router-link>
        <router-link class="header-link" to="/suggest">Предложить</router-link>
        <a class="header-link header-link--signout" href="/auth/signout">Выйти</a>
      </div>
    </header>
    <router-view :class="{ content: signedIn && student }"></router-view>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'App',
  computed: mapState(['loading', 'signedIn', 'student'])
}
</script>
