'use client';

import { useState, useEffect, Suspense } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { Sparkles, ArrowRight, Lock, Mail, AlertCircle, CheckCircle2, Eye, EyeOff } from 'lucide-react';
import { fetchApi, setAuthToken } from '@/lib/api';

function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const registered = searchParams.get('registered');
  const expired = searchParams.get('expired');

  const [email, setEmail] = useState('teste@salao.com');
  const [password, setPassword] = useState('123456');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const res = await fetchApi<{ token: string }>('/login', {
        method: 'POST',
        body: JSON.stringify({ login: email, email, password }),
      });

      if (res && res.token) {
        setAuthToken(res.token);
        router.push('/dashboard');
      } else {
        throw new Error('Token JWT não retornado pelo servidor.');
      }
    } catch (err: any) {
      if (err.message && (err.message.includes('fetch') || err.message.includes('servidor'))) {
        setError('Não foi possível conectar ao servidor. O backend no Render pode estar acordando, tente novamente em 15 segundos.');
      } else if (err.status === 401 || err.status === 403) {
        setError('E-mail ou senha incorretos. Verifique suas credenciais.');
      } else if (err.status >= 500) {
        setError('A conta de teste ainda não foi inicializada ou o banco está sincronizando. Tente cadastrar seu salão em "Cadastrar Grátis".');
      } else {
        setError(err.message || 'Erro ao efetuar login.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-[#1f1b18] p-8 sm:p-10 rounded-3xl border border-stone-800 shadow-2xl">
      {registered && (
        <div className="mb-6 p-4 rounded-xl bg-emerald-950/40 border border-emerald-500/30 text-emerald-300 text-sm flex items-center gap-3">
          <CheckCircle2 className="w-5 h-5 shrink-0 text-emerald-400" />
          <span>Espaço cadastrado com sucesso! Faça seu primeiro acesso.</span>
        </div>
      )}

      {expired && (
        <div className="mb-6 p-4 rounded-xl bg-amber-950/40 border border-amber-500/30 text-amber-300 text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 shrink-0 text-amber-400" />
          <span>Sua sessão expirou ou não está autenticada. Faça login para acessar o painel.</span>
        </div>
      )}

      {error && (
        <div className="mb-6 p-4 rounded-xl bg-rose-950/40 border border-rose-500/40 text-rose-300 text-sm flex items-center gap-3">
          <AlertCircle className="w-4 h-4 shrink-0 text-rose-400" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label className="block text-xs font-bold text-stone-300 mb-1.5">E-mail de Acesso</label>
          <div className="relative">
            <Mail className="w-4 h-4 text-amber-400 absolute left-3.5 top-3.5" />
            <input
              type="email"
              required
              placeholder="voce@seusalao.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-stone-900 border border-stone-800 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-100 placeholder-stone-600 focus:outline-none focus:border-amber-400 transition"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-stone-300 mb-1.5">Sua Senha</label>
          <div className="relative">
            <Lock className="w-4 h-4 text-amber-400 absolute left-3.5 top-3.5" />
            <input
              type={showPassword ? 'text' : 'password'}
              required
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-stone-900 border border-stone-800 rounded-xl pl-10 pr-10 py-3 text-sm text-stone-100 placeholder-stone-600 focus:outline-none focus:border-amber-400 transition"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3.5 top-3.5 text-stone-500 hover:text-stone-300 transition cursor-pointer"
              title={showPassword ? 'Ocultar senha' : 'Ver senha'}
            >
              {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full mt-4 bg-gradient-to-r from-amber-400 via-amber-500 to-amber-600 hover:from-amber-500 hover:to-amber-700 text-stone-950 font-black py-4 rounded-xl shadow-lg shadow-amber-500/20 transition flex items-center justify-center gap-2 disabled:opacity-50"
        >
          {loading ? (
            <div className="w-5 h-5 border-2 border-stone-950/30 border-t-stone-950 rounded-full animate-spin" />
          ) : (
            <>
              Acessar Painel ✨
              <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </form>

      <p className="mt-6 text-center text-xs text-stone-400 font-medium">
        Ainda não tem um espaço?{' '}
        <Link href="/signup" className="font-bold text-amber-400 hover:text-amber-300">
          Cadastrar Grátis
        </Link>
      </p>
    </div>
  );
}

export default function LoginPage() {
  return (
    <div className="min-h-screen text-stone-200 flex flex-col justify-center py-12 px-6 lg:px-8 relative overflow-hidden bg-[#0c0a09]">
      {/* Glow Effects */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden">
        <div className="absolute -top-40 right-1/4 w-[600px] h-[500px] bg-amber-500/10 blur-[140px] rounded-full" />
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md relative z-10 text-center">
        <Link href="/" className="inline-flex items-center gap-3 mb-6">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-amber-400 to-amber-600 flex items-center justify-center shadow-lg shadow-amber-500/20 text-stone-950 text-2xl font-black">
            💅
          </div>
          <span className="text-2xl font-extrabold tracking-tight text-stone-100 font-serif">Belas<span className="text-gold-gradient font-black">Unhas</span></span>
        </Link>
        <h2 className="text-3xl font-extrabold tracking-tight text-stone-100 font-serif">Acesse a sua Agenda</h2>
        <p className="mt-2 text-sm text-stone-400">Entre com seu e-mail para ver os horários de hoje</p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md relative z-10">
        <Suspense fallback={<div className="p-8 text-center text-stone-500 text-xs">Carregando...</div>}>
          <LoginForm />
        </Suspense>
      </div>
    </div>
  );
}
