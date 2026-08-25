'use client';

import { useState, Suspense } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { Building2, User, Mail, Lock, Phone, ArrowRight, AlertCircle, Eye, EyeOff, Check, X } from 'lucide-react';
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

      // Tenta login automático
      try {
        const loginRes = await fetchApi<{ token: string }>('/login', {
          method: 'POST',
          body: JSON.stringify({
            login: formData.email,
            email: formData.email,
            password: formData.password,
          }),
        });

        if (loginRes.token) {
          setAuthToken(loginRes.token);
          router.push('/dashboard');
          return;
        }
      } catch {
        // Fallback
      }

      router.push('/login?registered=true');
    } catch (err: any) {
      setError(err.message || 'Falha ao cadastrar. Verifique os dados e tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-[#14110f] p-8 sm:p-10 rounded-3xl border border-amber-500/25 shadow-2xl">
      {error && (
        <div className="mb-6 p-4 rounded-xl bg-rose-950/40 border border-rose-500/40 text-rose-300 text-sm flex items-center gap-3">
          <AlertCircle className="w-5 h-5 shrink-0 text-rose-400" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-xs font-bold text-stone-300 mb-1">Nome do Salão / Estúdio</label>
          <div className="relative">
            <Building2 className="w-4 h-4 text-amber-400 absolute left-3.5 top-3.5" />
            <input
              type="text"
              required
              placeholder="Ex: Studio Bella Nails"
              value={formData.name}
              onChange={handleNameChange}
              className="w-full bg-stone-900 border border-stone-800 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-100 placeholder-stone-600 focus:outline-none focus:border-amber-400 transition"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-stone-300 mb-1">Link de Agendamento da Cliente</label>
          <div className="relative flex items-center">
            <span className="absolute left-3.5 text-xs text-amber-400/70 font-mono">belasunhas.com.br/</span>
            <input
              type="text"
              required
              placeholder="studio-bella"
              value={formData.slug}
              onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
              className="w-full bg-stone-900 border border-stone-800 rounded-xl pl-38 pr-4 py-3 text-sm text-amber-300 font-mono focus:outline-none focus:border-amber-400 transition"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold text-stone-300 mb-1">Seu Nome / Responsável</label>
            <div className="relative">
              <User className="w-4 h-4 text-amber-400 absolute left-3.5 top-3.5" />
              <input
                type="text"
                required
                placeholder="Ex: Mariana Silva"
                value={formData.ownerName}
                onChange={(e) => setFormData({ ...formData, ownerName: e.target.value })}
                className="w-full bg-stone-900 border border-stone-800 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-100 placeholder-stone-600 focus:outline-none focus:border-amber-400 transition"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-stone-300 mb-1">WhatsApp de Contato</label>
            <div className="relative">
              <Phone className="w-4 h-4 text-amber-400 absolute left-3.5 top-3.5" />
              <input
                type="tel"
                required
                placeholder="(11) 99999-8888"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="w-full bg-stone-900 border border-stone-800 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-100 placeholder-stone-600 focus:outline-none focus:border-amber-400 transition"
              />
            </div>
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-stone-300 mb-1">E-mail de Acesso</label>
          <div className="relative">
            <Mail className="w-4 h-4 text-amber-400 absolute left-3.5 top-3.5" />
            <input
              type="email"
              required
              placeholder="admin@studiobella.com"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full bg-stone-900 border border-stone-800 rounded-xl pl-10 pr-4 py-3 text-sm text-stone-100 placeholder-stone-600 focus:outline-none focus:border-amber-400 transition"
            />
          </div>
        </div>

        {/* Senha e Confirmação de Senha */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold text-stone-300 mb-1">Senha Segura</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-amber-400 absolute left-3.5 top-3.5" />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                placeholder="••••••••"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                className="w-full bg-stone-900 border border-stone-800 rounded-xl pl-10 pr-10 py-3 text-sm text-stone-100 placeholder-stone-600 focus:outline-none focus:border-amber-400 transition"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-3 text-stone-500 hover:text-stone-300 transition cursor-pointer"
                title={showPassword ? 'Ocultar senha' : 'Ver senha'}
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-stone-300 mb-1">Confirmar Senha</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-amber-400 absolute left-3.5 top-3.5" />
              <input
                type={showConfirmPassword ? 'text' : 'password'}
                required
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className={`w-full bg-stone-900 border rounded-xl pl-10 pr-10 py-3 text-sm text-stone-100 placeholder-stone-600 focus:outline-none transition ${
                  confirmPassword && !passwordsMatch
                    ? 'border-rose-500 focus:border-rose-500'
                    : confirmPassword && passwordsMatch
                    ? 'border-emerald-500 focus:border-emerald-500'
                    : 'border-stone-800 focus:border-amber-400'
                }`}
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-3 top-3 text-stone-500 hover:text-stone-300 transition cursor-pointer"
                title={showConfirmPassword ? 'Ocultar confirmação' : 'Ver confirmação'}
              >
                {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>
        </div>

        {/* Indicador visual de requisitos da senha */}
        {formData.password.length > 0 && (
          <div className="p-3 bg-stone-900/60 rounded-xl border border-stone-800 space-y-1.5 text-[11px]">
            <div className="flex items-center gap-2">
              {hasMinLength ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <X className="w-3.5 h-3.5 text-rose-400" />}
              <span className={hasMinLength ? 'text-emerald-400 font-medium' : 'text-stone-400'}>Mínimo de 6 caracteres</span>
            </div>
            <div className="flex items-center gap-2">
              {hasUpper ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <X className="w-3.5 h-3.5 text-stone-500" />}
              <span className={hasUpper ? 'text-emerald-400 font-medium' : 'text-stone-400'}>Pelo menos 1 letra maiúscula (A-Z) (Recomendado)</span>
            </div>
            <div className="flex items-center gap-2">
              {hasNumber ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <X className="w-3.5 h-3.5 text-stone-500" />}
              <span className={hasNumber ? 'text-emerald-400 font-medium' : 'text-stone-400'}>Pelo menos 1 número (0-9) (Recomendado)</span>
            </div>
            {confirmPassword.length > 0 && (
              <div className="flex items-center gap-2 pt-1 border-t border-stone-800">
                {passwordsMatch ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <X className="w-3.5 h-3.5 text-rose-400" />}
                <span className={passwordsMatch ? 'text-emerald-400 font-bold' : 'text-rose-400 font-medium'}>
                  {passwordsMatch ? 'Senhas conferem!' : 'As senhas não coincidem'}
                </span>
              </div>
            )}
          </div>
        )}

        <div>
          <label className="block text-xs font-bold text-stone-300 mb-1.5">Plano Inicial Selecionado</label>
          <div className="grid grid-cols-3 gap-2">
            {(['FREE', 'PRO', 'ENTERPRISE'] as const).map((p) => (
              <button
                key={p}
                type="button"
                onClick={() => setFormData({ ...formData, plan: p })}
                className={`py-2.5 rounded-xl text-xs font-bold border transition cursor-pointer ${
                  formData.plan === p
                    ? 'border-amber-400 bg-amber-500 text-stone-950 shadow-md shadow-amber-500/20'
                    : 'border-stone-800 bg-stone-900 text-stone-400 hover:border-amber-500/30'
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
          className="w-full mt-4 bg-gradient-to-r from-amber-400 via-amber-500 to-amber-600 hover:from-amber-500 hover:to-amber-700 text-stone-950 font-black py-4 rounded-xl shadow-lg shadow-amber-500/20 transition flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer"
        >
          {loading ? (
            <div className="w-5 h-5 border-2 border-stone-950/30 border-t-stone-950 rounded-full animate-spin" />
          ) : (
            <>
              Criar meu Espaço Agora ✨
              <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </form>

      <p className="mt-6 text-center text-xs text-stone-400 font-medium">
        Já tem um cadastro?{' '}
        <Link href="/login" className="font-bold text-amber-400 hover:text-amber-300">
          Fazer Login
        </Link>
      </p>
    </div>
  );
}

export default function SignupPage() {
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
        <h2 className="text-3xl font-extrabold tracking-tight text-stone-100 font-serif">Cadastre o seu Salão</h2>
        <p className="mt-2 text-sm text-stone-400">Receba agendamentos automáticos pelo WhatsApp e Instagram</p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-xl relative z-10">
        <Suspense fallback={<div className="p-8 text-center text-stone-500 text-xs">Carregando formulário...</div>}>
          <SignupForm />
        </Suspense>
      </div>
    </div>
  );
}
