import Vue from 'vue';
import VueRouter from 'vue-router';

import Auth from '/views/Auth.vue';
import Intro from '/views/Intro.vue';
import Overview from '/views/Overview.vue';
import Diary from '/views/Diary.vue';
import Suggestions from '/views/Suggestions.vue';

Vue.use(VueRouter);

export default function (store) {
  const router = new VueRouter({
    routes: [
      { path: '/auth', component: Auth },
      { path: '/intro', component: Intro },
      { path: '/', component: Overview },
      { path: '/diary', component: Diary },
      { path: '/suggest', component: Suggestions },
    ]
  });

  router.beforeEach((to, from, next) => {
    if (!store.state.signedIn) {
      if (to.path === '/auth') next();
      else next('/auth');
    }
    else if (!store.state.student) {
      if (to.path === '/intro') next();
      else next('/intro');
    }
    else if (to.path === '/auth' || to.path === '/intro') {
      next(false);
    }
    else next();
  });

  return router;
};
