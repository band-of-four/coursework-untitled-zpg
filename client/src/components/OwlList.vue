<template>
<div>
  <section v-if="activeOwls.length > 0">
    <h2 class="heading">Активные</h2>
    <div v-for="o in activeOwls">
      <div class="owl-heading">
        <span class="owl-heading__name"># {{ o.name }}</span>
        <span class="owl-heading__count">{{ o.owlCount === 1 ? 'осталась одна' : `осталось ${o.owlCount}` }}</span>
      </div>
      <div>{{ o.description }}</div>
    </div>
  </section>
  <section v-if="availableOwls.length > 0">
    <h2 class="heading">Доступные</h2>
    <div class="owl" v-for="o in availableOwls">
      <div class="owl__main">
        <div class="owl-heading">
          <span class="owl-heading__name"># {{ o.name }}</span>
          <span class="owl-heading__count">{{ o.owlCount === 1 ? 'осталась одна' : `осталось ${o.owlCount}` }}</span>
        </div>
        <div>{{ o.description }}</div>
      </div>
      <div class="owl__side">
        <button class="button" @click.prevent="$emit('select', o.impl)">Применить</button>
      </div>
    </div>
  </section>
</div>
</template>

<script>
export default {
  name: 'OwlList',
  props: {
    owls: { type: Array, required: true }
  },
  computed: {
    activeOwls() {
      return this.owls.filter((o) => o.isActive);
    },
    availableOwls() {
      return this.owls.filter((o) => !o.isActive);
    }
  }
}
</script>
