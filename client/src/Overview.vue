<template>
<div>
  <h1>{{ name }}, {{ level }}-й год</h1>
  <span>Прогресс: {{ progress }}</span>
  <h2>Заклинания:</h2>
  <ul>
    <li v-for="spell in spells">
      {{ spell.name }}: вид {{ spell.kind }}, сила {{ spell.power }}
    </li>
  </ul>
</div>
</template>

<script>
import { mapState, mapGetters } from 'vuex';

export default {
  name: 'Overview',
  computed: {
    ...mapState('student', ['name', 'level', 'hp', 'spells']),
    ...mapGetters('stage', ['progress'])
  }
}
</script>
