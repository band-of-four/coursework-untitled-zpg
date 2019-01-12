export default {
  namespaced: true,
  state: {
    name: '',
    durationMs: 0,
    elapsedMs: 0
  },
  actions: {
    init({ commit, dispatch }, ws) {
      return new Promise((resolve) => {
        ws.onopen = () => ws.send('GetStage');
        ws.onmessage = ({ data }) => {
          commit('processMessage', data);
          resolve();
        };
      });
    }
  },
  mutations: {
    processMessage(state, msg) {
      const { type, payload } = JSON.parse(msg);
      if (type === 'StageUpdate') {
        state.name = payload.name;
        state.durationMs = payload.durationMs;
        state.elapsedMs = payload.elapsedMs;
      }
    }
  }
};
