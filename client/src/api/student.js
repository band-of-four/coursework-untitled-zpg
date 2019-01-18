import { get, post, getResource, postResource } from './index.js';

export const STUDENT_NOT_CREATED = 'student_not_created';

export async function getStudent() {
  const response = await get('/student');
  if (response.status === 404)
    throw STUDENT_NOT_CREATED;
  return await response.json();
}

export const postStudent = postResource('/student');

export const getSpells = getResource('/student/spells');
