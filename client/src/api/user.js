function get(url) {
  return fetch(url, {
    method: 'GET',
    credentials: 'same-origin'
  });
}

function post(url, body) {
  return fetch(url, {
    method: 'POST',
    credentials: 'same-origin',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(body)
  });
}

export const USER_STATE_MISSING = 'user_state_missing';
export const USER_AUTH_REQUIRED = 'user_auth_required';

export async function postSignIn(credentials) {
  const response = await post('/auth/signin', credentials);
  if (response.status === 200) return true;
  else return false;
}

export async function getState() {
  const response = await get('/user/state');
  if (response.status === 401) return USER_AUTH_REQUIRED;
  if (response.status === 404) return USER_STATE_MISSING;

  const state = await response.json();

  return state;
}
