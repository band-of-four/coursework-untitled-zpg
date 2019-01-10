<template>
<div>
  <span>
    Смотрю, ты нашел новую душу в нашу школу, Хранитель? Представь же ее мне.
  </span>
  <button @click="create">Продолжить</button>
</div>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'Auth',
  components: {},
  methods: {
    create() {
      this.createStudent({}).then(() =>
        this.$router.replace('/'));
    },
    ...mapActions('user', ['createStudent'])
  }
}
</script>
