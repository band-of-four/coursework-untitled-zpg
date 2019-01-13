<template>
<div>
  <h1>{{ name }}, {{ level }}-й год</h1>
  <section v-if="note.stage === 'Travel'">
    Портреты на стенах
    <div>"{{ note.text }}"</div>
  </section>
  <section v-else-if="note.stage === 'Fight'">
    Шум в коридоре
    <div>{{ note.creature }}</div>
    <div>"{{ note.text }}"</div>
  </section>
  <section v-else-if="note.stage === 'Lesson'">
    Слева от парты распахнуто окно
    <div>{{ note.lesson }}</div>
    <div>"{{ note.text }}"</div>
  </section>
  <section v-else-if="note.stage === 'Infirmary'">
    Прививки
    <div>"{{ note.text }}"</div>
  </section>
  <section v-else-if="note.stage === 'Library'">
    Где-то в кармане был читательский билет
    <div>"{{ note.text }}"</div>
  </section>
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
    ...mapState('stage', ['note']),
    ...mapState('student', ['name', 'level', 'hp', 'spells']),
    ...mapGetters('stage', ['progress'])
  }
}
</script>
