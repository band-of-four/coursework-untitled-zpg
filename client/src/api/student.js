import { get, post, getResource, getResourcePage, postResource } from './index.js';

export const STUDENT_NOT_CREATED = 'student_not_created';
export const STUDENT_NAME_TAKEN = 'student_name_taken';

export async function getStudent() {
  const response = await get('/student');
  if (response.status === 404)
    throw STUDENT_NOT_CREATED;
  return await response.json();
}

export async function postStudent(data) {
  const response = await post('/student', data);
  if (response.status === 422)
    throw STUDENT_NAME_TAKEN;
  return await response.json();
}

export const getSpells = getResource('/student/spells');

export const getAttendance = getResource('/student/attendance');

export const getRelationships = getResourcePage('/student/relationships');

export const getCreatureHandlingSkills = getResourcePage('/student/skills');
