import { paginatedStore } from './paginated-store.js';
import { getRelationships } from '/api/student.js';

export default paginatedStore('Relationships', getRelationships);
