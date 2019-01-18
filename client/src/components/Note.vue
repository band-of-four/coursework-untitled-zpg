<template>
<div>
  {{ text }}
  <a href="#" @click.prevent="toggle">
    <span v-if="toggled">разлюбить</span>
    <span v-else>полюбить</span>
  </a>
  <span>({{ hearts }})</span>
</div>
</template>

<script>
import { postHeart } from '/api/note.js';

export default {
  name: 'Note',
  props: {
    id: { type: Number, required: true },
    text: { type: String, required: true },
    initHearts: { type: Number, required: true },
    initToggled: { type: Boolean, required: true }
  },
  data() {
    return {
      hearts: this.initHearts,
      toggled: this.initToggled
    }
  },
  methods: {
    async toggle() {
      const { heartCount, isHearted } = await postHeart(this.id);
      this.hearts = heartCount;
      this.toggled = isHearted;
    }
  }
}
</script>

