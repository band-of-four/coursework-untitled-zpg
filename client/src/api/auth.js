import { get, post } from './index.js';

export async function postSignIn(credentials) {
  const response = await post('/auth/signin', credentials);
  if (response.status === 200) return true;
  else return false;
}
