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
    perPage: { type: Number, required: true }
  },
  data() {
    return {
      page: 0,
      fullPageCountAfterNextFetch: this.perPage
    };
  },
  computed: {
    hasMore() {
      return this.itemCount >= this.fullPageCountAfterNextFetch;
    }
  },
  methods: {
    requestMore() {
      this.page += 1;
      this.fullPageCountAfterNextFetch += this.perPage;
      this.$emit('next-page', this.page);
    }
  }
}
</script>
