const rawUrl = process.env.NEXT_PUBLIC_API_URL || 'https://manicure-api-vi63.onrender.com';
const API_BASE_URL = rawUrl.trim().replace(/[\r\n\s]+$/, '').replace(/\/+$/, '');

export class ApiError extends Error {
  status: number;
  data: any;
  constructor(status: number, message: string, data?: any) {
    super(message);
    this.status = status;
    this.data = data;
  }
}

export function getAuthToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('bellasync_token');
}

export function setAuthToken(token: string) {
  if (typeof window !== 'undefined') {
    localStorage.setItem('bellasync_token', token);
  }
}

export function removeAuthToken() {
  if (typeof window !== 'undefined') {
    localStorage.removeItem('bellasync_token');
    localStorage.removeItem('bellasync_user');
  }
}

export async function fetchApi<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = getAuthToken();
  const headers = new Headers(options.headers || {});

  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  let cleanEndpoint = endpoint.trim().replace(/^\/+/, '');
  if (/^(services|professionals|appointments|clients|payments)(\/|\?|$)/.test(cleanEndpoint)) {
    cleanEndpoint = `api/v1/${cleanEndpoint}`;
  }

  const isPublicAuth = /^(login|auth\/login|tenants\/signup)(\/|\?|$)/.test(cleanEndpoint);
  if (token && !isPublicAuth) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const url = cleanEndpoint.startsWith('http') ? cleanEndpoint : `${API_BASE_URL}/${cleanEndpoint}`;

  let res: Response;
  try {
    res = await fetch(url, {
      ...options,
      headers,
    });
  } catch (err: any) {
    throw new ApiError(
      0,
      'Não foi possível conectar ao servidor. Verifique se a API está online ou tente novamente em instantes.',
      err
    );
  }

  if (!res.ok) {
    let errorData = null;
    try {
      errorData = await res.json();
    } catch {
      // Body not JSON
    }

    let message = errorData?.message || errorData?.error;
    if (!message && Array.isArray(errorData?.errors) && errorData.errors.length > 0) {
      message = errorData.errors.map((e: any) => e.message || e.defaultMessage).filter(Boolean).join(', ');
    }
    if (!message) {
      if (res.status === 403 || res.status === 401) {
        message = 'E-mail ou senha incorretos, ou acesso não autorizado.';
      } else if (res.status === 404) {
        message = 'Recurso não encontrado no servidor.';
      } else if (res.status >= 500) {
        message = 'Erro interno no servidor da API. Tente novamente em alguns segundos.';
      } else {
        message = `Erro na requisição (${res.status}).`;
      }
    }
    throw new ApiError(res.status, message, errorData);
  }

  if (res.status === 204) {
    return {} as T;
  }

  return res.json();
}
