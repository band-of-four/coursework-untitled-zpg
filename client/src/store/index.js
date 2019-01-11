import Vue from 'vue';
import Vuex from 'vuex';

import { API_UNAUTHENTICATED } from '../api';
import { getStudent, postStudent, STUDENT_NOT_CREATED } from '../api/student.js';
import { postSignIn, postSignUp } from '../api/auth.js';

import studentModule from './student.js';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    error: false,
    loading: true,
    signedIn: true,
    student: null
  },
  actions: {
    async load({ commit, dispatch }) {
      try {
        return await dispatch('initState', await getStudent());
      }
      catch (e) {
        if (e === API_UNAUTHENTICATED)
          commit('signedOut');
        else if (e === STUDENT_NOT_CREATED)
          commit('studentNotCreated');
        else
          commit('apiError');
      }
    },
    async signUp({ commit, dispatch }, credentials) {
      try {
        return await postSignUp(credentials);
      }
      catch (e) {
        commit('apiError');
      }
    },
    async signIn({ commit, dispatch }, credentials) {
      const success = await postSignIn(credentials);
      if (!success) return false;

      await dispatch('load');

      return true;
    },
    async createStudent({ commit, dispatch }, student) {
      return await dispatch('initState', await postStudent(student));
    },
    async initState({ commit }, student) {
      this.registerModule('student', studentModule(student));
      commit('loaded');
    }
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
