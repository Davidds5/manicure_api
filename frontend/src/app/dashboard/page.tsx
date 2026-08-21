'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { 
  Calendar, 
  TrendingUp, 
  Users, 
  Scissors, 
  Clock, 
  CheckCircle2, 
  AlertCircle, 
  ArrowRight,
  ExternalLink,
  Plus
} from 'lucide-react';
import { fetchApi } from '@/lib/api';
import { Appointment, Professional, ServiceItem, TenantDetails } from '@/types';

export default function DashboardOverviewPage() {
  const [tenant, setTenant] = useState<TenantDetails | null>(null);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [services, setServices] = useState<ServiceItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDashboardData() {
      try {
        const [tenantData, apptsData, profsData, servsData] = await Promise.allSettled([
          fetchApi<TenantDetails>('/tenants/me'),
          fetchApi<{ content?: Appointment[] } | Appointment[]>('/appointments?size=10'),
          fetchApi<Professional[]>('/professionals'),
          fetchApi<ServiceItem[]>('/services'),
        ]);

        if (tenantData.status === 'fulfilled') setTenant(tenantData.value);
        if (apptsData.status === 'fulfilled') {
          const res = apptsData.value;
          setAppointments(Array.isArray(res) ? res : res.content || []);
        }
        if (profsData.status === 'fulfilled') {
          setProfessionals(Array.isArray(profsData.value) ? profsData.value : []);
        }
        if (servsData.status === 'fulfilled') {
          setServices(Array.isArray(servsData.value) ? servsData.value : []);
        }
      } catch (err) {
        console.error('Erro ao carregar dados do dashboard:', err);
      } finally {
        setLoading(false);
      }
    }

    loadDashboardData();
  }, []);

  const totalEarnings = appointments
    .filter((a) => a.status === 'COMPLETED' || a.status === 'CONFIRMED')
    .reduce((acc, curr) => acc + (curr.servicePrice || 0), 0);

  return (
    <div className="space-y-8">
      {/* Welcome Banner com Alto Contraste & Botão de Destaque */}
      <div className="p-6 sm:p-8 rounded-3xl border border-stone-800 flex flex-col md:flex-row md:items-center justify-between gap-6 relative overflow-hidden bg-[#1f1b18] shadow-lg">
        <div className="relative z-10">
          <span className="text-xs font-bold uppercase tracking-wider text-pink-400 block mb-1">
            Painel do Estúdio
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white font-serif">
            Olá, {tenant?.name || 'Studio Bella'}! ✨
          </h1>
          <p className="text-sm text-stone-300 mt-1 max-w-xl font-normal leading-relaxed">
            Acompanhe seus agendamentos em tempo real, gerencie sua equipe de manicures e compartilhe o link de auto-agendamento com suas clientes.
          </p>
        </div>

        {tenant?.slug && (
          <div className="relative z-10 flex flex-col sm:flex-row items-stretch sm:items-center gap-3">
            <a
              href={`/agendar/${tenant.slug}`}
              target="_blank"
              rel="noreferrer"
              className="px-6 py-3.5 rounded-xl bg-pink-600 hover:bg-pink-700 text-white text-xs font-bold shadow-md shadow-pink-600/25 flex items-center justify-center gap-2 transition cursor-pointer"
            >
              <span>Abrir Portal de Agendamento</span>
              <ExternalLink className="w-4 h-4" />
            </a>
          </div>
        )}
      </div>

      {/* KPI Cards com Alto Contraste e Legibilidade Cristalina */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-[#1f1b18] p-6 rounded-2xl border border-stone-800 shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs text-stone-400 font-semibold">Faturamento Estimado</span>
            <div className="w-8 h-8 rounded-lg bg-emerald-950/60 text-emerald-400 border border-emerald-500/30 flex items-center justify-center font-bold">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-extrabold text-white font-serif">
            R$ {totalEarnings.toFixed(2).replace('.', ',')}
          </div>
          <p className="text-[11px] text-stone-400 mt-1 font-medium">Agendamentos confirmados</p>
        </div>

        <div className="bg-[#1f1b18] p-6 rounded-2xl border border-stone-800 shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs text-stone-400 font-semibold">Agendamentos</span>
            <div className="w-8 h-8 rounded-lg bg-pink-950/60 text-pink-400 border border-pink-500/30 flex items-center justify-center font-bold">
              <Calendar className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-extrabold text-white font-serif">
            {appointments.length} <span className="text-sm font-normal text-stone-400">Total</span>
          </div>
          <p className="text-[11px] text-stone-400 mt-1 font-medium">
            {appointments.filter((a) => a.status === 'SCHEDULED').length} pendentes
          </p>
        </div>

        <div className="bg-[#1f1b18] p-6 rounded-2xl border border-stone-800 shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs text-stone-400 font-semibold">Profissionais</span>
            <div className="w-8 h-8 rounded-lg bg-stone-800 text-stone-200 border border-stone-700 flex items-center justify-center font-bold">
              <Users className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-extrabold text-white font-serif">
            {professionals.length} <span className="text-sm font-normal text-stone-400">Cadastrados</span>
          </div>
          <p className="text-[11px] text-stone-400 mt-1 font-medium">
            Limite: 1 no plano FREE
          </p>
        </div>

        <div className="bg-[#1f1b18] p-6 rounded-2xl border border-stone-800 shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <span className="text-xs text-stone-400 font-semibold">Serviços no Catálogo</span>
            <div className="w-8 h-8 rounded-lg bg-pink-950/60 text-pink-400 border border-pink-500/30 flex items-center justify-center font-bold">
              <Scissors className="w-4 h-4" />
            </div>
          </div>
          <div className="text-2xl font-extrabold text-white font-serif">
            {services.length} <span className="text-sm font-normal text-stone-400">Ativos</span>
          </div>
          <p className="text-[11px] text-stone-400 mt-1 font-medium">
            Prontos para agendamento
          </p>
        </div>
      </div>

      {/* Recent Appointments & Quick Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Appointments List */}
        <div className="lg:col-span-2 bg-[#14110f] p-6 sm:p-7 rounded-3xl border border-amber-500/20 shadow-xl">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-lg font-bold text-stone-100 font-serif">Próximos Agendamentos</h2>
              <p className="text-xs text-stone-400">Atendimentos recentes do salão</p>
            </div>
            <Link
              href="/dashboard/agendamentos"
              className="text-xs font-bold text-amber-400 hover:text-amber-300 flex items-center gap-1"
            >
              Ver todos <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          {appointments.length === 0 ? (
            <div className="py-12 text-center text-stone-500 text-sm font-medium">
              Nenhum agendamento registrado ainda. Compartilhe o link do seu portal com as clientes!
            </div>
          ) : (
            <div className="divide-y divide-stone-800">
              {appointments.slice(0, 5).map((appt) => (
                <div key={appt.id} className="py-4 flex items-center justify-between gap-4">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-stone-900 border border-amber-500/20 flex items-center justify-center text-amber-400 font-bold text-xs">
                      {appt.clientName ? appt.clientName.substring(0, 2).toUpperCase() : 'CL'}
                    </div>
                    <div>
                      <span className="text-sm font-bold text-stone-100 block">{appt.clientName}</span>
                      <span className="text-xs text-stone-400">
                        {appt.serviceName} • {appt.professionalName}
                      </span>
                    </div>
                  </div>

                  <div className="text-right">
                    <span className="text-xs font-extrabold text-amber-400 block">
                      R$ {appt.servicePrice ? appt.servicePrice.toFixed(2).replace('.', ',') : '0,00'}
                    </span>
                    <span className="text-[10px] text-stone-500 font-medium">
                      {new Date(appt.dateTime).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Quick Links / Status Panel */}
        <div className="bg-[#14110f] p-6 sm:p-7 rounded-3xl border border-amber-500/20 shadow-xl space-y-6">
          <div>
            <h2 className="text-lg font-bold text-stone-100 font-serif mb-1">Ações Rápidas</h2>
            <p className="text-xs text-stone-400">Gerencie a estrutura do seu salão</p>
          </div>

          <div className="space-y-3">
            <Link
              href="/dashboard/servicos"
              className="flex items-center justify-between p-4 rounded-xl border border-stone-800 bg-stone-900/60 hover:bg-stone-900 hover:border-amber-500/30 transition text-sm text-stone-200 font-semibold"
            >
              <span className="flex items-center gap-3">
                <Scissors className="w-4 h-4 text-amber-400" />
                Cadastrar Novo Serviço
              </span>
              <Plus className="w-4 h-4 text-stone-500" />
            </Link>

            <Link
              href="/dashboard/profissionais"
              className="flex items-center justify-between p-4 rounded-xl border border-stone-800 bg-stone-900/60 hover:bg-stone-900 hover:border-amber-500/30 transition text-sm text-stone-200 font-semibold"
            >
              <span className="flex items-center gap-3">
                <Users className="w-4 h-4 text-amber-400" />
                Adicionar Manicure / Equipe
              </span>
              <Plus className="w-4 h-4 text-stone-500" />
            </Link>

            <Link
              href="/dashboard/configuracoes"
              className="flex items-center justify-between p-4 rounded-xl border border-stone-800 bg-stone-900/60 hover:bg-stone-900 hover:border-amber-500/30 transition text-sm text-stone-200 font-semibold"
            >
              <span className="flex items-center gap-3">
                <TrendingUp className="w-4 h-4 text-amber-400" />
                Fazer Upgrade de Plano
              </span>
              <ArrowRight className="w-4 h-4 text-stone-500" />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
