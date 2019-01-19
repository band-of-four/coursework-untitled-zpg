<template>
<div>
  <h1>Совы</h1>
  <section v-show="status.text" :class="{ error: status.isError }">
    {{ status.text }}
    <button @click.prevent="status.text = null">Закрыть</button>
  </section>
  <OwlList v-if="!dynamicOwlView" :owls="owls" @select="apply" />
  <component v-else :is="dynamicOwlView" @submit="submitDynamic" @cancel="cancelDynamic" />
</div>
</template>

<script>
import { getOwls, applyOwl } from '/api/owls.js';

import OwlList from '/components/OwlList.vue';
import DynamicOwlViews from '/components/owls';

export default {
  name: 'Owls',
  components: { OwlList },
  data: () => ({
    owls: [],
    status: {
      text: null,
      isError: false
    },
    dynamicOwlView: null
  }),
  created() {
    this.refreshOwls();
  },
  methods: {
    async refreshOwls() {
      this.owls = await getOwls();
    },
    apply(impl) {
      const dynamic = DynamicOwlViews[impl];
      if (dynamic)
        this.dynamicOwlView = dynamic;
      else
        this.submit(impl, {});
    },
    cancelDynamic() {
      this.dynamicOwlView = null;
    },
    submitDynamic(payload) {
      const impl = this.dynamicOwlView.name;
      this.dynamicOwlView = null;
      this.submit(impl, payload);
    },
    async submit(impl, payload) {
      const response = await applyOwl(impl, payload);
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
