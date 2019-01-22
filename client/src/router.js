import Vue from 'vue';
import VueRouter from 'vue-router';

import Auth from '/views/Auth.vue';
import Intro from '/views/Intro.vue';
import Overview from '/views/Overview.vue';
import Diary from '/views/Diary.vue';
import Owls from '/views/Owls.vue';
import Relationships from '/views/Relationships.vue';
import Skills from '/views/Skills.vue';
import Suggestions from '/views/Suggestions.vue';
import SuggestionsLesson from '/views/suggestions/Lesson.vue';
import SuggestionsCreature from '/views/suggestions/Creature.vue';
import SuggestionsClub from '/views/suggestions/Club.vue';

Vue.use(VueRouter);

export default function (store) {
  const router = new VueRouter({
    routes: [
      { path: '/auth', component: Auth },
      { path: '/intro', component: Intro },
      { path: '/', component: Overview },
      { path: '/diary', component: Diary },
      { path: '/owls', component: Owls },
      { path: '/relationships', component: Relationships },
      { path: '/skills', component: Skills },
      { path: '/suggest', component: Suggestions },
      { path: '/suggest/lesson', component: SuggestionsLesson },
      { path: '/suggest/creature', component: SuggestionsCreature },
      { path: '/suggest/club', component: SuggestionsClub }
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
