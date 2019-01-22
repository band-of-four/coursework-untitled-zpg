<template>
<div>
  <section class="page-section">
    <h1 class="page-section__heading">{{ name }}, {{ level }}-й год</h1>
    <div class="stage-note">
      <div class="stage-note__heading">
        {{ stageNote.subject }} — {{ stageNote.label }}
      </div>
      <div class="stage-note__body">
        <div>{{ stageNote.text }}</div>
        <div class="stage-note__heart heart fa-heart-mask"></div>
      </div>
    </div>
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
    ...mapState(['spells', 'attendance']),
    ...mapState('student', ['name', 'level', 'hp']),
    ...mapGetters('stage', ['progress']),
    stageNote() {
      const note = this.$store.state.stage.note;

      const label = note.stage === 'Travel' ? 'портреты на стенах'
                  : note.stage === 'Fight' ? 'шум в коридоре!'
                  : note.stage === 'Lesson' ? 'слева от парты распахнуто окно'
                  : note.stage === 'Club' ? 'интересно...'
                  : note.stage === 'Library' ? 'где-то в кармане был читательский билет'
                  : note.stage === 'Infirmary' ? 'вижу свет' : '';

      const subject = note.creature || note.lesson || note.club || 'Школа';

      return { label, subject, text: note.text };
    }
  }
}
</script>
