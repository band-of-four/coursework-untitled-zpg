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
  <section v-show="spells.stale">
    <a href="#" @click.prevent="loadSpells">Обновить</a> 
  </section>
  <ul>
    <li v-for="spell in spells.items">
      {{ spell.name }}: вид {{ spell.kind }}, сила {{ spell.power }}
    </li>
  </ul>
</div>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex';

export default {
  name: 'Overview',
  created() {
    this.loadSpells();
  },
  methods: {
    ...mapActions({ loadSpells: 'spells/load' })
  },
  computed: {
    ...mapState('stage', ['note']),
    ...mapState(['spells']),
    ...mapState('student', ['name', 'level', 'hp']),
    ...mapGetters('stage', ['progress']),
    ...mapGetters('stage', ['progress']),
  }
}
</script>
