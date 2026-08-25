'use client';

import { useState, useEffect } from 'react';
import { Scissors, Plus, Trash2, Edit3, Check, X, AlertCircle } from 'lucide-react';
import { fetchApi } from '@/lib/api';
import { ServiceItem } from '@/types';

export default function ServicesPage() {
  const [services, setServices] = useState<ServiceItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    duration: '60',
    active: true,
  });

  const loadServices = async () => {
    try {
      setLoading(true);
      const data = await fetchApi<ServiceItem[]>('/services');
      setServices(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Erro ao listar serviços:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadServices();
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    try {
      await fetchApi('/services', {
        method: 'POST',
        body: JSON.stringify({
          name: formData.name,
          description: formData.description,
          price: parseFloat(formData.price),
          duration: parseInt(formData.duration, 10),
          active: formData.active,
        }),
      });

      setShowModal(false);
      setFormData({ name: '', description: '', price: '', duration: '60', active: true });
      loadServices();
    } catch (err: any) {
      setError(err.message || 'Erro ao criar serviço.');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Deseja realmente excluir este serviço?')) return;
    try {
      await fetchApi(`/services/${id}`, { method: 'DELETE' });
      loadServices();
    } catch (err: any) {
      alert(err.message || 'Erro ao excluir serviço.');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white flex items-center gap-2">
            <Scissors className="w-6 h-6 text-rose-500" />
            Catálogo de Serviços
          </h1>
          <p className="text-xs text-zinc-400 mt-1">
            Configure os procedimentos, durações e preços oferecidos no seu salão
          </p>
        </div>

        <button
          onClick={() => setShowModal(true)}
          className="px-4 py-2.5 rounded-xl bg-gradient-to-r from-rose-500 to-pink-600 hover:from-rose-600 hover:to-pink-700 text-white text-xs font-bold shadow-lg shadow-rose-500/25 flex items-center justify-center gap-2 transition"
        >
          <Plus className="w-4 h-4" />
          Novo Serviço
        </button>
      </div>

      {/* Grid of Services */}
      {loading ? (
        <div className="py-16 text-center text-stone-400 text-sm font-medium">Carregando catálogo de serviços...</div>
      ) : services.length === 0 ? (
        <div className="bg-[#1c1815] p-12 rounded-3xl border border-stone-800 text-center shadow-xl">
          <div className="w-16 h-16 rounded-2xl bg-pink-500/10 border border-pink-500/20 flex items-center justify-center mx-auto mb-4 text-pink-400">
            <Scissors className="w-8 h-8" />
          </div>
          <h3 className="text-xl font-bold text-white mb-2">Nenhum serviço cadastrado ainda</h3>
          <p className="text-sm text-stone-400 max-w-md mx-auto mb-6">
            Cadastre os procedimentos que você realiza (ex: Pé e Mão, Manicure Simples, Alongamento, Esmaltação em Gel) com seus preços e tempos de atendimento.
          </p>
          <button
            onClick={() => setShowModal(true)}
            className="px-6 py-3.5 rounded-xl bg-gradient-to-r from-pink-500 to-rose-600 hover:from-pink-600 hover:to-rose-700 text-white text-sm font-bold shadow-lg shadow-pink-500/20 transition cursor-pointer"
          >
            + Cadastrar Meu Primeiro Serviço
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {services.map((svc) => (
            <div key={svc.id} className="bg-[#1c1815] p-6 rounded-2xl border border-stone-800 flex flex-col justify-between hover:border-pink-500/40 transition shadow-lg">
              <div>
                <div className="flex items-start justify-between gap-3 mb-3">
                  <h3 className="text-base font-bold text-white">{svc.name}</h3>
                  <span className="text-sm font-extrabold text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-3 py-1 rounded-lg">
                    R$ {Number(svc.price).toFixed(2).replace('.', ',')}
                  </span>
                </div>
                <p className="text-xs text-stone-400 line-clamp-2 mb-4 leading-relaxed">{svc.description || 'Sem descrição informada.'}</p>
              </div>

              <div className="border-t border-stone-800 pt-4 flex items-center justify-between text-xs text-stone-400">
                <span className="flex items-center gap-1.5 font-medium text-stone-300">
                  ⏱️ {svc.duration} minutos
                </span>
                <button
                  onClick={() => handleDelete(svc.id)}
                  className="p-2 rounded-xl text-stone-400 hover:text-rose-400 hover:bg-rose-500/10 border border-transparent hover:border-rose-500/20 transition cursor-pointer"
                  title="Excluir Serviço"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create Modal com Alto Contraste e Legibilidade */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="bg-[#1c1815] w-full max-w-lg p-6 sm:p-8 rounded-3xl border border-stone-700 shadow-2xl relative text-stone-100">
            <button
              onClick={() => setShowModal(false)}
              className="absolute top-5 right-5 text-stone-400 hover:text-white p-1 rounded-lg hover:bg-stone-800 transition"
            >
              <X className="w-5 h-5" />
            </button>

            <div className="mb-6">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Scissors className="w-5 h-5 text-pink-500" />
                Adicionar Novo Serviço
              </h3>
              <p className="text-xs text-stone-400 mt-1">
                Preencha os detalhes do procedimento para que suas clientes possam agendar
              </p>
            </div>

            {/* Presets Rápidos */}
            <div className="mb-5">
              <span className="block text-[11px] font-bold uppercase tracking-wider text-stone-400 mb-2">
                Sugestões Rápidas (Clique para preencher):
              </span>
              <div className="flex flex-wrap gap-1.5">
                {[
                  { name: 'Mão e Pé Completo', price: '70.00', duration: '60', desc: 'Cuticulagem, esmaltação e hidratação para mãos e pés' },
                  { name: 'Manicure Simples', price: '35.00', duration: '35', desc: 'Cuticulagem e esmaltação tradicional das unhas das mãos' },
                  { name: 'Pedicure Tradicional', price: '40.00', duration: '40', desc: 'Cutilagem, esfoliação e esmaltação dos pés' },
                  { name: 'Esmaltação em Gel', price: '75.00', duration: '45', desc: 'Secagem em cabine LED com alta durabilidade' },
                  { name: 'Alongamento em Fibra', price: '150.00', duration: '120', desc: 'Alongamento com fibra de vidro e acabamento natural' },
                ].map((preset) => (
                  <button
                    key={preset.name}
                    type="button"
                    onClick={() => setFormData({
                      name: preset.name,
                      price: preset.price,
                      duration: preset.duration,
                      description: preset.desc,
                      active: true
                    })}
                    className="text-[11px] px-2.5 py-1 rounded-lg bg-stone-900 border border-stone-700 text-stone-300 hover:border-pink-500 hover:text-white transition cursor-pointer"
                  >
                    + {preset.name}
                  </button>
                ))}
              </div>
            </div>

            {error && (
              <div className="mb-5 p-4 rounded-xl bg-rose-950/50 border border-rose-500/40 text-rose-300 text-xs flex items-center gap-3">
                <AlertCircle className="w-4 h-4 shrink-0 text-rose-400" />
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-stone-200 mb-1.5">Nome do Procedimento *</label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Mão e Pé, Alongamento em Gel, etc."
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-200 mb-1.5">Descrição do que está incluso</label>
                <textarea
                  rows={2}
                  placeholder="Ex: Cutilagem funda, hidratação, esmaltação e massagem relaxante..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-2.5 text-sm text-white placeholder-stone-500 focus:outline-none transition resize-none shadow-inner"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-stone-200 mb-1.5">Preço Cobrado (R$) *</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    placeholder="Ex: 65.00"
                    value={formData.price}
                    onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                    className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner font-semibold"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-stone-200 mb-1.5">Tempo Médio (minutos) *</label>
                  <input
                    type="number"
                    required
                    placeholder="Ex: 50"
                    value={formData.duration}
                    onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                    className="w-full bg-[#12100e] border border-stone-700 focus:border-pink-500 rounded-xl px-4 py-3 text-sm text-white placeholder-stone-500 focus:outline-none transition shadow-inner font-semibold"
                  />
                </div>
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
                  Salvar Serviço ✨
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
