import { getState, postSignIn, USER_STATE_MISSING, USER_AUTH_REQUIRED } from '../api/user.js';

export default {
  namespaced: true,
  state: {
    isSignedIn: false,
    hasProfile: false
  },
  actions: {
    async initState({ commit }) {
      const state = await getState();
      if (state === USER_AUTH_REQUIRED)
        commit('signedOut');
      else if (state === USER_STATE_MISSING) {
        commit('signedIn');
        commit('profileMissing');
      }
      else
        commit('initialized');
    },
    async signIn({ commit, dispatch }, credentials) {
      const success = await postSignIn(credentials);
      if (!success) return false;

      await dispatch('initState');

      return true;
    },
    async createStudent({ commit, dispatch }, student) {
      await dispatch('initState');
    }
  },
  mutations: {
    signedOut(state) { state.isSignedIn = false; },
    signedIn(state) { state.isSignedIn = true; },
    profileMissing(state) { state.hasProfile = false; },
    initialized(state) {
      state.isSignedIn = true;
      state.hasProfile = true;
    }
  }
};
