import { nonPaginatedStore } from './_resource.js';
import { getAttendance } from '/api/student.js';

export default nonPaginatedStore('LessonAttendance', getAttendance);
