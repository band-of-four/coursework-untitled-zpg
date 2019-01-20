import { WS_IN_STAGE_UPDATE } from '/api/ws.js';

export default ({ name, level, hp }) => ({
  namespaced: true,
  state: {
    name,
    level,
    hp
  },
  mutations: {
    processMessage(state, { type, payload }) {
      if (type === WS_IN_STAGE_UPDATE) {
        state.hp = payload.hp;
        state.level = payload.level;
      }
    }
  }
});
