import { WS_IN_STAGE_UPDATE } from '/api/ws.js';

export const paginatedStore = (resource, providerFunction) => ({
  namespaced: true,
  state: {
    items: [],
    stale: false,
    page: -1,
    perPage: 10
  },
  actions: {
    async loadNext({ commit, state }) {
      commit('append', await providerFunction(state.page + 1));
    },
    async refresh({ commit }) {
      commit('reset', await providerFunction(0));
    }
  },
  mutations: {
    processMessage(state, { type, payload }) {
      if (type === WS_IN_STAGE_UPDATE && payload.updated.includes(resource) && state.page > -1)
        state.stale = true;
    },
    append(state, items) {
      state.page += 1;
      state.items = state.items.concat(items);
    },
    reset(state, items) {
      state.items = items;
      state.stale = false;
      state.page = 0;
    }
  }
});
