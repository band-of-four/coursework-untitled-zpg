<template>
<div>
  <section class="page-section">
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
    <div class="progress">
      <div class="progress__neg-value" :style="{ width: `${100 - progress}%` }"></div>
    </div>
  </section>
  <SpellSection :spells="spells.items" :stale="spells.stale" @refresh="loadSpells" />
  <AttendanceSection :attendance="attendance.items" :stale="attendance.stale" @refresh="loadAttendance" />
</div>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex';
import SpellSection from '/components/SpellSection.vue';
import AttendanceSection from '/components/AttendanceSection.vue';

export default {
  name: 'Overview',
  components: { SpellSection, AttendanceSection },
  created() {
    this.loadSpells();
    this.loadAttendance();
  },
  methods: {
    ...mapActions({ loadSpells: 'spells/load', loadAttendance: 'attendance/load' })
  },
  computed: {
    ...mapState('stage', ['note']),
    ...mapState(['spells', 'attendance']),
    ...mapState('student', ['name', 'level', 'hp']),
    ...mapGetters('stage', ['progress']),
    ...mapGetters('stage', ['progress']),
  }
}
</script>
