const PROGRESS_TICK_MS = 10000;

export default {
  namespaced: true,
  state: {
    kind: '',
    durationMs: 0,
    elapsedMs: 0
  },
  getters: {
    progress({ elapsedMs, durationMs }) {
      return Math.round((elapsedMs / durationMs) * 100);
    }
  },
  actions: {
    init({ commit, dispatch }, ws) {
      return new Promise((resolve) => {
        ws.onopen = () => ws.send('GetStage');
        ws.onmessage = ({ data }) => {
          commit('processMessage', data);
          dispatch('tickProgress');
          resolve();
        };
      });
    },
    tickProgress({ commit, state }) {
      setInterval(() => commit('tickElapsed'), PROGRESS_TICK_MS);
    }
  },
  mutations: {
    processMessage(state, msg) {
      const { type, payload } = JSON.parse(msg);
      if (type === 'StageUpdate') {
        state.kind = payload.name;
        state.durationMs = payload.durationMs;
        state.elapsedMs = payload.elapsedMs;
      }
    },
    tickElapsed(state) { state.elapsedMs += PROGRESS_TICK_MS; }
  }
};
