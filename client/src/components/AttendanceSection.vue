<template>
  <section class="page-section">
  <h1>Зачетная книжка</h1>
  <section v-show="stale">
    <a href="#" @click.prevent="$emit('refresh')">Обновить</a> 
  </section>
  <div class="resource-item" v-for="att in renderedAttendance">
    <div>{{ att.lesson }}</div>
    <div class="resource-item__stats">{{ att.progress }}%</div>
  </div>
</section>
</template>

<script>
export default {
  name: 'AttendanceSection',
  props: {
    attendance: { type: Array, required: true },
    stale: { type: Boolean, required: true }
  },
  computed: {
    renderedAttendance() {
      return this.attendance.map(({ lesson, attended, requiredAttendance }) => {
        return { lesson, progress: Math.round(attended / requiredAttendance * 100) };
      });
    }
  }
}
</script>

