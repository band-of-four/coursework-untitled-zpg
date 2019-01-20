import { paginatedStore } from './_resource.js';
import { getRelationships } from '/api/student.js';

export default paginatedStore('Relationships', getRelationships);
