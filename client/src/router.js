import Vue from 'vue';
import VueRouter from 'vue-router';

import Auth from './Auth.vue';

Vue.use(VueRouter);

export default new VueRouter({
  mode: 'history',
  routes: [
    { path: '/auth', component: Auth }
  ]
});
