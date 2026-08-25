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
        <div className="py-16 text-center text-stone-400 text-sm font-medium">Carregando equipe de manicures...</div>
      ) : professionals.length === 0 ? (
        <div className="bg-[#1c1815] p-12 rounded-3xl border border-stone-800 text-center shadow-xl">
          <div className="w-16 h-16 rounded-2xl bg-pink-500/10 border border-pink-500/20 flex items-center justify-center mx-auto mb-4 text-pink-400">
            <Users className="w-8 h-8" />
          </div>
          <h3 className="text-xl font-bold text-white mb-2">Nenhum profissional cadastrado ainda</h3>
          <p className="text-sm text-stone-400 max-w-md mx-auto mb-6">
            Adicione manicures, pedicures e nail designers à sua equipe para liberar suas agendas individuais.
          </p>
          <button
            onClick={() => setShowModal(true)}
            className="px-6 py-3.5 rounded-xl bg-gradient-to-r from-pink-500 to-rose-600 hover:from-pink-600 hover:to-rose-700 text-white text-sm font-bold shadow-lg shadow-pink-500/20 transition cursor-pointer"
          >
            + Adicionar Primeira Manicure
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {professionals.map((prof) => (
            <div key={prof.id} className="bg-[#1c1815] p-6 rounded-2xl border border-stone-800 flex items-center gap-4 hover:border-pink-500/40 transition shadow-lg">
              <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-pink-500 to-rose-600 flex items-center justify-center text-white font-bold text-xl shadow-md shadow-pink-500/20 shrink-0">
                {prof.name.charAt(0).toUpperCase()}
              </div>

              <div className="overflow-hidden flex-1">
                <h3 className="text-base font-bold text-white truncate">{prof.name}</h3>
                <span className="text-xs text-pink-400 block font-semibold mt-0.5">{prof.specialty}</span>
                <span className="text-xs text-stone-400 truncate block mt-1 font-mono">{prof.email}</span>
              </div>

              <span className="w-3 h-3 rounded-full bg-emerald-400 shrink-0 shadow-sm shadow-emerald-400/50" title="Ativo" />
            </div>
          ))}
        </div>
      )}

      {/* Create Modal com Alto Contraste */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="bg-[#1c1815] w-full max-w-lg p-6 sm:p-8 rounded-3xl border border-stone-700 shadow-2xl relative text-stone-100">
            <button
              onClick={() => setShowModal(false)}
              className="absolute top-5 right-5 text-stone-400 hover:text-white p-1 rounded-lg hover:bg-stone-800 transition cursor-pointer"
            >
              <X className="w-5 h-5" />
            </button>

            <div className="mb-6">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Users className="w-5 h-5 text-pink-500" />
                Cadastrar Nova Manicure
              </h3>
              <p className="text-xs text-stone-400 mt-1">
                Adicione uma nova profissional à equipe para que ela receba agendamentos
              </p>
            </div>

            {error && (
              <div className="mb-5 p-4 rounded-xl bg-rose-950/50 border border-rose-500/40 text-rose-300 text-xs flex items-center gap-3">
                <AlertCircle className="w-4 h-4 shrink-0 text-rose-400" />
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-stone-200 mb-1.5">Nome Completo *</label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Beatriz Lima"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-200 mb-1.5">E-mail de Acesso *</label>
                <input
                  type="email"
                  required
                  placeholder="beatriz@salao.com"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-200 mb-1.5">Especialidade / Cargo *</label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Manicure, Pedicure & Nail Designer"
                  value={formData.specialty}
                  onChange={(e) => setFormData({ ...formData, specialty: e.target.value })}
                  className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-200 mb-1.5">Senha Provisória de Acesso *</label>
                <input
                  type="password"
                  required
                  placeholder="••••••••"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner"
                />
              </div>

              <div className="pt-4 flex items-center justify-end gap-3 border-t border-stone-800 mt-6">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-5 py-2.5 rounded-xl border border-stone-700 text-xs font-semibold text-stone-400 hover:text-white hover:bg-stone-800 transition cursor-pointer"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-pink-500 to-rose-600 hover:from-pink-600 hover:to-rose-700 text-white text-xs font-bold transition shadow-lg shadow-pink-500/25 cursor-pointer"
                >
                  Salvar Manicure ✨
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
