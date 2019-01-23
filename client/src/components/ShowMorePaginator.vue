<template>
<section>
  <div v-for="(page, i) in pages">
    <slot v-for="item in page" :item="item"></slot>
    <div class="page-break" v-if="i + 1 < pages.length"></div>
  </div>
  <div class="pagination" v-show="hasMore">
    <a class="button" :class="{ popout: loading }" href="#" @click.prevent="requestMore">Показать больше</a>
  </div>
</section>
</template>

<script>
import { delay } from '/utils.js';

export default {
  name: 'ShowMorePaginator',
  props: {
    items: { type: Array, required: true },
    itemSize: { type: Function, default: () => 1 },
    perPage: { type: Number, required: true },
    page: { type: Number, required: true }
  },
  data() {
    return {
      fullPageCountAfterNextFetch: this.perPage,
      loading: false
    };
  },
  watch: {
    items(newItems, oldItems) {
      this.loading = false;
      if (this.itemCount(newItems) < this.itemCount(oldItems))
        /* Items have been refreshed */
        this.fullPageCountAfterNextFetch = this.perPage;
    }
  },
  computed: {
    hasMore() {
      return this.itemCount(this.items) >= this.fullPageCountAfterNextFetch;
    },
    pages() {
      const pages = [];
      let currPageLen = 0;
      for (let item of this.items) {
        const currPage = pages[pages.length - 1];
        if (!currPage || currPageLen >= this.perPage) {
          pages.push([item]);
          currPageLen = this.itemSize(item);
        }
        else {
          currPage.push(item);
          currPageLen += this.itemSize(item);
        }
      }
      return pages;
    }
  },
  methods: {
    itemCount(list) {
      return list.reduce((acc, item) => acc + this.itemSize(item), 0);
    },
    async requestMore() {
      this.loading = true;
      await delay(400); // wait for the animation to complete

      this.fullPageCountAfterNextFetch += this.perPage;
      this.$emit('show-more');
    }
  }
}
</script>
