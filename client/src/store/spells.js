import { nonPaginatedStore } from './_resource.js';
import { getSpells } from '/api/student.js';

export default nonPaginatedStore('Spells', getSpells);
