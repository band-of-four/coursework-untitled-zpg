import { WS_IN_STAGE_UPDATE } from '../api/ws.js';

export default ({ name, level, hp }, spells) => ({
  namespaced: true,
  state: {
    name, level, hp, spells
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
