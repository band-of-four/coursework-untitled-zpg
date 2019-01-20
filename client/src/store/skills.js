import { paginatedStore } from './paginated-store.js';
import { getCreatureHandlingSkills } from '/api/student.js';

export default paginatedStore('CreatureHandlingSkills', getCreatureHandlingSkills);
