<template>
<button @click="login">Login</button>
</template>

<script>
export default {
  name: 'Auth',
  components: {},
  methods: {
    login() {
      this.$store.commit('login');
    }
  }
}
</script>
