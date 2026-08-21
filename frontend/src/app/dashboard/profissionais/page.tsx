'use client';

import { useState, useEffect } from 'react';
import { Users, Plus, UserCheck, X, AlertCircle } from 'lucide-react';
import { fetchApi } from '@/lib/api';
import { Professional } from '@/types';

export default function ProfessionalsPage() {
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    specialty: 'Manicure & Nail Designer',
    active: true,
  });

  const loadProfessionals = async () => {
    try {
      setLoading(true);
      const data = await fetchApi<Professional[]>('/professionals');
      setProfessionals(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Erro ao carregar profissionais:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfessionals();
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      await fetchApi('/professionals', {
        method: 'POST',
        body: JSON.stringify(formData),
      });

      setShowModal(false);
      setFormData({ name: '', email: '', password: '', specialty: 'Manicure & Nail Designer', active: true });
      loadProfessionals();
    } catch (err: any) {
      setError(err.message || 'Erro ao cadastrar profissional. Verifique os limites do plano.');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white flex items-center gap-2">
            <Users className="w-6 h-6 text-pink-500" />
            Equipe & Manicures
          </h1>
          <p className="text-xs text-zinc-400 mt-1">
            Cadastre os profissionais que atendem no seu salão
          </p>
        </div>

        <button
          onClick={() => setShowModal(true)}
          className="px-4 py-2.5 rounded-xl bg-gradient-to-r from-rose-500 to-pink-600 hover:from-rose-600 hover:to-pink-700 text-white text-xs font-bold shadow-lg shadow-rose-500/25 flex items-center justify-center gap-2 transition"
        >
          <Plus className="w-4 h-4" />
          Adicionar Manicure
        </button>
      </div>

      {/* Grid of Professionals */}
      {loading ? (
        <div className="py-16 text-center text-zinc-500 text-sm">Carregando equipe...</div>
      ) : professionals.length === 0 ? (
        <div className="glass-panel p-12 rounded-3xl border border-white/10 text-center">
          <Users className="w-12 h-12 text-zinc-600 mx-auto mb-3" />
          <h3 className="text-base font-bold text-white mb-1">Nenhum profissional cadastrado</h3>
          <p className="text-xs text-zinc-400 mb-6">
            Adicione manicures e nail designers à sua equipe para liberar suas agendas.
          </p>
          <button
            onClick={() => setShowModal(true)}
            className="px-4 py-2.5 rounded-xl bg-rose-500 text-white text-xs font-bold"
          >
            Adicionar Primeiro Profissional
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {professionals.map((prof) => (
            <div key={prof.id} className="glass-card p-5 rounded-2xl border border-white/10 flex items-center gap-4">
              <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-pink-500 to-rose-600 flex items-center justify-center text-white font-bold text-lg shadow-md shadow-pink-500/20">
                {prof.name.charAt(0).toUpperCase()}
              </div>

              <div className="overflow-hidden flex-1">
                <h3 className="text-sm font-bold text-white truncate">{prof.name}</h3>
                <span className="text-xs text-rose-400 block font-medium">{prof.specialty}</span>
                <span className="text-[11px] text-zinc-500 truncate block mt-0.5">{prof.email}</span>
              </div>

              <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 shrink-0" title="Ativo" />
            </div>
          ))}
        </div>
      )}

      {/* Create Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-panel w-full max-w-md p-6 rounded-3xl border border-white/10 shadow-2xl relative">
            <button
              onClick={() => setShowModal(false)}
              className="absolute top-4 right-4 text-zinc-400 hover:text-white"
            >
              <X className="w-5 h-5" />
            </button>

            <h3 className="text-lg font-bold text-white mb-4">Cadastrar Nova Manicure</h3>

            {error && (
              <div className="mb-4 p-3 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-xs flex items-center gap-2">
                <AlertCircle className="w-4 h-4 shrink-0" />
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-zinc-300 mb-1">Nome Completo</label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Beatriz Lima"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full bg-zinc-900/80 border border-white/10 rounded-xl px-3.5 py-2.5 text-xs text-white placeholder-zinc-500 focus:outline-none focus:border-rose-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-zinc-300 mb-1">E-mail</label>
                <input
                  type="email"
                  required
                  placeholder="beatriz@salao.com"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="w-full bg-zinc-900/80 border border-white/10 rounded-xl px-3.5 py-2.5 text-xs text-white placeholder-zinc-500 focus:outline-none focus:border-rose-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-zinc-300 mb-1">Especialidade</label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Nail Designer / Alongamento Fibra"
                  value={formData.specialty}
                  onChange={(e) => setFormData({ ...formData, specialty: e.target.value })}
                  className="w-full bg-zinc-900/80 border border-white/10 rounded-xl px-3.5 py-2.5 text-xs text-white placeholder-zinc-500 focus:outline-none focus:border-rose-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-zinc-300 mb-1">Senha Provisória</label>
                <input
                  type="password"
                  required
                  placeholder="••••••••"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  className="w-full bg-zinc-900/80 border border-white/10 rounded-xl px-3.5 py-2.5 text-xs text-white placeholder-zinc-500 focus:outline-none focus:border-rose-500"
                />
              </div>

              <div className="pt-2 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 text-xs font-semibold text-zinc-400 hover:text-white"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-pink-500 hover:bg-pink-600 text-white text-xs font-bold transition shadow-lg shadow-pink-500/25"
                >
                  Salvar Manicure
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
