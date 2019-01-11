export const API_ERROR = 'api_error';
export const API_UNAUTHENTICATED = 'api_unauthenticated';

export function get(url) {
  return fetch(url, {
    method: 'GET',
    credentials: 'same-origin'
  }).catch((e) => {
    throw API_ERROR;
  }).then((response) => {
    if (response.status === 401)
      throw API_UNAUTHENTICATED;
    return response;
  });
}

export function post(url, body) {
  return fetch(url, {
    method: 'POST',
    credentials: 'same-origin',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(body)
  }).catch((e) => {
    throw API_ERROR;
  }).then((response) => {
    if (response.status === 401)
      throw API_UNAUTHENTICATED;
    return response;
  });
}
