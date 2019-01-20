import { nonPaginatedStore } from './_resource.js';
import { getOwls } from '/api/owls.js';

export default nonPaginatedStore('Owls', getOwls);
