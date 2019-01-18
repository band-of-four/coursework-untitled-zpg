<template>
<div>
  <h1>Совы</h1>
  <section v-if="activeOwls.length > 0">
    <h3>Активные</h3>
    <div v-for="o in activeOwls">
      {{ o.name }} ~ {{ o.owlCount }}
      <p>{{ o.description }}</p>
    </div>
  </section>
  <section v-if="availableOwls.length > 0">
    <h3>Доступные</h3>
    <div v-for="o in availableOwls">
      {{ o.name }} ~ {{ o.owlCount }}
      <p>{{ o.description }}</p>
    </div>
  </section>
</div>
</template>

<script>
import { getOwls } from '/api/owls.js';

export default {
  name: 'Owls',
  data: () => ({
    activeOwls: [],
    availableOwls: []
  }),
  created() {
    getOwls().then((owls) => {
      this.activeOwls = owls.filter((o) => o.isActive);
      this.availableOwls = owls.filter((o) => !o.isActive);
    });
  },
  computed: {

  }
}
</script>
