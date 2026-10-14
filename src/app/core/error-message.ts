export function apiErrorMessage(error: unknown, fallback = 'Please try again in a moment.'): string {
  const response = error as { error?: unknown; message?: string };
  const body = response?.error;
  const details = [typeof body === 'string' ? body : '', response?.message || ''].join(' ');
  if ((error as { status?: number })?.status === 0 || /proxy|econnrefused|failed to fetch|networkerror/i.test(details)) {
    return 'The backend service could not be reached. Start the matching Spring Boot service and check its database connection, then retry.';
  }
  if (typeof body === 'string' && body.trim()) return body;
  if (body && typeof body === 'object' && 'message' in body) {
    const message = (body as { message?: unknown }).message;
    if (typeof message === 'string' && message.trim()) return message;
  }
  return response?.message || fallback;
}
