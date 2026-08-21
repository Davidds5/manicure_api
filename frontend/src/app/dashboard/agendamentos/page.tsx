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
      <div className="flex items-center gap-2 border-b border-white/10 pb-3 overflow-x-auto text-xs font-medium">
        {['ALL', 'SCHEDULED', 'CONFIRMED', 'COMPLETED', 'CANCELLED'].map((tab) => (
          <button
            key={tab}
            onClick={() => setFilterStatus(tab)}
            className={`px-4 py-2 rounded-xl transition ${
              filterStatus === tab
                ? 'bg-rose-500 text-white font-bold shadow-lg shadow-rose-500/20'
                : 'text-zinc-400 hover:text-white hover:bg-white/5'
            }`}
          >
            {tab === 'ALL' ? 'Todos' : tab === 'SCHEDULED' ? 'Pendentes' : tab === 'CONFIRMED' ? 'Confirmados' : tab === 'COMPLETED' ? 'Concluídos' : 'Cancelados'}
          </button>
        ))}
      </div>

      {/* Appointments List */}
      {loading ? (
        <div className="py-16 text-center text-zinc-500 text-sm">Carregando agenda...</div>
      ) : filtered.length === 0 ? (
        <div className="glass-panel p-12 rounded-3xl border border-white/10 text-center">
          <Calendar className="w-12 h-12 text-zinc-600 mx-auto mb-3" />
          <h3 className="text-base font-bold text-white mb-1">Nenhum agendamento encontrado</h3>
          <p className="text-xs text-zinc-400">
            Nenhum registro com o filtro selecionado no momento.
          </p>
        </div>
      ) : (
        <div className="glass-panel rounded-3xl border border-white/10 overflow-hidden">
          <div className="divide-y divide-white/5">
            {filtered.map((appt) => (
              <div key={appt.id} className="p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-white/[0.02] transition">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 rounded-2xl bg-rose-500/10 border border-rose-500/20 flex flex-col items-center justify-center text-rose-400">
                    <span className="text-xs font-bold leading-none">
                      {new Date(appt.dateTime).getDate()}
                    </span>
                    <span className="text-[9px] uppercase font-bold text-zinc-400">
                      {new Date(appt.dateTime).toLocaleDateString('pt-BR', { month: 'short' }).replace('.', '')}
                    </span>
                  </div>

                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="text-sm font-bold text-white">{appt.clientName}</h3>
                      {getStatusBadge(appt.status)}
                    </div>
                    <span className="text-xs text-zinc-400 block mt-0.5">
                      💅 {appt.serviceName} • 👤 {appt.professionalName}
                    </span>
                    <span className="text-[11px] text-zinc-500 block">
                      ⏰ Horário: {new Date(appt.dateTime).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}
                    </span>
                  </div>
                </div>

                <div className="flex items-center justify-between sm:justify-end gap-4 border-t sm:border-t-0 border-white/5 pt-3 sm:pt-0">
                  <span className="text-sm font-bold text-emerald-400">
                    R$ {appt.servicePrice ? appt.servicePrice.toFixed(2).replace('.', ',') : '0,00'}
                  </span>

                  <div className="flex items-center gap-1.5">
                    {appt.status === 'SCHEDULED' && (
                      <button
                        onClick={() => handleStatusUpdate(appt.id, 'CONFIRMED')}
                        className="px-3 py-1.5 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 border border-emerald-500/20 text-xs font-semibold transition"
                      >
                        Confirmar
                      </button>
                    )}
                    {appt.status === 'CONFIRMED' && (
                      <button
                        onClick={() => handleStatusUpdate(appt.id, 'COMPLETED')}
                        className="px-3 py-1.5 rounded-lg bg-blue-500/10 hover:bg-blue-500/20 text-blue-400 border border-blue-500/20 text-xs font-semibold transition"
                      >
                        Concluir
                      </button>
                    )}
                    {appt.status !== 'CANCELLED' && appt.status !== 'COMPLETED' && (
                      <button
                        onClick={() => handleStatusUpdate(appt.id, 'CANCELLED')}
                        className="px-3 py-1.5 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/20 text-xs font-semibold transition"
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
