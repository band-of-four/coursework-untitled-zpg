<template>
<div>
  <h1>Отношения</h1>
  <section v-for="r in relationships">
    <strong>{{ r.studentName }}</strong>
    <p>
      Отношения: {{ r.relationship }}, последнее изменение: {{ r.delta }}
    </p>
  </section>
</div>
</template>

<script>
import { getRelationships } from '/api/student.js';

export default {
  name: 'Relationships',
  data: () => ({
    relationships: []
  }),
  async created() {
    this.relationships = await getRelationships();
  }
}
</script>
