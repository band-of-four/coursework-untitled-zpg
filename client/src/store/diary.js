import { WS_IN_STAGE_UPDATE } from '/api/ws.js';
import { getDiarySections } from '/api/note.js';

export default {
  namespaced: true,
  state: {
    sections: [],
    stale: false,
    page: -1,
    perPage: 10
  },
  actions: {
    async loadNext({ commit, state }) {
      commit('append', await getDiarySections(state.page + 1));
    },
    async refresh({ commit }) {
      commit('reset', await getDiarySections(0));
    }
  },
  mutations: {
    processMessage(state, { type, payload }) {
      if (type === WS_IN_STAGE_UPDATE && payload.updated.includes('Diary') && state.page > -1)
        state.stale = true;
    },
    append(state, sections) {
      state.page += 1;
      state.sections = state.sections.concat(sections);
    },
    reset(state, sections) {
      state.sections = sections;
      state.stale = false;
      state.page = 0;
    }
  }
};
