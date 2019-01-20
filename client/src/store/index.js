import Vue from 'vue';
import Vuex from 'vuex';

import { API_UNAUTHENTICATED } from '/api';
import { getStudent, postStudent, STUDENT_NOT_CREATED } from '/api/student.js';
import { postSignIn, postSignUp } from '/api/auth.js';
import { WS_URI, WS_OUT_GET_STAGE } from '/api/ws.js';

import studentModule from './student.js';
import stageModule from './stage.js';
import spellsModule from './spells.js';
import attendanceModule from './attendance.js';
import diaryModule from './diary.js';
import relationshipsModule from './relationships.js';
import skillsModule from './skills.js';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    error: false,
    loading: true,
    signedIn: true,
    ws: null
  },
  actions: {
    async load({ commit, dispatch }) {
      try {
        return await dispatch('initState', await getStudent());
      }
      catch (e) {
        if (e === API_UNAUTHENTICATED)
          commit('signedOut');
        else if (e === STUDENT_NOT_CREATED)
          commit('studentNotCreated');
        else
          commit('apiError');
      }
    },
    async signUp({ commit, dispatch }, credentials) {
      try {
        return await postSignUp(credentials);
      }
      catch (e) {
        commit('apiError');
      }
    },
    async signIn({ commit, dispatch }, credentials) {
      const success = await postSignIn(credentials);
      if (!success) return false;

      await dispatch('load');

      return true;
    },
    async createStudent({ commit, dispatch }, student) {
      return await dispatch('initState', await postStudent(student));
    },
    async initState({ commit, dispatch }, student) {
      this.registerModule('student', studentModule(student));
      this.registerModule('spells', spellsModule);
      this.registerModule('attendance', attendanceModule);
      this.registerModule('diary', diaryModule);
      this.registerModule('relationships', relationshipsModule);
      this.registerModule('skills', skillsModule);

      this.registerModule('stage', stageModule);
      dispatch('stage/init');

      const ws = new WebSocket(WS_URI);
      await new Promise((resolve) => {
        ws.onopen = () => ws.send(WS_OUT_GET_STAGE);
        ws.onmessage = ({ data }) => {
          ws.onmessage = ({ data }) => dispatch('handleWsMessage', data);
          dispatch('handleWsMessage', data);
          resolve();
        };
      });
      commit('wsAcquired', ws);
    },
    handleWsMessage({ commit }, data) {
      const message = JSON.parse(data);
      commit('stage/processMessage', message);
      commit('student/processMessage', message);
      commit('spells/processMessage', message);
      commit('attendance/processMessage', message);
      commit('diary/processMessage', message);
      commit('relationships/processMessage', message);
      commit('skills/processMessage', message);
    }
  },
  mutations: {
    apiError(state) {
      state.loading = false;
      state.error = true;
    },
    signedOut(state) {
      state.loading = false;
      state.signedIn = false;
    },
    studentNotCreated(state) {
      state.loading = false;
      state.student = null;
    },
    signedIn(state) { state.signedIn = true; },
    wsAcquired(state, ws) {
      state.loading = false;
      state.ws = ws;
    }
  }
});
