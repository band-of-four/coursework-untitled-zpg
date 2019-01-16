<template>
<div>
  <h1>Предложить</h1>
  <router-link to="/bar">Урок</router-link>
  <section v-for="entry in entries">
    {{ entry }}
  </section>
</div>
</template>

<script>
export default {
  name: 'Suggestions'
}
</script>
