import { postSignIn } from '../api/user.js';

export default {
  namespaced: true,
  state: {
    isSignedIn: false,
    hasStudent: false
  },
  actions: {
    async init({ state, commit }) {
      if (state.isSignedIn)
        commit('studentMissing');
    },
    async signIn({ commit, dispatch }, credentials) {
      const success = await postSignIn(credentials);
      if (!success) return false;

      commit('signedIn');
      await dispatch('init');

      return true;
    },
    async createStudent({ commit, dispatch }, student) {
      commit('studentCreated');
    }
  },
  mutations: {
    signedIn(state) { state.isSignedIn = true; },
    studentMissing(state) { state.hasStudent = false; },
    studentCreated(state) { state.hasStudent = true; }
  }
};
