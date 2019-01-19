import { getResourcePage, post } from './index.js';

export const getDiarySections = getResourcePage('/notes/diary');

export function postHeart(id) {
  return post(`/notes/${id}/heart`, undefined).then((r) => r.json());
}
