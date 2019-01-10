<template>
<Auth v-if="!isLoggedIn" />
</template>

<script>
import { mapState } from 'vuex'

import Auth from './Auth.vue';

export default {
  name: 'App',
  components: { Auth },
  computed: mapState([
    'isLoggedIn'
  ])
}
</script>
