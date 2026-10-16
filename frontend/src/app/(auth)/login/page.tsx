'use client';

import { useState, Suspense } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { 
  ArrowRight, 
  ArrowLeft, 
  Lock, 
  Mail, 
  AlertCircle, 
  CheckCircle2, 
  Eye, 
  EyeOff, 
  Store 
} from 'lucide-react';
import { fetchApi, setAuthToken, removeAuthToken } from '@/lib/api';

function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const registered = searchParams.get('registered');
  const expired = searchParams.get('expired');
  const initialSlug = searchParams.get('slug') || '';

  const [email, setEmail] = useState('teste@salao.com');
  const [password, setPassword] = useState('123456');
  const [tenantSlug, setTenantSlug] = useState(initialSlug);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    removeAuthToken();

    try {
      const payload: { login: string; email: string; password: string; tenantSlug?: string } = {
        login: email.trim(),
        email: email.trim(),
        password,
      };

      if (tenantSlug.trim()) {
        payload.tenantSlug = tenantSlug.trim().toLowerCase();
      }

      const res = await fetchApi<{ token: string }>('/login', {
        method: 'POST',
        body: JSON.stringify(payload),
      });

      if (res && res.token) {
        setAuthToken(res.token);
        router.push('/dashboard');
      } else {
        throw new Error('Token JWT não retornado pelo servidor.');
      }
    } catch (err: any) {
      if (err.message && (err.message.includes('fetch') || err.message.includes('servidor') || err.status === 0)) {
        setError('Não foi possível conectar ao servidor. O backend no Render pode estar acordando, tente novamente em 15 segundos.');
      } else if (err.status === 401 || err.status === 403) {
        setError('E-mail ou senha incorretos. Verifique suas credenciais.');
      } else if (err.status >= 500) {
        setError(err.message || 'Servidor temporariamente indisponível. Tente novamente em instantes.');
      } else {
        setError(err.message || 'Erro ao efetuar login.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white p-8 sm:p-10 rounded-3xl border border-stone-200/90 shadow-soft">
      {registered && (
        <div className="mb-6 p-4 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm flex items-start gap-3 shadow-xs">
          <CheckCircle2 className="w-5 h-5 shrink-0 text-emerald-600 mt-0.5" />
          <span>Espaço cadastrado com sucesso! Faça seu primeiro acesso ao painel.</span>
        </div>
      )}

      {expired && (
        <div className="mb-6 p-4 rounded-2xl bg-amber-50 border border-amber-200 text-amber-800 text-xs flex items-start gap-2.5 shadow-xs">
          <AlertCircle className="w-4 h-4 shrink-0 text-amber-600 mt-0.5" />
          <span>Sua sessão expirou ou não está autenticada. Faça login para acessar o painel.</span>
        </div>
      )}

      {error && (
        <div className="mb-6 p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-sm flex items-start gap-3 shadow-xs animate-in fade-in duration-200">
          <AlertCircle className="w-5 h-5 shrink-0 text-rose-600 mt-0.5" />
          <div>
            <p className="font-semibold">{error}</p>
            {error.includes('credenciais') && (
              <p className="text-xs text-rose-600 mt-1">
                Dica: Se o seu salão possui identificador específico, preencha o campo opcional de identificador do salão abaixo.
              </p>
            )}
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
            E-mail de Acesso
          </label>
          <div className="relative">
            <Mail className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
            <input
              type="email"
              required
              placeholder="voce@seusalao.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
            Sua Senha
          </label>
          <div className="relative">
            <Lock className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
            <input
              type={showPassword ? 'text' : 'password'}
              required
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-10 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3.5 top-3.5 text-stone-400 hover:text-stone-700 transition cursor-pointer"
              title={showPassword ? 'Ocultar senha' : 'Ver senha'}
            >
              {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-stone-700 mb-1.5 flex items-center justify-between">
            <span className="uppercase tracking-wider">Identificador do Salão (Slug)</span>
            <span className="text-[11px] font-normal text-stone-400 capitalize">Opcional</span>
          </label>
          <div className="relative">
            <Store className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
            <input
              type="text"
              placeholder="ex: studio-bella"
              value={tenantSlug}
              onChange={(e) => setTenantSlug(e.target.value)}
              className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
            />
          </div>
          <p className="text-[11px] text-stone-500 mt-1">
            Preencha caso gerencie um salão específico (ex: <code className="bg-stone-100 text-stone-700 px-1 py-0.5 rounded text-[10px]">studio-bella</code>).
          </p>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="btn-tactile w-full mt-2 bg-pink-600 hover:bg-pink-700 text-white font-bold py-3.5 px-6 rounded-xl shadow-md shadow-pink-500/25 transition flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50"
        >
          {loading ? (
            <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
          ) : (
            <>
              <span>Acessar Painel</span>
              <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </form>

      <div className="mt-8 pt-6 border-t border-stone-100 text-center">
        <p className="text-xs text-stone-600 font-medium">
          Ainda não tem um espaço?{' '}
          <Link href="/signup" className="font-bold text-pink-600 hover:text-pink-700 underline underline-offset-4">
            Cadastrar Grátis
          </Link>
        </p>
      </div>
    </div>
  );
}

export default function LoginPage() {
  return (
    <div className="min-h-screen text-stone-900 flex flex-col justify-center py-12 px-6 lg:px-8 relative overflow-hidden bg-[#faf7f2]">
      {/* Ambient Glows com a paleta suave da landing page */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden -z-10">
        <div className="absolute -top-40 right-1/4 w-[500px] h-[500px] bg-pink-500/10 blur-[130px] rounded-full" />
        <div className="absolute -bottom-40 left-1/4 w-[500px] h-[500px] bg-rose-400/10 blur-[130px] rounded-full" />
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md relative z-10 text-center">
        {/* Logo oficial BelasUnhas */}
        <Link href="/" className="inline-flex items-center gap-3 mb-6 group">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-rose-500 to-pink-500 flex items-center justify-center text-white shadow-md shadow-pink-500/25 transition-transform duration-200 group-hover:scale-105">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="m14 12-8.5 8.5a2.12 2.12 0 1 1-3-3L11 9" />
              <path d="M15 13 9 7l4-4 6 6-4 4Z" />
              <path d="m17 7 3-3" />
            </svg>
          </div>
          <div className="flex flex-col text-left">
            <span className="text-xl font-extrabold tracking-tight text-stone-900 font-serif leading-tight">
              Belas<span className="text-pink-600">Unhas</span>
            </span>
            <span className="text-[10px] font-bold text-stone-500 tracking-wider uppercase">
              Agenda & Gestão de Manicures
            </span>
          </div>
        </Link>

        <h1 className="text-3xl font-extrabold tracking-tight text-stone-900 font-serif">
          Acesse a sua Agenda
        </h1>
        <p className="mt-2 text-sm text-stone-600">
          Entre com seu e-mail para ver os horários de hoje
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md relative z-10">
        <Suspense fallback={<div className="p-8 text-center text-stone-500 text-xs">Carregando formulário...</div>}>
          <LoginForm />
        </Suspense>

        <div className="mt-6 text-center">
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-stone-500 hover:text-pink-600 transition"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            Voltar para o início
          </Link>
        </div>
      </div>
    </div>
  );
}
