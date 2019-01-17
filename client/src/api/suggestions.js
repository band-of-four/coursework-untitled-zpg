import { getResource, post } from './index.js';

export const getLessonNames = getResource('/suggest/lesson/names');

export const postText = (data) => post('/suggest/text', data);
