import Vue from 'vue';

import store from './store';
import router from './router.js';
import App from './App.vue';

new Vue({
  el: '#app',
  components: { App },
  render: (h) => h(App),
  router: router(store),
  store
});
