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
  Store,
  Sparkles,
  ShieldCheck,
  CalendarCheck,
  TrendingUp,
  MessageCircle,
  Star,
  Check
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
  const [tenantSlug, setTenantSlug] = useState(initialSlug || 'studio-bella');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleFillDemo = () => {
    setEmail('teste@salao.com');
    setPassword('123456');
    setTenantSlug('studio-bella');
    setError(null);
  };

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

      const cleanSlug = tenantSlug.trim().toLowerCase();
      if (cleanSlug) {
        payload.tenantSlug = cleanSlug;
      } else if (email.trim().toLowerCase() === 'teste@salao.com') {
        payload.tenantSlug = 'studio-bella';
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
        setError('E-mail ou senha incorretos. Verifique suas credenciais de acesso.');
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
    <div className="w-full">
      {/* Atalho de Demonstração para Testes */}
      <div className="mb-6 p-3.5 rounded-2xl bg-pink-50/90 border border-pink-200/80 flex items-center justify-between gap-3 shadow-2xs">
        <div className="flex items-center gap-2.5 text-xs text-stone-700">
          <div className="w-7 h-7 rounded-lg bg-pink-100 flex items-center justify-center text-pink-600 shrink-0">
            <Sparkles className="w-4 h-4" />
          </div>
          <div>
            <span className="font-bold text-stone-900 block leading-tight">
              Acesso Demonstrativo
            </span>
            <span className="text-[11px] text-stone-500">
              Teste o salão de exemplo imediatamente
            </span>
          </div>
        </div>
        <button
          type="button"
          onClick={handleFillDemo}
          className="btn-secondary-tactile px-3 py-1.5 text-xs font-bold rounded-xl text-pink-700 hover:text-pink-800 transition cursor-pointer shrink-0"
          title="Preencher com credenciais de demonstração (teste@salao.com)"
        >
          Preencher Teste
        </button>
      </div>

      {registered && (
        <div className="mb-6 p-4 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm flex items-start gap-3 shadow-xs">
          <CheckCircle2 className="w-5 h-5 shrink-0 text-emerald-600 mt-0.5" />
          <div>
            <p className="font-bold">Espaço cadastrado com sucesso!</p>
            <p className="text-xs text-emerald-700 mt-0.5">Faça login com os dados cadastrados para acessar o seu painel.</p>
          </div>
        </div>
      )}

      {expired && (
        <div className="mb-6 p-4 rounded-2xl bg-amber-50 border border-amber-200 text-amber-800 text-xs flex items-start gap-2.5 shadow-xs">
          <AlertCircle className="w-4 h-4 shrink-0 text-amber-600 mt-0.5" />
          <span>Sua sessão expirou por segurança. Faça login novamente para continuar gerenciando sua agenda.</span>
        </div>
      )}

      {error && (
        <div className="mb-6 p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-sm flex items-start gap-3 shadow-xs animate-in fade-in duration-200">
          <AlertCircle className="w-4 h-4 shrink-0 text-rose-600 mt-0.5" />
          <div>
            <p className="font-semibold text-rose-900">{error}</p>
            {error.includes('credenciais') && (
              <p className="text-xs text-rose-700 mt-1">
                Dica: Verifique se o identificador do salão (<code className="font-mono bg-rose-100/90 text-rose-900 px-1 py-0.5 rounded text-[11px] font-bold">studio-bella</code>) está preenchido corretamente no campo abaixo.
              </p>
            )}
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Campo E-mail */}
        <div>
          <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
            E-mail Profissional
          </label>
          <div className="relative group">
            <Mail className="w-4 h-4 text-stone-400 group-focus-within:text-pink-600 absolute left-3.5 top-3.5 transition-colors" />
            <input
              type="email"
              required
              placeholder="voce@seusalao.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-white border border-stone-200 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:ring-4 focus:ring-pink-500/10 transition shadow-2xs"
            />
          </div>
        </div>

        {/* Campo Senha */}
        <div>
          <div className="flex items-center justify-between mb-1.5">
            <label className="block text-xs font-bold text-stone-700 uppercase tracking-wider">
              Sua Senha
            </label>
            <button
              type="button"
              onClick={() => alert('Para redefinir sua senha de acesso, solicite o reset através do suporte do salão ou do administrador.')}
              className="text-[11px] font-medium text-pink-600 hover:text-pink-700 hover:underline transition"
            >
              Esqueceu a senha?
            </button>
          </div>
          <div className="relative group">
            <Lock className="w-4 h-4 text-stone-400 group-focus-within:text-pink-600 absolute left-3.5 top-3.5 transition-colors" />
            <input
              type={showPassword ? 'text' : 'password'}
              required
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-white border border-stone-200 rounded-xl pl-10 pr-10 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:ring-4 focus:ring-pink-500/10 transition shadow-2xs"
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

        {/* Campo Slug do Salão */}
        <div>
          <div className="flex items-center justify-between mb-1.5">
            <label className="block text-xs font-bold text-stone-700 uppercase tracking-wider">
              Identificador do Salão (Slug)
            </label>
            <span className="text-[11px] font-medium text-stone-500 bg-stone-100 px-2 py-0.5 rounded-full">
              Opcional
            </span>
          </div>
          <div className="relative group">
            <Store className="w-4 h-4 text-stone-400 group-focus-within:text-pink-600 absolute left-3.5 top-3.5 transition-colors" />
            <input
              type="text"
              placeholder="ex: studio-bella"
              value={tenantSlug}
              onChange={(e) => setTenantSlug(e.target.value)}
              className="w-full bg-white border border-stone-200 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:ring-4 focus:ring-pink-500/10 transition shadow-2xs"
            />
          </div>
          <p className="text-[11px] text-stone-500 mt-1.5">
            Salão de demonstração: <code className="bg-stone-100 text-stone-800 px-1.5 py-0.5 rounded text-[11px] font-semibold border border-stone-200">studio-bella</code>
          </p>
        </div>

        {/* Botão de Submit Tátil */}
        <button
          type="submit"
          disabled={loading}
          className="btn-tactile w-full mt-2 bg-pink-600 hover:bg-pink-700 text-white font-bold py-3.5 px-6 rounded-xl shadow-card transition flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50"
        >
          {loading ? (
            <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
          ) : (
            <>
              <span className="text-sm">Acessar Meu Painel</span>
              <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </form>

      {/* Rodapé do Formulário */}
      <div className="mt-8 pt-6 border-t border-stone-200/80 text-center">
        <p className="text-xs text-stone-600 font-medium">
          Ainda não tem um espaço cadastrado?{' '}
          <Link href="/signup" className="font-bold text-pink-600 hover:text-pink-700 underline underline-offset-4 transition">
            Cadastrar Salão Grátis
          </Link>
        </p>
      </div>
    </div>
  );
}

export default function LoginPage() {
  return (
    <div className="min-h-screen flex flex-col lg:grid lg:grid-cols-12 bg-[#faf7f2] text-stone-900 selection:bg-pink-500 selection:text-white">
      {/* =========================================================================
          COLUNA DA ESQUERDA (DESKTOP): SHOWCASE VISUAL NO PADRÃO DO DASHBOARD BELASUNHAS
          ========================================================================= */}
      <section className="lg:col-span-5 xl:col-span-5 bg-[#161311] text-stone-100 p-8 lg:p-12 xl:p-14 flex flex-col justify-between relative overflow-hidden border-b lg:border-b-0 lg:border-r border-stone-800">
        {/* Glows ambientais de iluminação premium */}
        <div className="absolute top-0 right-0 w-80 h-80 bg-pink-600/15 rounded-full blur-[110px] pointer-events-none" />
        <div className="absolute bottom-0 left-0 w-80 h-80 bg-rose-500/10 rounded-full blur-[130px] pointer-events-none" />

        {/* Topo: Logo Oficial */}
        <div className="relative z-10">
          <Link href="/" className="inline-flex items-center gap-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-rose-500 to-pink-500 flex items-center justify-center text-white shadow-md shadow-pink-500/30 transition-transform duration-200 group-hover:scale-105">
              <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="m14 12-8.5 8.5a2.12 2.12 0 1 1-3-3L11 9" />
                <path d="M15 13 9 7l4-4 6 6-4 4Z" />
                <path d="m17 7 3-3" />
              </svg>
            </div>
            <div className="flex flex-col">
              <span className="text-xl font-extrabold tracking-tight text-white font-serif leading-tight">
                Belas<span className="text-pink-500">Unhas</span>
              </span>
              <span className="text-[10px] font-bold text-stone-400 tracking-wider uppercase">
                Painel do Profissional
              </span>
            </div>
          </Link>
        </div>

        {/* Centro: Proposta de Valor & Mockup do Dashboard */}
        <div className="my-8 lg:my-auto relative z-10 space-y-6">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-pink-500/10 border border-pink-500/20 text-pink-400 text-xs font-semibold">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Gestão completa para seu estúdio</span>
          </div>

          <h2 className="text-2xl sm:text-3xl xl:text-4xl font-extrabold font-serif text-white leading-tight tracking-tight">
            Sua agenda cheia, clientes no horário e seu faturamento sob controle.
          </h2>

          <p className="text-sm text-stone-300 leading-relaxed max-w-md">
            O BelasUnhas automatiza seus agendamentos, envia lembretes por WhatsApp e organiza sua equipe de manicures sem estresse.
          </p>

          {/* Mini Mockup do Painel (espelhando a estética do Dashboard real) */}
          <div className="p-5 rounded-2xl bg-[#1c1815] border border-stone-800 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-stone-800/80 pb-3">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-pink-600 flex items-center justify-center font-bold text-white text-xs shadow-xs">
                  S
                </div>
                <div>
                  <h4 className="text-xs font-bold text-white leading-none">Studio Bella Nails</h4>
                  <span className="text-[10px] text-pink-400 font-mono">/studio-bella</span>
                </div>
              </div>
              <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-pink-500/15 text-pink-400 border border-pink-500/30">
                Plano FREE
              </span>
            </div>

            {/* Grid de Métricas do Mockup */}
            <div className="grid grid-cols-2 gap-2.5">
              <div className="p-3 rounded-xl bg-stone-900/80 border border-stone-800">
                <span className="text-[10px] text-stone-400 block mb-0.5">Faturamento Estimado</span>
                <span className="text-sm font-extrabold text-white font-serif">R$ 2.450,00</span>
                <span className="text-[9px] text-emerald-400 font-bold block mt-0.5">↑ 18% este mês</span>
              </div>
              <div className="p-3 rounded-xl bg-stone-900/80 border border-stone-800">
                <span className="text-[10px] text-stone-400 block mb-0.5">Agendamentos Hoje</span>
                <span className="text-sm font-extrabold text-white font-serif">6 confirmados</span>
                <span className="text-[9px] text-pink-400 font-bold block mt-0.5">WhatsApp ativo</span>
              </div>
            </div>

            {/* Notificação em tempo real simulada */}
            <div className="p-2.5 rounded-xl bg-stone-900/60 border border-stone-800 flex items-center gap-2.5 text-[11px] text-stone-300">
              <div className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              <span className="truncate">
                Auto-agendamento via WhatsApp: <strong>Alongamento em Gel</strong> às 15:30
              </span>
            </div>
          </div>
        </div>

        {/* Rodapé da Coluna Esquerda: Selos de Confiança */}
        <div className="relative z-10 pt-4 border-t border-stone-800/80 flex items-center justify-between text-xs text-stone-400">
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-emerald-400" />
            <span>Dados 100% isolados por salão</span>
          </div>
          <div className="flex items-center gap-1.5 text-amber-300">
            <Star className="w-3.5 h-3.5 fill-amber-300" />
            <span className="font-bold text-white text-xs">4.9/5</span>
            <span className="text-[11px] text-stone-400">(+1.200 salões)</span>
          </div>
        </div>
      </section>

      {/* =========================================================================
          COLUNA DA DIREITA: FORMULÁRIO DE LOGIN LIMPO, ERGONÔMICO E ELEGANTE
          ========================================================================= */}
      <main className="lg:col-span-7 xl:col-span-7 p-6 sm:p-10 lg:p-14 xl:p-16 flex flex-col justify-between relative overflow-hidden">
        {/* Ambient Glow sutil na direita */}
        <div className="absolute -top-32 right-0 w-96 h-96 bg-pink-500/5 blur-[120px] pointer-events-none" />
        <div className="absolute -bottom-32 right-1/4 w-96 h-96 bg-rose-400/5 blur-[120px] pointer-events-none" />

        {/* Topo da Direita: Navegação e Link para Cadastro */}
        <div className="flex items-center justify-between relative z-10 mb-8 sm:mb-12">
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-stone-500 hover:text-pink-600 transition"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Voltar para o início</span>
          </Link>

          <Link
            href="/signup"
            className="text-xs font-bold text-pink-600 hover:text-pink-700 bg-pink-50 hover:bg-pink-100 border border-pink-200/80 px-3.5 py-1.5 rounded-full transition shadow-2xs"
          >
            Cadastrar meu Salão ↗
          </Link>
        </div>

        {/* Centro da Direita: Card Principal de Autenticação */}
        <div className="w-full max-w-md mx-auto my-auto relative z-10">
          <div className="mb-8">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-stone-100 border border-stone-200 text-stone-700 text-xs font-bold mb-3">
              <span className="w-2 h-2 rounded-full bg-pink-500" />
              <span>Acesso do Profissional</span>
            </div>
            <h1 className="text-3xl font-extrabold tracking-tight text-stone-900 font-serif">
              Acesse a sua Agenda
            </h1>
            <p className="mt-2 text-sm text-stone-600">
              Entre com suas credenciais de acesso para abrir os agendamentos e o painel do seu espaço.
            </p>
          </div>

          <div className="bg-white p-7 sm:p-9 rounded-3xl border border-stone-200/90 shadow-soft">
            <Suspense fallback={<div className="p-8 text-center text-stone-500 text-xs">Carregando formulário...</div>}>
              <LoginForm />
            </Suspense>
          </div>

          {/* Selos de Garantia / Suporte */}
          <div className="mt-8 grid grid-cols-2 gap-3 text-center text-stone-500 text-[11px]">
            <div className="p-2.5 rounded-xl bg-stone-100/70 border border-stone-200/70 flex items-center justify-center gap-1.5">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
              <span>Conexão Criptografada SSL</span>
            </div>
            <div className="p-2.5 rounded-xl bg-stone-100/70 border border-stone-200/70 flex items-center justify-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-pink-600" />
              <span>Suporte Humanizado</span>
            </div>
          </div>
        </div>

        {/* Rodapé da Direita: Copyright */}
        <div className="text-center pt-8 border-t border-stone-200/60 relative z-10 text-[11px] text-stone-400">
          BelasUnhas © {new Date().getFullYear()} • Sistema de Agendamento Inteligente para Manicures
        </div>
      </main>
    </div>
  );
}
