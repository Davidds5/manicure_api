'use client';

import { useState, useEffect } from 'react';
import { Calendar, CheckCircle2, Clock, XCircle, AlertCircle, RefreshCw } from 'lucide-react';
import { fetchApi } from '@/lib/api';
import { Appointment, AppointmentStatus } from '@/types';

export default function AppointmentsPage() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState<string>('ALL');

  const loadAppointments = async () => {
    try {
      setLoading(true);
      const res = await fetchApi<{ content?: Appointment[] } | Appointment[]>('/appointments?size=50');
      const list = Array.isArray(res) ? res : res.content || [];
      setAppointments(list);
    } catch (err) {
      console.error('Erro ao carregar agendamentos:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAppointments();
  }, []);

  const handleStatusUpdate = async (id: number, status: AppointmentStatus) => {
    try {
      await fetchApi(`/appointments/${id}`, {
        method: 'PUT',
        body: JSON.stringify({ status }),
      });
      loadAppointments();
    } catch (err: any) {
      alert(err.message || 'Erro ao atualizar status.');
    }
  };

  const filtered = filterStatus === 'ALL' 
    ? appointments 
    : appointments.filter((a) => a.status === filterStatus);

  const getStatusBadge = (status: AppointmentStatus) => {
    switch (status) {
      case 'CONFIRMED':
        return <span className="px-2.5 py-1 rounded-lg text-[11px] font-bold bg-emerald-500/10 border border-emerald-500/20 text-emerald-400">Confirmado</span>;
      case 'COMPLETED':
        return <span className="px-2.5 py-1 rounded-lg text-[11px] font-bold bg-blue-500/10 border border-blue-500/20 text-blue-400">Concluído</span>;
      case 'CANCELLED':
        return <span className="px-2.5 py-1 rounded-lg text-[11px] font-bold bg-rose-500/10 border border-rose-500/20 text-rose-400">Cancelado</span>;
      default:
        return <span className="px-2.5 py-1 rounded-lg text-[11px] font-bold bg-amber-500/10 border border-amber-500/20 text-amber-400">Agendado</span>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white flex items-center gap-2">
            <Calendar className="w-6 h-6 text-rose-500" />
            Agenda & Atendimentos
          </h1>
          <p className="text-xs text-zinc-400 mt-1">
            Controle de todos os horários marcados pelas clientes
          </p>
        </div>

        <button
          onClick={loadAppointments}
          className="p-2.5 rounded-xl border border-white/10 bg-white/5 hover:bg-white/10 text-zinc-300 hover:text-white transition flex items-center gap-2 text-xs font-semibold"
        >
          <RefreshCw className="w-4 h-4" />
          Atualizar Lista
        </button>
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center gap-2 border-b border-stone-800 pb-3 overflow-x-auto text-xs font-medium">
        {['ALL', 'SCHEDULED', 'CONFIRMED', 'COMPLETED', 'CANCELLED'].map((tab) => (
          <button
            key={tab}
            onClick={() => setFilterStatus(tab)}
            className={`px-4 py-2.5 rounded-xl transition cursor-pointer font-bold ${
              filterStatus === tab
                ? 'bg-pink-600 text-white shadow-md shadow-pink-600/25'
                : 'bg-stone-900 border border-stone-800 text-stone-400 hover:text-white hover:border-stone-700'
            }`}
          >
            {tab === 'ALL' ? 'Todos' : tab === 'SCHEDULED' ? 'Pendentes' : tab === 'CONFIRMED' ? 'Confirmados' : tab === 'COMPLETED' ? 'Concluídos' : 'Cancelados'}
          </button>
        ))}
      </div>

      {/* Appointments List */}
      {loading ? (
        <div className="py-16 text-center text-stone-400 text-sm font-medium">Carregando agendamentos...</div>
      ) : filtered.length === 0 ? (
        <div className="bg-[#1c1815] p-12 rounded-3xl border border-stone-800 text-center shadow-xl">
          <div className="w-16 h-16 rounded-2xl bg-pink-500/10 border border-pink-500/20 flex items-center justify-center mx-auto mb-4 text-pink-400">
            <Calendar className="w-8 h-8" />
          </div>
          <h3 className="text-xl font-bold text-white mb-2">Nenhum agendamento encontrado</h3>
          <p className="text-sm text-stone-400 max-w-md mx-auto">
            Não há nenhum agendamento com o status selecionado. Compartilhe o link do seu portal para suas clientes agendarem!
          </p>
        </div>
      ) : (
        <div className="bg-[#1c1815] rounded-3xl border border-stone-800 overflow-hidden shadow-xl">
          <div className="divide-y divide-stone-800">
            {filtered.map((appt) => (
              <div key={appt.id} className="p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-stone-900/40 transition">
                <div className="flex items-center gap-4">
                  <div className="w-13 h-13 rounded-2xl bg-[#12100e] border border-stone-700 flex flex-col items-center justify-center text-pink-400 shrink-0">
                    <span className="text-base font-extrabold leading-none">
                      {new Date(appt.dateTime).getDate()}
                    </span>
                    <span className="text-[10px] uppercase font-bold text-stone-400 mt-0.5">
                      {new Date(appt.dateTime).toLocaleDateString('pt-BR', { month: 'short' }).replace('.', '')}
                    </span>
                  </div>

                  <div>
                    <div className="flex items-center gap-2.5 mb-1">
                      <h3 className="text-base font-bold text-white">{appt.clientName}</h3>
                      {getStatusBadge(appt.status)}
                    </div>
                    <span className="text-xs text-stone-300 block font-medium">
                      💅 {appt.serviceName} • 👤 {appt.professionalName}
                    </span>
                    <span className="text-xs text-pink-400/90 block mt-0.5 font-semibold">
                      ⏰ {new Date(appt.dateTime).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })} • 📞 {appt.clientPhone || 'WhatsApp não informado'}
                    </span>
                  </div>
                </div>

                <div className="flex items-center justify-between sm:justify-end gap-4 border-t sm:border-t-0 border-stone-800 pt-3 sm:pt-0">
                  <span className="text-base font-extrabold text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-3 py-1 rounded-xl">
                    R$ {appt.servicePrice ? appt.servicePrice.toFixed(2).replace('.', ',') : '0,00'}
                  </span>

                  <div className="flex items-center gap-2">
                    {appt.status === 'SCHEDULED' && (
                      <button
                        onClick={() => handleStatusUpdate(appt.id, 'CONFIRMED')}
                        className="px-3.5 py-2 rounded-xl bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs font-bold transition cursor-pointer"
                      >
                        Confirmar
                      </button>
                    )}
                    {appt.status === 'CONFIRMED' && (
                      <button
                        onClick={() => handleStatusUpdate(appt.id, 'COMPLETED')}
                        className="px-3.5 py-2 rounded-xl bg-blue-500/10 hover:bg-blue-500/20 text-blue-400 border border-blue-500/30 text-xs font-bold transition cursor-pointer"
                      >
                        Concluir
                      </button>
                    )}
                    {appt.status !== 'CANCELLED' && appt.status !== 'COMPLETED' && (
                      <button
                        onClick={() => handleStatusUpdate(appt.id, 'CANCELLED')}
                        className="px-3.5 py-2 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 text-xs font-bold transition cursor-pointer"
                      >
                        Cancelar
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
