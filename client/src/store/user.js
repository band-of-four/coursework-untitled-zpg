export default {
  namespaced: true,
  state: {
    isLoggedIn: false,
    hasStudent: false
  },
  actions: {
    async init({ state, commit }) {
      if (state.isLoggedIn) commit('studentMissing');
      else commit('loggedOut');
    },
    async login({ commit, dispatch }, { username, password }) {
      if (username === 'h' && password === 'h') {
        commit('loggedIn');
        await dispatch('init');
        return true;
      }
      return false;
    },
    async createStudent({ commit, dispatch }, student) {
      commit('studentCreated');
    }
  },
  mutations: {
    loggedIn(state) { state.isLoggedIn = true; },
    loggedOut(state) { state.isLoggedIn = false; },
    studentMissing(state) { state.hasStudent = false; },
    studentCreated(state) { state.hasStudent = true; }
  }
};
