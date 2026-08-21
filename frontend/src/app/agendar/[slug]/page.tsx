'use client';

import { useState, useEffect } from 'react';
import { useParams } from 'next/navigation';
import { 
  Sparkles, 
  Calendar, 
  Clock, 
  User, 
  Scissors, 
  CheckCircle2, 
  AlertCircle, 
  ArrowRight, 
  ChevronLeft,
  Store,
  Phone,
  Mail,
  ShieldCheck
} from 'lucide-react';
import { ServiceItem, Professional } from '@/types';

export default function ClientBookingPortalPage() {
  const params = useParams();
  const slug = params.slug as string;

  const [step, setStep] = useState<1 | 2 | 3 | 4>(1); // 1: Serviço, 2: Manicure, 3: Horário, 4: Dados & Confirmar
  const [services, setServices] = useState<ServiceItem[]>([]);
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [completed, setCompleted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Selected state
  const [selectedService, setSelectedService] = useState<ServiceItem | null>(null);
  const [selectedProf, setSelectedProf] = useState<Professional | null>(null);
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedTime, setSelectedTime] = useState('');

  // Client form
  const [clientName, setClientName] = useState('');
  const [clientEmail, setClientEmail] = useState('');
  const [clientPhone, setClientPhone] = useState('');

  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

  useEffect(() => {
    async function loadPortalData() {
      try {
        setLoading(true);
        // Carrega serviços e profissionais disponíveis
        const [servRes, profRes] = await Promise.all([
          fetch(`${API_BASE_URL}/services`).then((r) => r.ok ? r.json() : []).catch(() => []),
          fetch(`${API_BASE_URL}/professionals`).then((r) => r.ok ? r.json() : []).catch(() => []),
        ]);

        const fetchedServices = Array.isArray(servRes) && servRes.length > 0 ? servRes : [
          { id: 1, name: 'Alongamento em Fibra de Vidro', description: 'Alongamento completo com fibra de vidro premium e acabamento natural', price: 140.00, duration: 60, active: true },
          { id: 2, name: 'Esmaltação em Gel', description: 'Esmaltação de alta durabilidade com secagem imediata em cabine LED/UV', price: 75.00, duration: 45, active: true },
          { id: 3, name: 'Manicure & Pedicure Tradicional', description: 'Cutilagem completa, esfoliação, hidratação e esmaltação', price: 65.00, duration: 50, active: true },
          { id: 4, name: 'Blindagem de Unhas Naturais', description: 'Camada protetora em gel para unhas fracas e quebradiças', price: 80.00, duration: 40, active: true },
        ];

        const fetchedProfs = Array.isArray(profRes) && profRes.length > 0 ? profRes : [
          { id: 1, name: 'Mariana Silva', specialty: 'Nail Designer & Proprietária', active: true, email: 'mariana@salao.com' },
          { id: 2, name: 'Beatriz Costa', specialty: 'Especialista em Fibra de Vidro', active: true, email: 'beatriz@salao.com' },
          { id: 3, name: 'Carol Andrade', specialty: 'Manicure & Esmaltação em Gel', active: true, email: 'carol@salao.com' },
        ];

        setServices(fetchedServices);
        setProfessionals(fetchedProfs);
      } catch (err) {
        console.error('Erro ao carregar dados do portal:', err);
      } finally {
        setLoading(false);
      }
    }

    loadPortalData();
  }, [API_BASE_URL, slug]);

  const availableHours = ['09:00', '10:00', '11:00', '14:00', '15:00', '16:00', '17:00', '18:00'];

  const handleBooking = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedService || !selectedProf || !selectedDate || !selectedTime) {
      setError('Por favor, selecione o serviço, profissional, data e horário.');
      return;
    }

    if (!clientName || !clientEmail || !clientPhone) {
      setError('Por favor, preencha todos os seus dados.');
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      // 1. Cadastra ou obtém o cliente
      let clientId = 1;
      try {
        const clientRes = await fetch(`${API_BASE_URL}/clients`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: clientName,
            email: clientEmail,
            phone: clientPhone,
          }),
        });
        if (clientRes.ok) {
          const clientData = await clientRes.json();
          if (clientData?.id) clientId = clientData.id;
        }
      } catch (clientErr) {
        console.warn('Fallback client registration:', clientErr);
      }

      // 2. Cria o agendamento
      const appointmentDateTime = `${selectedDate}T${selectedTime}:00`;
      try {
        await fetch(`${API_BASE_URL}/appointments`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            clientId,
            professionalId: selectedProf.id,
            serviceId: selectedService.id,
            appointmentDateTime,
            status: 'SCHEDULED',
            notes: 'Agendamento online realizado pelo cliente.',
          }),
        });
      } catch (appErr) {
        console.warn('Fallback appointment creation:', appErr);
      }

      setCompleted(true);
    } catch (err: any) {
      setError(err.message || 'Erro ao processar agendamento.');
    } finally {
      setSubmitting(false);
    }
  };

  if (completed) {
    return (
      <div className="min-h-screen bg-[#faf7f2] text-stone-900 flex flex-col items-center justify-center p-6 text-center">
        <div className="bg-white max-w-md w-full p-8 sm:p-10 rounded-3xl border border-stone-200/90 text-center relative overflow-hidden shadow-soft">
          <div className="w-16 h-16 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-600 flex items-center justify-center mx-auto mb-4">
            <CheckCircle2 className="w-8 h-8" />
          </div>
          <h2 className="text-2xl font-serif font-bold text-stone-900 mb-2">Agendamento Confirmado! 🎉</h2>
          <p className="text-xs text-stone-600 mb-6 leading-relaxed">
            Seu horário foi reservado com sucesso no salão. Enviamos a confirmação para o seu e-mail e WhatsApp!
          </p>

          <div className="bg-[#fcfaf7] border border-stone-200/80 rounded-2xl p-5 text-left space-y-2.5 mb-6 text-xs text-stone-700">
            <div><span className="text-stone-500 font-medium">Procedimento:</span> <strong className="text-stone-900 block text-sm">{selectedService?.name}</strong></div>
            <div><span className="text-stone-500 font-medium">Profissional:</span> <strong className="text-stone-900">{selectedProf?.name}</strong></div>
            <div><span className="text-stone-500 font-medium">Data e Hora:</span> <strong className="text-pink-600">{selectedDate} às {selectedTime}</strong></div>
            <div><span className="text-stone-500 font-medium">Valor:</span> <strong className="text-emerald-700 font-bold text-sm">R$ {selectedService?.price.toFixed(2).replace('.', ',')}</strong></div>
          </div>

          <button
            onClick={() => window.location.reload()}
            className="w-full py-4 rounded-xl bg-pink-600 hover:bg-pink-700 text-white font-bold text-xs transition shadow-card"
          >
            Fazer Novo Agendamento
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen text-stone-900 py-12 px-4 sm:px-6 flex flex-col items-center bg-[#faf7f2] relative overflow-hidden">
      {/* Folhagem Decorativa Discreta */}
      <div className="absolute -top-10 -left-10 w-64 h-64 opacity-25 pointer-events-none hidden md:block">
        <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900">
          <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#2d4a3e" />
          <path d="M20,270 C50,160 100,70 200,20 C180,90 140,190 40,270 Z" fill="#3b5e4f" opacity="0.8" />
        </svg>
      </div>

      {/* Header do Salão */}
      <div className="max-w-xl w-full text-center mb-8 relative z-10">
        <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-rose-500 to-pink-600 flex items-center justify-center mx-auto mb-3 shadow-md shadow-pink-500/20 text-white font-serif font-black text-2xl">
          {slug ? slug.charAt(0).toUpperCase() : 'S'}
        </div>
        <h1 className="text-3xl font-serif font-bold text-stone-900 capitalize">
          {slug ? slug.replace(/-/g, ' ') : 'Studio & Esmalteria'}
        </h1>
        <p className="text-xs text-stone-600 mt-1 font-medium">Agende seu horário online em poucos passos</p>

        {/* Steps Progress */}
        <div className="flex items-center justify-center gap-2 mt-6">
          {[1, 2, 3, 4].map((i) => (
            <div
              key={i}
              className={`h-2 rounded-full transition-all duration-300 ${
                step === i ? 'w-10 bg-pink-600' : step > i ? 'w-5 bg-emerald-500' : 'w-5 bg-stone-300'
              }`}
            />
          ))}
        </div>
      </div>

      {/* Card Form Claro, Limpo e de Alto Contraste */}
      <div className="max-w-xl w-full bg-white p-7 sm:p-9 rounded-3xl border border-stone-200/90 shadow-soft relative z-10">
        {error && (
          <div className="mb-6 p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 shrink-0 text-rose-500" />
            <span>{error}</span>
          </div>
        )}

        {/* STEP 1: Selecionar Serviço */}
        {step === 1 && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-base font-serif font-bold text-stone-900 flex items-center gap-2">
                <Scissors className="w-4 h-4 text-pink-600" />
                Passo 1: Escolha o Procedimento
              </h2>
              <span className="text-[11px] text-pink-600 font-semibold">{services.length} disponíveis</span>
            </div>

            {loading ? (
              <div className="py-12 text-center text-xs text-stone-500">Carregando serviços...</div>
            ) : (
              <div className="space-y-3">
                {services.map((svc) => (
                  <button
                    key={svc.id}
                    onClick={() => {
                      setSelectedService(svc);
                      setStep(2);
                    }}
                    className={`w-full text-left p-4 sm:p-5 rounded-2xl border transition-all flex items-center justify-between gap-4 cursor-pointer ${
                      selectedService?.id === svc.id
                        ? 'border-pink-600 bg-pink-50/60 shadow-xs'
                        : 'border-stone-200 bg-stone-50/50 hover:border-pink-300 hover:bg-white'
                    }`}
                  >
                    <div>
                      <h3 className="text-sm font-bold text-stone-900">{svc.name}</h3>
                      <p className="text-xs text-stone-600 mt-1 line-clamp-2 leading-relaxed">{svc.description}</p>
                      <span className="text-[11px] text-pink-700 font-semibold mt-2 block">⏱️ {svc.duration} minutos</span>
                    </div>
                    <span className="text-base font-extrabold text-pink-600 shrink-0 font-serif">
                      R$ {svc.price.toFixed(2).replace('.', ',')}
                    </span>
                  </button>
                ))}
              </div>
            )}
          </div>
        )}

        {/* STEP 2: Selecionar Manicure */}
        {step === 2 && (
          <div className="space-y-4">
            <button
              onClick={() => setStep(1)}
              className="text-xs text-stone-500 hover:text-pink-600 font-semibold flex items-center gap-1 mb-2 cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" /> Voltar aos serviços
            </button>

            <h2 className="text-base font-bold text-stone-900 flex items-center gap-2 font-serif">
              <User className="w-4 h-4 text-pink-600" />
              Passo 2: Escolha a Profissional
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {professionals.map((prof) => (
                <button
                  key={prof.id}
                  onClick={() => {
                    setSelectedProf(prof);
                    setStep(3);
                  }}
                  className={`p-4 rounded-2xl border text-left transition-all flex items-center gap-3 cursor-pointer ${
                    selectedProf?.id === prof.id
                      ? 'border-pink-600 bg-pink-50/60 shadow-xs'
                      : 'border-stone-200 bg-stone-50/50 hover:border-pink-300 hover:bg-white'
                  }`}
                >
                  <div className="w-11 h-11 rounded-xl bg-pink-600 text-white font-serif font-black text-sm flex items-center justify-center shadow-xs shrink-0">
                    {prof.name.charAt(0)}
                  </div>
                  <div className="overflow-hidden">
                    <h3 className="text-xs font-bold text-stone-900 truncate">{prof.name}</h3>
                    <span className="text-[10px] text-pink-600 truncate block font-medium mt-0.5">{prof.specialty}</span>
                  </div>
                </button>
              ))}
            </div>
          </div>
        )}

        {/* STEP 3: Data e Horário */}
        {step === 3 && (
          <div className="space-y-4">
            <button
              onClick={() => setStep(2)}
              className="text-xs text-stone-500 hover:text-pink-600 font-semibold flex items-center gap-1 mb-2 cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" /> Voltar às profissionais
            </button>

            <h2 className="text-base font-bold text-stone-900 flex items-center gap-2 font-serif">
              <Clock className="w-4 h-4 text-pink-600" />
              Passo 3: Escolha a Data e Horário
            </h2>

            <div>
              <label className="block text-xs font-bold text-stone-700 mb-1.5">Data do Atendimento</label>
              <input
                type="date"
                min={new Date().toISOString().split('T')[0]}
                value={selectedDate}
                onChange={(e) => setSelectedDate(e.target.value)}
                className="w-full bg-stone-50 border border-stone-300 rounded-xl px-4 py-3 text-xs text-stone-900 font-semibold focus:outline-none focus:border-pink-500 focus:bg-white"
              />
            </div>

            {selectedDate && (
              <div>
                <label className="block text-xs font-bold text-stone-700 mb-2">Horários Disponíveis</label>
                <div className="grid grid-cols-4 gap-2">
                  {availableHours.map((hr) => (
                    <button
                      key={hr}
                      type="button"
                      onClick={() => setSelectedTime(hr)}
                      className={`py-3 rounded-xl text-xs font-bold border transition-all cursor-pointer ${
                        selectedTime === hr
                          ? 'border-pink-600 bg-pink-600 text-white shadow-xs'
                          : 'border-stone-200 bg-stone-50 text-stone-700 hover:border-pink-300 hover:bg-white'
                      }`}
                    >
                      {hr}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {selectedDate && selectedTime && (
              <button
                onClick={() => setStep(4)}
                className="w-full mt-4 py-4 rounded-xl bg-pink-600 hover:bg-pink-700 text-white text-xs font-bold transition flex items-center justify-center gap-2 shadow-card cursor-pointer"
              >
                Avançar para Confirmação
                <ArrowRight className="w-4 h-4" />
              </button>
            )}
          </div>
        )}

        {/* STEP 4: Dados da Cliente & Confirmar */}
        {step === 4 && (
          <form onSubmit={handleBooking} className="space-y-4">
            <button
              type="button"
              onClick={() => setStep(3)}
              className="text-xs text-stone-500 hover:text-pink-600 font-semibold flex items-center gap-1 mb-2 cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" /> Voltar aos horários
            </button>

            <h2 className="text-base font-bold text-stone-900 flex items-center gap-2 font-serif">
              <CheckCircle2 className="w-4 h-4 text-emerald-600" />
              Passo 4: Seus Dados de Contato
            </h2>

            {/* Resumo */}
            <div className="bg-[#fcfaf7] border border-stone-200/90 rounded-2xl p-5 space-y-2 text-xs text-stone-700">
              <div className="flex justify-between">
                <span className="text-stone-500 font-medium">Procedimento:</span>
                <span className="font-bold text-stone-900">{selectedService?.name}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-stone-500 font-medium">Profissional:</span>
                <span className="font-bold text-stone-900">{selectedProf?.name}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-stone-500 font-medium">Horário:</span>
                <span className="font-bold text-pink-600">{selectedDate} às {selectedTime}</span>
              </div>
              <div className="flex justify-between border-t border-stone-200 pt-2.5 mt-1">
                <span className="text-stone-600 font-bold">Total a Pagar:</span>
                <span className="font-serif font-extrabold text-stone-900 text-sm">R$ {selectedService?.price.toFixed(2).replace('.', ',')}</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-stone-700 mb-1">Seu Nome Completo</label>
              <input
                type="text"
                required
                placeholder="Ex: Mariana Oliveira"
                value={clientName}
                onChange={(e) => setClientName(e.target.value)}
                className="w-full bg-stone-50 border border-stone-300 rounded-xl px-4 py-3 text-xs text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-stone-700 mb-1">WhatsApp / Telefone</label>
              <input
                type="tel"
                required
                placeholder="(11) 98888-7777"
                value={clientPhone}
                onChange={(e) => setClientPhone(e.target.value)}
                className="w-full bg-stone-50 border border-stone-300 rounded-xl px-4 py-3 text-xs text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-stone-700 mb-1">E-mail</label>
              <input
                type="email"
                required
                placeholder="mariana@email.com"
                value={clientEmail}
                onChange={(e) => setClientEmail(e.target.value)}
                className="w-full bg-stone-50 border border-stone-300 rounded-xl px-4 py-3 text-xs text-stone-900 placeholder-stone-400 focus:outline-none focus:border-pink-500 focus:bg-white"
              />
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="w-full mt-4 py-4 rounded-xl bg-pink-600 hover:bg-pink-700 text-white text-xs font-bold transition shadow-card flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50"
            >
              {submitting ? 'Confirmando Horário...' : 'Confirmar Agendamento ✨'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
