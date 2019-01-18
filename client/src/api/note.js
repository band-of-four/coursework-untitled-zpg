import { getResource, post } from './index.js';

export const getDiarySections = getResource('/notes/diary');

export function postHeart(id) {
  return post(`/notes/heart/${id}`, undefined).then((r) => r.json());
}
