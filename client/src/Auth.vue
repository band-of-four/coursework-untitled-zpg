<template>
<button @click="login">Login</button>
</template>

<script>
export default {
  name: 'Auth',
  components: {},
  methods: {
    login() {
      this.$store.dispatch('user/login', { username: 'h', password: 'h' }).then((res) =>
        res && this.$router.replace('/'));
    }
  }
}
</script>
