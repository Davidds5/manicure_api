'use client';

import { useState, Suspense } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { 
  Building2, 
  User, 
  Mail, 
  Lock, 
  Phone, 
  ArrowRight, 
  ArrowLeft,
  AlertCircle, 
  Eye, 
  EyeOff, 
  Check, 
  X,
  Sparkles
} from 'lucide-react';
import { fetchApi, setAuthToken } from '@/lib/api';

function SignupForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const initialPlan = searchParams.get('plan') || 'FREE';

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [confirmPassword, setConfirmPassword] = useState('');

  const [formData, setFormData] = useState({
    name: '',
    slug: '',
    ownerName: '',
    phone: '',
    email: '',
    password: '',
    plan: initialPlan,
  });

  // Requisitos de força de senha
  const hasMinLength = formData.password.length >= 6;
  const hasUpper = /[A-Z]/.test(formData.password);
  const hasNumber = /[0-9]/.test(formData.password);
  const passwordsMatch = formData.password !== '' && formData.password === confirmPassword;

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    const generatedSlug = val
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-z0-9]/g, '-')
      .replace(/-+/g, '-')
      .replace(/^-|-$/g, '');

    setFormData((prev) => ({
      ...prev,
      name: val,
      slug: prev.slug === '' || prev.slug === prev.name.toLowerCase().replace(/\s+/g, '-') ? generatedSlug : prev.slug,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!hasMinLength) {
      setError('A senha deve conter no mínimo 6 caracteres.');
      return;
    }

    if (formData.password !== confirmPassword) {
      setError('As senhas digitadas não coincidem. Verifique e tente novamente.');
      return;
    }

    setLoading(true);

    try {
      await fetchApi<any>('/tenants/signup', {
        method: 'POST',
        body: JSON.stringify({
          salonName: formData.name,
          slug: formData.slug,
          ownerName: formData.ownerName,
          ownerEmail: formData.email,
          ownerPassword: formData.password,
          specialty: 'Proprietária',
          brandColor: '#ec4899',
        }),
      });

      // Tenta login automático com o tenantSlug incluído
      try {
        const loginRes = await fetchApi<{ token: string }>('/login', {
          method: 'POST',
          body: JSON.stringify({
            login: formData.email,
            email: formData.email,
            password: formData.password,
            tenantSlug: formData.slug,
          }),
        });

        if (loginRes.token) {
          setAuthToken(loginRes.token);
          router.push('/dashboard');
          return;
        }
      } catch {
        // Fallback para tela de login com o slug pré-preenchido
      }

      router.push(`/login?registered=true&slug=${encodeURIComponent(formData.slug)}`);
    } catch (err: any) {
      setError(err.message || 'Falha ao cadastrar. Verifique os dados e tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white p-8 sm:p-10 rounded-3xl border border-stone-200/90 shadow-soft">
      {error && (
        <div className="mb-6 p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-sm flex items-start gap-3 shadow-xs">
          <AlertCircle className="w-5 h-5 shrink-0 text-rose-600 mt-0.5" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
            Nome do Salão / Estúdio
          </label>
          <div className="relative">
            <Building2 className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
            <input
              type="text"
              required
              placeholder="Ex: Studio Bella Nails"
              value={formData.name}
              onChange={handleNameChange}
              className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
            Link de Agendamento da Cliente
          </label>
          <div className="relative flex items-center">
            <span className="absolute left-3.5 text-xs text-stone-400 font-mono select-none">
              belasunhas.com.br/
            </span>
            <input
              type="text"
              required
              placeholder="studio-bella"
              value={formData.slug}
              onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
              className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-36 sm:pl-38 pr-4 py-3 text-sm text-pink-700 font-mono font-semibold focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
              Seu Nome / Responsável
            </label>
            <div className="relative">
              <User className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
              <input
                type="text"
                required
                placeholder="Ex: Mariana Silva"
                value={formData.ownerName}
                onChange={(e) => setFormData({ ...formData, ownerName: e.target.value })}
                className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
              WhatsApp de Contato
            </label>
            <div className="relative">
              <Phone className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
              <input
                type="tel"
                required
                placeholder="(11) 99999-8888"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
              />
            </div>
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
            E-mail de Login
          </label>
          <div className="relative">
            <Mail className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
            <input
              type="email"
              required
              placeholder="voce@seusalao.com"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
              Senha de Acesso
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                placeholder="Mínimo 6 dígitos"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-10 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3.5 top-3.5 text-stone-400 hover:text-stone-700 transition cursor-pointer"
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
              Confirmar Senha
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-stone-400 absolute left-3.5 top-3.5 transition-colors" />
              <input
                type={showConfirmPassword ? 'text' : 'password'}
                required
                placeholder="Repita a senha"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full bg-stone-50 border border-stone-200 rounded-xl pl-10 pr-10 py-3 text-sm text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white focus:ring-2 focus:ring-pink-500/10 transition"
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-3.5 top-3.5 text-stone-400 hover:text-stone-700 transition cursor-pointer"
              >
                {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>
        </div>

        {/* Indicadores de Validação de Senha */}
        <div className="bg-stone-50 p-3 rounded-xl border border-stone-200 flex flex-wrap gap-x-4 gap-y-1.5 text-[11px] text-stone-600">
          <div className="flex items-center gap-1.5">
            {hasMinLength ? <Check className="w-3.5 h-3.5 text-emerald-600 font-bold" /> : <X className="w-3.5 h-3.5 text-stone-400" />}
            <span>6+ dígitos</span>
          </div>
          <div className="flex items-center gap-1.5">
            {hasUpper ? <Check className="w-3.5 h-3.5 text-emerald-600 font-bold" /> : <X className="w-3.5 h-3.5 text-stone-400" />}
            <span>Letra maiúscula</span>
          </div>
          <div className="flex items-center gap-1.5">
            {hasNumber ? <Check className="w-3.5 h-3.5 text-emerald-600 font-bold" /> : <X className="w-3.5 h-3.5 text-stone-400" />}
            <span>Número</span>
          </div>
          <div className="flex items-center gap-1.5">
            {passwordsMatch ? <Check className="w-3.5 h-3.5 text-emerald-600 font-bold" /> : <X className="w-3.5 h-3.5 text-stone-400" />}
            <span>Senhas iguais</span>
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-stone-700 mb-1.5 uppercase tracking-wider">
            Plano Inicial Selecionado
          </label>
          <div className="grid grid-cols-3 gap-2">
            {(['FREE', 'PRO', 'ENTERPRISE'] as const).map((p) => (
              <button
                key={p}
                type="button"
                onClick={() => setFormData({ ...formData, plan: p })}
                className={`py-2.5 rounded-xl text-xs font-bold border transition cursor-pointer ${
                  formData.plan === p
                    ? 'border-pink-500 bg-pink-50 text-pink-700 shadow-xs'
                    : 'border-stone-200 bg-stone-50 text-stone-600 hover:border-pink-300'
                }`}
              >
                {p}
              </button>
            ))}
          </div>
        </div>

        <button
          type="submit"
          disabled={loading || (formData.password !== '' && !passwordsMatch)}
          className="btn-tactile w-full mt-4 bg-pink-600 hover:bg-pink-700 text-white font-bold py-4 rounded-xl shadow-md shadow-pink-500/25 transition flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer"
        >
          {loading ? (
            <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
          ) : (
            <>
              <span>Criar meu Espaço Agora</span>
              <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </form>

      <div className="mt-8 pt-6 border-t border-stone-100 text-center">
        <p className="text-xs text-stone-600 font-medium">
          Já tem um cadastro?{' '}
          <Link href="/login" className="font-bold text-pink-600 hover:text-pink-700 underline underline-offset-4">
            Fazer Login
          </Link>
        </p>
      </div>
    </div>
  );
}

export default function SignupPage() {
  return (
    <div className="min-h-screen text-stone-900 flex flex-col justify-center py-12 px-6 lg:px-8 relative overflow-hidden bg-[#faf7f2]">
      {/* Ambient Glows com a paleta da landing page */}
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
          Cadastre o seu Salão
        </h1>
        <p className="mt-2 text-sm text-stone-600">
          Receba agendamentos automáticos pelo WhatsApp e Instagram
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-xl relative z-10">
        <Suspense fallback={<div className="p-8 text-center text-stone-500 text-xs">Carregando formulário...</div>}>
          <SignupForm />
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
