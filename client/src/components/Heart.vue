<template>
<div>
  <span class="heart fa-heart-mask" :class="{ set }" @click.prevent="toggle"></span>
  <span class="heart-count" v-if="hearts > 0">{{ hearts }}</span>
</div>
</template>

<script>
import { postHeart } from '/api/note.js';

export default {
  name: 'Heart',
  props: {
    id: { type: Number, required: true },
    initHearts: { type: Number, required: true },
    initSet: { type: Boolean, required: true }
  },
  data() {
    return {
      hearts: this.initHearts,
      set: this.initSet
    }
  },
  methods: {
    async toggle() {
      const { heartCount, isHearted } = await postHeart(this.id);
      this.hearts = heartCount;
      this.set = isHearted;
    }
  }
}
</script>

