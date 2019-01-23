<template>
<main>
  <div class="page-section">
    <div class="page-section-header">
      <div class="page-section-header__icon icon--owl"></div>
      <span class="page-section-header__text page-section-header__text--large">Совы</span>
    </div>
    <section v-if="!dynamicOwlView">
      <section v-show="owls.stale">
        <a href="#" @click.prevent="loadOwls">Обновить</a> 
      </section>
      <section v-show="status.text" :class="{ error: status.isError }">
        {{ status.text }}
        <button @click.prevent="status.text = null">Закрыть</button>
      </section>
      <OwlList :owls="owls.items" @select="apply" />
    </section>
    <component v-else :is="dynamicOwlView" @submit="submitDynamic" @cancel="cancelDynamic" />
  </div>
</main>
</template>

<script>
import { mapActions, mapState } from 'vuex';
import { applyOwl } from '/api/owls.js';

import OwlList from '/components/OwlList.vue';
import DynamicOwlViews from '/components/owls';

export default {
  name: 'Owls',
  components: { OwlList },
  data: () => ({
    status: {
      text: null,
      isError: false
    },
    dynamicOwlView: null
  }),
  created() {
    this.loadOwls();
  },
  computed: mapState(['owls']),
  methods: {
    ...mapActions({ loadOwls: 'owls/load' }),
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
      this.loadOwls();
    }
  }
}
</script>
