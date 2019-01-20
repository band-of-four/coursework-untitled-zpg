<template>
<section>
  <slot></slot>
  <a v-show="hasMore" href="#" @click.prevent="requestMore">Показать больше</a>
</section>
</template>

<script>
export default {
  name: 'ShowMorePaginator',
  props: {
    itemCount: { type: Number, required: true },
    perPage: { type: Number, required: true },
    page: { type: Number, required: true }
  },
  data() {
    return {
      fullPageCountAfterNextFetch: this.perPage
    };
  },
  watch: {
    itemCount(newCount, oldCount) {
      if (newCount < oldCount)
        /* Items have been refreshed */
        this.fullPageCountAfterNextFetch = this.perPage;
    }
  },
  computed: {
    hasMore() {
      return this.itemCount >= this.fullPageCountAfterNextFetch;
    }
  },
  methods: {
    requestMore() {
      this.fullPageCountAfterNextFetch += this.perPage;
      this.$emit('show-more');
    }
  }
}
</script>
