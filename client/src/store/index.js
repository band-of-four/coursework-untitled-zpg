import Vue from 'vue';
import Vuex from 'vuex';

import { API_UNAUTHENTICATED } from '../api';
import { getStudent, STUDENT_NOT_CREATED } from '../api/student.js';
import { postSignIn } from '../api/auth.js';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    error: false,
    loading: true,
    signedIn: false,
    student: null
  },
  actions: {
    async load({ commit }) {
      try {
        const student = await getStudent();
      }
      catch (e) {
        if (e === API_UNAUTHENTICATED)
          commit('signedOut');
        else if (e === STUDENT_NOT_CREATED) {
          commit('signedIn');
          commit('studentNotCreated');
        }
        else
          commit('apiError');
      }
    },
    async signIn({ commit, dispatch }, credentials) {
      const success = await postSignIn(credentials);
      if (!success) return false;

      await dispatch('load');

      return true;
    },
  },
  mutations: {
    apiError(state) {
      state.loading = false;
      state.error = true;
    },
    signedOut(state) {
      state.loading = false;
      state.signedIn = false;
    },
    studentNotCreated(state) {
      state.loading = false;
      state.student = null;
    },
    signedIn(state) { state.signedIn = true; },
    loaded(state) { state.loading = false; }
  }
});
