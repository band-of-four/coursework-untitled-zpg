import { getResource, post } from './index.js';

export const getOwls = getResource('/owls');

export function applyOwl(id, data) {
  return post(`/owls/${id}/apply`, data).then((r) => r.json());
}
