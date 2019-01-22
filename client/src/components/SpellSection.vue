<template>
  <section class="page-section">
  <h1 class="heading">Заклинания</h1>
  <section v-show="stale">
    <a href="#" @click.prevent="$emit('refresh')">Обновить</a> 
  </section>
  <div class="resource-item" v-for="spell in renderedSpells">
    <div>{{ spell.name }}</div>
    <div class="resource-item__stats">{{ spell.stats }}</div>
  </div>
</section>
</template>

<script>
export default {
  name: 'SpellSection',
  props: {
    spells: { type: Array, required: true },
    stale: { type: Boolean, required: true }
  },
  computed: {
    renderedSpells() {
      return this.spells.map(({ name, power, kind }) => {
        const appliesTo = kind === 'Attack' ? 'атаке'
                        : kind === 'Defence' ? 'защите'
                        : kind === 'Luck' ? 'удаче'
                        : kind === 'Charisma' ? 'харизме' : '?';
        const mod = power > 0 ? `+${power}` : power;

        return { name, stats: `${mod} к ${appliesTo}` };
      });
    }
  }
}
</script>

