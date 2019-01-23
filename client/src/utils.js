export async function delay(millis) {
  await new Promise((resolve) => setTimeout(resolve, millis));
}

export async function withDelay(delayMillis, action) {
  const actionStartMillis = Date.now();

  const ret = await action();
  const delayRemaining = actionStartMillis + delayMillis - Date.now();
  if (delayRemaining > 0) await delay(delayRemaining);
  return ret;
}
