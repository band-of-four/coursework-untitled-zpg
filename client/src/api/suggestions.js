import { getResource, getResourcePage, post } from './index.js';

export const getLessonNames = getResource('/suggestions/lesson/names');

export const getClubNames = getResource('/suggestions/club/names');

export const postText = (data) => post('/suggestions/text', data);

export const postCreature = (data) => post('/suggestions/creature', data);

export const getApproved = getResourcePage('/suggestions/approved');
