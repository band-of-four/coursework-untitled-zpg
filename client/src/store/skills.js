import { paginatedStore } from './_resource.js';
import { getCreatureHandlingSkills } from '/api/student.js';

export default paginatedStore('CreatureHandlingSkills', getCreatureHandlingSkills);
