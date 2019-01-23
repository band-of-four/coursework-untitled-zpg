import Vue from 'vue';
import Vuex from 'vuex';

import { API_UNAUTHENTICATED } from '/api';
import { getStudent, postStudent, STUDENT_NOT_CREATED } from '/api/student.js';
import { postSignIn, postSignUp } from '/api/auth.js';
import { WS_URI, WS_OUT_GET_STAGE } from '/api/ws.js';
import { withDelay } from '/utils.js';

import studentModule from './student.js';
import stageModule from './stage.js';
import spellsModule from './spells.js';
import owlsModule from './owls.js';
import attendanceModule from './attendance.js';
import diaryModule from './diary.js';
import relationshipsModule from './relationships.js';
import skillsModule from './skills.js';

const LOADING_SCREEN_MIN_SHOWN_MS = 2000;

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    error: false,
    loading: false,
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

      commit('signedInLoading');
      await withDelay(LOADING_SCREEN_MIN_SHOWN_MS, () => dispatch('load'));

      return true;
    },
    async createStudent({ commit, dispatch }, studentForm) {
      try {
        const student = await postStudent(studentForm);
        commit('loading');
        await withDelay(LOADING_SCREEN_MIN_SHOWN_MS, () => dispatch('initState', student));
        return true;
      }
      catch (e) {
        return e;
      }
    },
    async initState({ commit, dispatch }, student) {
      this.registerModule('student', studentModule(student));
      this.registerModule('spells', spellsModule);
      this.registerModule('owls', owlsModule);
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
      commit('owls/processMessage', message);
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
      state.student = null;
    },
    loading(state) {
      state.loading = true;
    },
    loaded(state) {
      state.loading = false;
    },
    signedInLoading(state) {
      state.signedIn = true;
      state.loading = true;
    },
    wsAcquired(state, ws) {
      state.ws = ws;
    }
  }
});
