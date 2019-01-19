<template>
<div>
  <h1>Совы</h1>
  <section v-show="status.text" :class="{ error: status.isError }">
    {{ status.text }}
    <button @click.prevent="status.text = null">Закрыть</button>
  </section>
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
      <button @click.prevent="apply(o.id)">Применить</button>
    </div>
  </section>
</div>
</template>

<script>
import { getOwls, applyOwl } from '/api/owls.js';

export default {
  name: 'Owls',
  data: () => ({
    activeOwls: [],
    availableOwls: [],
    status: {
      text: null,
      isError: false
    }
  }),
  created() {
    this.refreshOwls();
  },
  methods: {
    refreshOwls() {
      getOwls().then((owls) => {
        this.activeOwls = owls.filter((o) => o.isActive);
        this.availableOwls = owls.filter((o) => !o.isActive);
      });
    },
    async apply(id) {
      const response = await applyOwl(id, {}); // TODO: implement custom payloads smh?
      if (response.status === 'failed') {
        this.status.isError = true;
        this.status.text = response.message || 'Не удалось применить сову, попробуй еще раз.';
      }
      else {
        this.status.isError = false;
        this.status.text = response.message || 'Еще одна сова отправилась в полет... Пожелаем ей удачи.';
      }
      this.refreshOwls();
    }
  }
}
</script>
