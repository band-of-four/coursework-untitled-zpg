import { paginatedStore } from './paginated-store.js';
import { getDiarySections } from '/api/note.js';

const store = paginatedStore('Diary', getDiarySections);

export default {
  ...store,
  getters: {
    noteCount(state) {
      return state.items.reduce((acc, { notes }) => acc + notes.length, 0);
    }
  }
};
