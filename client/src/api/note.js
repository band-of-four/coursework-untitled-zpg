import { getResource, post } from './index.js';

export const getDiarySections = getResource('/notes/diary');

export function postHeart(id) {
  return post(`/notes/${id}/heart`, undefined).then((r) => r.json());
}
