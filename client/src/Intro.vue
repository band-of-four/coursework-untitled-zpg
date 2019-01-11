<template>
<div>
  <span>
    Смотрю, ты нашел новую душу в нашу школу, Хранитель? Представь же ее мне.
  </span>
  <button @click="create">Продолжить</button>
</div>
</template>

<script>
export default {
  name: 'Auth',
  components: {},
  methods: {
    async create() {
      await this.$store.dispatch('createStudent', { name: 'Lain', gender: 'Female' });
      this.$router.replace('/');
    }
  }
}
</script>
