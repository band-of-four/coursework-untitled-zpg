import { getResource, post } from './index.js';

export const getOwls = getResource('/owls');

export function applyOwl(impl, data) {
  return post(`/owls/${impl}/apply`, data).then((r) => r.json());
}
