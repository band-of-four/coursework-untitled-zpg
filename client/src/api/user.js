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

export async function postSignIn(credentials) {
  const response = await post('/auth/signin', credentials);
  if (response.status === 200) return true;
  else return false;
}
