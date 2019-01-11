import { get, post } from './index.js';

export const STUDENT_NOT_CREATED = 'student_not_created';

export async function getStudent() {
  const response = await get('/student');
  if (response.status === 404)
    throw STUDENT_NOT_CREATED;
  return await response.json();
}