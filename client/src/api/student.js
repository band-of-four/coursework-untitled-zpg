import { get, post } from './index.js';

export const STUDENT_NOT_CREATED = 'student_not_created';

export async function getStudent() {
  const response = await get('/student');
  if (response.status === 404)
    throw STUDENT_NOT_CREATED;
  return await response.json();
}

export async function postStudent(data) {
  const response = await post('/student', data);
  return await response.json();
}

export async function getSpells() {
  const response = await get('/student/spells');
  return await response.json();
}
