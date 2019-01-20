import { WS_IN_STAGE_UPDATE } from '/api/ws.js';
import { getSpells } from '/api/student.js';

export default {
  namespaced: true,
  state: {
    items: [],
    stale: false
  },
  actions: {
    async load({ commit }) {
      commit('set', await getSpells());
    }
  },
  mutations: {
    processMessage(state, { type, payload }) {
      if (type === WS_IN_STAGE_UPDATE && payload.updated.includes('Spells') && state.items.length > 0)
        state.stale = true;
    },
    set(state, items) {
      state.items = items;
      state.stale = false;
    }
  }
};
