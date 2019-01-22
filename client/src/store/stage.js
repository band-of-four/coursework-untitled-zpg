import { WS_IN_STAGE_UPDATE } from '/api/ws.js';

const PROGRESS_TICK_MS = 10000;

export default {
  namespaced: true,
  state: {
    note: null,
    durationMs: 0,
    elapsedMs: 0
  },
  getters: {
    progress({ elapsedMs, durationMs }) {
      return Math.min(100, Math.round((elapsedMs / durationMs) * 100));
    }
  },
  actions: {
    init({ commit }) {
      setInterval(() => commit('tickElapsed'), PROGRESS_TICK_MS);
    }
  },
  mutations: {
    processMessage(state, { type, payload }) {
      if (type === WS_IN_STAGE_UPDATE) {
        state.note = payload.note;
        state.durationMs = payload.stageDuration;
        state.elapsedMs = payload.stageElapsed;
      }
    },
    tickElapsed(state) { state.elapsedMs += PROGRESS_TICK_MS; }
  }
};
