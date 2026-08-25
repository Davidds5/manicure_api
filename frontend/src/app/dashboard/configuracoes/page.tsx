'use client';

import { useState, useEffect } from 'react';
import { Settings, Shield, Store, Palette, AlertCircle, CheckCircle2, Zap } from 'lucide-react';
import { fetchApi } from '@/lib/api';
import { TenantDetails } from '@/types';

export default function SettingsPage() {
  const [tenant, setTenant] = useState<TenantDetails | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    logoUrl: '',
    brandColor: '#f43f5e',
  });

  const loadTenant = async () => {
    try {
      setLoading(true);
      const data = await fetchApi<TenantDetails>('/tenants/me');
      setTenant(data);
      setFormData({
        name: data.name || '',
        logoUrl: data.logoUrl || '',
        brandColor: data.brandColor || '#f43f5e',
      });
    } catch (err: any) {
      setError(err.message || 'Erro ao carregar dados do salão.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTenant();
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(false);

    try {
      await fetchApi('/tenants/me', {
        method: 'PATCH',
        body: JSON.stringify(formData),
      });

      setSuccess(true);
      loadTenant();
    } catch (err: any) {
      setError(err.message || 'Erro ao atualizar configurações.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-8 max-w-4xl">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-extrabold text-white flex items-center gap-2">
          <Settings className="w-6 h-6 text-rose-500" />
          Configurações do Salão
        </h1>
        <p className="text-xs text-zinc-400 mt-1">
          Personalize a identidade visual do seu espaço e gerencie sua assinatura SaaS
        </p>
      </div>

      {success && (
        <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-300 text-sm flex items-center gap-3">
          <CheckCircle2 className="w-5 h-5 shrink-0 text-emerald-400" />
          <span>Configurações atualizadas com sucesso!</span>
        </div>
      )}

      {error && (
        <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-sm flex items-center gap-3">
          <AlertCircle className="w-5 h-5 shrink-0 text-rose-400" />
          <span>{error}</span>
        </div>
      )}

      {/* Branding Form */}
      <div className="bg-[#1c1815] p-6 sm:p-8 rounded-3xl border border-stone-800 shadow-xl">
        <h2 className="text-base font-bold text-white mb-1 flex items-center gap-2">
          <Store className="w-4 h-4 text-pink-500" />
          Identidade Visual & Marca
        </h2>
        <p className="text-xs text-stone-400 mb-6">
          Estas informações serão exibidas no portal de agendamento das suas clientes
        </p>

        <form onSubmit={handleSave} className="space-y-5">
          <div>
            <label className="block text-xs font-bold text-stone-200 mb-1.5">Nome do Salão / Estúdio *</label>
            <input
              type="text"
              required
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner font-medium"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-stone-200 mb-1.5">Cor Primária da Marca</label>
              <div className="flex items-center gap-3 bg-[#12100e] border border-stone-700 rounded-xl p-2">
                <input
                  type="color"
                  value={formData.brandColor}
                  onChange={(e) => setFormData({ ...formData, brandColor: e.target.value })}
                  className="w-10 h-10 rounded-lg bg-transparent cursor-pointer border-0"
                />
                <span className="text-xs font-mono text-stone-200 font-bold">{formData.brandColor}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-stone-200 mb-1.5">URL da Logo (Opcional)</label>
              <input
                type="url"
                placeholder="https://meusalao.com/logo.png"
                value={formData.logoUrl}
                onChange={(e) => setFormData({ ...formData, logoUrl: e.target.value })}
                className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner font-medium"
              />
            </div>
          </div>

          <div className="pt-4 flex justify-end border-t border-stone-800">
            <button
              type="submit"
              disabled={saving}
              className="px-6 py-3 rounded-xl bg-gradient-to-r from-pink-500 to-rose-600 hover:from-pink-600 hover:to-rose-700 text-white text-xs font-bold shadow-lg shadow-pink-500/25 transition flex items-center gap-2 disabled:opacity-50 cursor-pointer"
            >
              {saving ? 'Salvando...' : 'Salvar Alterações ✨'}
            </button>
          </div>
        </form>
      </div>

      {/* Subscription Plan Card */}
      <div className="bg-[#1c1815] p-6 sm:p-8 rounded-3xl border border-stone-800 shadow-xl relative overflow-hidden">
        <div className="flex items-start justify-between gap-4 mb-6">
          <div>
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <Zap className="w-4 h-4 text-amber-400" />
              Plano & Assinatura do SaaS
            </h2>
            <p className="text-xs text-stone-400 mt-0.5">
              Status da cota e limites do seu salão
            </p>
          </div>

          <span className="px-3.5 py-1.5 rounded-xl text-xs font-extrabold bg-pink-500/10 border border-pink-500/30 text-pink-400 uppercase">
            Plano {tenant?.plan || 'PRO'}
          </span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="bg-[#12100e] p-5 rounded-2xl border border-stone-800">
            <span className="text-xs text-stone-400 block mb-1 font-semibold">Status da Assinatura</span>
            <span className="text-sm font-extrabold text-emerald-400 flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              {tenant?.subscriptionStatus || 'ATIVO'}
            </span>
          </div>

          <div className="bg-[#12100e] p-5 rounded-2xl border border-stone-800">
            <span className="text-xs text-stone-400 block mb-1 font-semibold">Limite de Manicures</span>
            <span className="text-sm font-extrabold text-white">
              {tenant?.maxProfessionals || 10} Profissionais
            </span>
          </div>

          <div className="bg-[#12100e] p-5 rounded-2xl border border-stone-800">
            <span className="text-xs text-stone-400 block mb-1 font-semibold">Limite Mensal</span>
            <span className="text-sm font-extrabold text-white">
              {tenant?.maxAppointmentsPerMonth || 1000} Agendamentos
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
