'use strict';
import Link from 'next/link';
import { 
  ArrowRight, 
  Check, 
  Clock, 
  Star, 
  Calendar, 
  Shield, 
  MessageCircle,
  TrendingUp,
  Sparkles,
  Scissors
} from 'lucide-react';

export default function LandingPage() {
  return (
    <div className="min-h-screen text-stone-900 selection:bg-pink-500 selection:text-white bg-[#faf7f2]">
      {/* Header com tonalidade orgânica acolhedora */}
      <header className="sticky top-0 z-50 bg-[#faf7f2]/90 backdrop-blur-md border-b border-stone-200/80">
        <div className="max-w-6xl mx-auto px-6 h-20 flex items-center justify-between">
          <Link href="/" className="flex items-center gap-3">
            {/* SVG Custom Logo de Esmalte com fundo Pink */}
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-rose-500 to-pink-500 flex items-center justify-center text-white shadow-md shadow-pink-500/25">
              <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="m14 12-8.5 8.5a2.12 2.12 0 1 1-3-3L11 9" />
                <path d="M15 13 9 7l4-4 6 6-4 4Z" />
                <path d="m17 7 3-3" />
              </svg>
            </div>
            <div className="flex flex-col">
              <span className="text-xl font-extrabold tracking-tight text-stone-900 font-serif leading-tight">Belas<span className="text-pink-600">Unhas</span></span>
              <span className="text-[10px] font-bold text-stone-500 tracking-wider uppercase">Agenda & Gestão de Manicures</span>
            </div>
          </Link>

          <nav className="hidden md:flex items-center gap-8 text-sm font-bold text-stone-700">
            <a href="#como-funciona" className="hover:text-pink-600 transition">Como funciona</a>
            <a href="#depoimentos" className="hover:text-pink-600 transition">Depoimentos</a>
            <a href="#planos" className="hover:text-pink-600 transition">Planos</a>
          </nav>

          <div className="flex items-center gap-4">
            <Link
              href="/login"
              className="text-sm font-bold text-stone-700 hover:text-pink-600 transition px-3 py-2"
            >
              Entrar
            </Link>
            <Link
              href="/signup"
              className="text-sm font-bold bg-pink-600 hover:bg-pink-700 text-white px-5 py-2.5 rounded-xl transition shadow-md shadow-pink-500/25 flex items-center gap-2"
            >
              Criar meu Espaço
              <ArrowRight className="w-4 h-4 text-white" />
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section com Estética de Salão Real & Ricas Folhagens Tropicais */}
      <section className="relative pt-12 pb-28 px-6 overflow-hidden">
        {/* Foto de Fundo do Salão Real de Referência com Iluminação Natural e Plantas */}
        <div className="absolute inset-0 pointer-events-none -z-10 overflow-hidden">
          <img 
            src="/images/salon-reference.png" 
            alt="Salão contemporâneo com grandes folhagens tropicais e luz natural" 
            className="w-full h-full object-cover object-left sm:object-center opacity-60"
          />
          {/* Overlay suave garantindo contraste perfeito para o texto e mantendo as plantas bem visíveis */}
          <div className="absolute inset-0 bg-gradient-to-r from-[#faf7f2]/95 via-[#faf7f2]/80 to-[#faf7f2]/60" />
          <div className="absolute inset-0 bg-gradient-to-b from-[#faf7f2]/30 via-transparent to-[#faf7f2]" />
        </div>

        {/* 🌿 Folhagem Tropical Grande - Canto Inferior Esquerdo (Estrelícia / Banana da Terra) */}
        <div className="absolute -bottom-10 -left-12 w-96 h-96 opacity-65 pointer-events-none -z-0">
          <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900 drop-shadow-md">
            {/* Folha 1 Principal */}
            <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#2d4a3e" />
            <path d="M40,290 C90,190 150,110 240,50" stroke="#4e7967" strokeWidth="3" strokeLinecap="round" />
            {/* Folha 2 Lateral */}
            <path d="M20,270 C50,160 100,70 200,20 C180,90 140,190 40,270 Z" fill="#3b5e4f" opacity="0.85" />
            {/* Folha 3 Inferior */}
            <path d="M60,300 C110,230 180,160 280,120 C245,180 190,250 80,300 Z" fill="#243d32" opacity="0.9" />
          </svg>
        </div>

        {/* 🌿 Folhagem Tropical - Canto Superior Esquerdo (Saindo do Topo) */}
        <div className="absolute -top-14 -left-10 w-72 h-72 opacity-50 pointer-events-none hidden md:block -z-0 transform rotate-180">
          <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900">
            <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#2d4a3e" />
            <path d="M20,270 C50,160 100,70 200,20 C180,90 140,190 40,270 Z" fill="#3b5e4f" opacity="0.8" />
          </svg>
        </div>

        {/* 🌿 Folhagem Tropical - Canto Superior Direito (Atrás do Mockup) */}
        <div className="absolute -top-12 right-0 w-80 h-80 opacity-45 pointer-events-none hidden lg:block -z-0">
          <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900 transform scale-x-[-1]">
            <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#355648" />
            <path d="M40,290 C90,190 150,110 240,50" stroke="#5a8673" strokeWidth="3" strokeLinecap="round" />
            <path d="M10,260 C40,150 90,60 190,10 C170,80 130,180 30,260 Z" fill="#243d32" opacity="0.75" />
          </svg>
        </div>

        {/* 🌿 Folhagem Tropical - Canto Inferior Direito (Abaixo do Card PIX) */}
        <div className="absolute -bottom-16 right-10 w-72 h-72 opacity-40 pointer-events-none hidden sm:block -z-0">
          <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900 transform rotate-45 scale-x-[-1]">
            <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#2d4a3e" />
            <path d="M20,270 C50,160 100,70 200,20 C180,90 140,190 40,270 Z" fill="#3b5e4f" opacity="0.8" />
          </svg>
        </div>

        <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-10 items-center relative z-10">
          
          {/* Coluna Esquerda: Texto Editorial com legibilidade cristalina */}
          <div className="lg:col-span-7 text-left space-y-7">
            
            {/* Título com composição tipográfica e linha de marca-texto orgânico */}
            <div className="relative">
              <h1 className="text-4xl sm:text-5xl lg:text-[54px] font-serif font-bold text-stone-900 leading-[1.14] tracking-tight">
                Sua agenda cheia, sem você precisar{' '}
                <span className="relative inline-block px-1">
                  <span className="relative z-10 italic text-stone-950 font-bold">parar o atendimento</span>
                  {/* Linha de marca-texto orgânico */}
                  <svg className="absolute -bottom-1.5 left-0 w-full h-3 text-pink-300 -z-0 opacity-85" viewBox="0 0 100 20" preserveAspectRatio="none">
                    <path d="M0,15 Q25,5 50,14 T100,8 L98,18 Q50,22 0,17 Z" fill="currentColor" />
                  </svg>
                </span>{' '}
                para responder.
              </h1>
            </div>

            <p className="text-base sm:text-lg text-stone-700 max-w-xl leading-relaxed font-normal">
              Coloque o link do seu salão no WhatsApp e na bio do Instagram. Sua cliente escolhe o procedimento, a profissional e o horário disponível em menos de 1 minuto.
            </p>

            {/* Ações principais: Botão sólido rosa preservado */}
            <div className="pt-2 flex flex-col sm:flex-row items-start sm:items-center gap-4">
              <Link
                href="/signup"
                className="w-full sm:w-auto text-center font-bold bg-pink-600 hover:bg-pink-700 text-white px-8 py-4 rounded-xl shadow-card hover:shadow-lg transition-all duration-200 text-base"
              >
                Testar 14 dias grátis
              </Link>
              <div className="text-xs text-stone-600 font-medium flex items-center gap-2 px-1">
                <Shield className="w-4 h-4 text-pink-600 shrink-0" />
                Sem fidelidade • Cancele a qualquer momento
              </div>
            </div>

            {/* Prova Social Orgânica & Verificável */}
            <div className="pt-6 border-t border-stone-200/90 grid grid-cols-1 sm:grid-cols-2 gap-4 items-center">
              <div className="flex items-center gap-3">
                <div className="flex -space-x-2.5 overflow-hidden">
                  <img className="inline-block h-11 w-11 rounded-full ring-2 ring-[#faf7f2] object-cover" src="https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=120&auto=format&fit=crop&q=80" alt="Renata Nails" />
                  <img className="inline-block h-11 w-11 rounded-full ring-2 ring-[#faf7f2] object-cover" src="https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=120&auto=format&fit=crop&q=80" alt="Camila Studio" />
                  <img className="inline-block h-11 w-11 rounded-full ring-2 ring-[#faf7f2] object-cover" src="https://images.unsplash.com/photo-1580489944761-15a19d654956?w=120&auto=format&fit=crop&q=80" alt="Juliana Manicure" />
                </div>
                <div className="text-xs">
                  <span className="font-bold text-stone-900 block">+1.840 manicures</span>
                  <span className="text-stone-600 font-normal">ativas em todo o Brasil</span>
                </div>
              </div>

              {/* Indicador de agendamentos reais */}
              <div className="bg-white/90 backdrop-blur-sm border border-stone-200 px-3.5 py-2 rounded-xl flex items-center gap-2.5 shadow-xs">
                <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse shrink-0" />
                <div className="text-xs">
                  <span className="font-extrabold text-stone-900">4.820 horários</span>{' '}
                  <span className="text-stone-600">agendados esta semana</span>
                </div>
              </div>
            </div>
          </div>

          {/* Coluna Direita: Mockup de Celular na Atmosfera de Salão */}
          <div className="lg:col-span-5 relative flex justify-center">
            {/* Foto Polaroid Orgânica rotacionada ao fundo */}
            <div className="absolute -top-7 -left-7 w-44 bg-white p-2.5 shadow-photo rounded-sm transform -rotate-7 z-0 hidden sm:block border border-stone-200">
              <img 
                src="https://images.unsplash.com/photo-1632345031435-8727f6897d53?w=350&auto=format&fit=crop&q=80" 
                alt="Unhas feitas" 
                className="w-full h-30 object-cover rounded-xs mb-1.5"
              />
              <span className="text-[11px] font-serif text-stone-800 font-bold block text-center">Alongamento Fibra ✨</span>
            </div>

            {/* Mockup de Celular com Acabamento BelasUnhas */}
            <div className="w-[305px] h-[580px] bg-[#1a1518] phone-mockup relative z-10 overflow-hidden flex flex-col justify-between border-2 border-stone-900">
              {/* Dynamic Island / Top Notch */}
              <div className="h-7 bg-[#1a1518] flex justify-center items-center">
                <div className="w-24 h-3.5 bg-stone-900 rounded-full" />
              </div>

              {/* Tela Interna do Aplicativo */}
              <div className="p-3.5 flex-1 bg-[#fff8fa] overflow-y-auto text-left text-xs space-y-3">
                {/* Header Custom do Salão */}
                <div className="bg-white p-3 rounded-2xl border border-pink-200/80 shadow-xs flex items-center justify-between">
                  <div className="flex items-center gap-2.5">
                    <div className="w-9 h-9 rounded-xl bg-pink-600 text-white font-serif font-black flex items-center justify-center text-sm shadow-xs">
                      B
                    </div>
                    <div>
                      <span className="font-bold text-stone-900 block leading-tight">Studio Bella Nails</span>
                      <span className="text-[10px] text-pink-600 font-medium">Autoatendimento 24h</span>
                    </div>
                  </div>
                  <span className="text-[9px] font-bold bg-pink-100 text-pink-700 px-2 py-0.5 rounded-full">Aberto</span>
                </div>

                {/* Notificação Orgânica estilo Balão de Conversa WhatsApp */}
                <div className="bg-white border-l-4 border-emerald-500 p-2.5 rounded-xl text-[11px] text-stone-700 shadow-xs space-y-0.5">
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-emerald-700 text-[10px] flex items-center gap-1">
                      <MessageCircle className="w-3 h-3" /> WhatsApp Automático
                    </span>
                    <span className="text-[9px] text-stone-400">14:02</span>
                  </div>
                  <p className="text-[10px] text-stone-600 leading-snug">
                    "Oi Mariana! Agendamento de Manicure confirmado para amanhã às 14:00."
                  </p>
                </div>

                {/* Lista de Procedimentos com Hierarquia Editorial */}
                <div className="space-y-2">
                  <div className="flex items-center justify-between text-[11px] font-bold text-stone-800">
                    <span>Escolha o serviço:</span>
                    <span className="text-[10px] text-pink-600 font-normal">1 de 3 selecionado</span>
                  </div>
                  
                  <div className="bg-white p-3 rounded-2xl border-2 border-pink-500 shadow-xs flex justify-between items-center">
                    <div>
                      <span className="font-bold text-stone-900 block text-[11px]">Alongamento em Fibra de Vidro</span>
                      <span className="text-[10px] text-stone-500">60 min • Profissional Beatriz</span>
                    </div>
                    <span className="font-bold text-pink-600 text-xs">R$ 140</span>
                  </div>

                  <div className="bg-white p-2.5 rounded-2xl border border-pink-100 flex justify-between items-center opacity-85">
                    <div>
                      <span className="font-semibold text-stone-900 block text-[11px]">Esmaltação em Gel</span>
                      <span className="text-[10px] text-stone-500">45 min • Profissional Carol</span>
                    </div>
                    <span className="font-medium text-stone-700 text-xs">R$ 75</span>
                  </div>
                </div>

                {/* Seletor de Horários */}
                <div>
                  <span className="text-[11px] font-bold text-stone-800 block mb-1.5">Horários livres:</span>
                  <div className="grid grid-cols-3 gap-1.5">
                    <span className="p-2 text-center bg-pink-600 text-white rounded-xl font-bold text-[10px] shadow-xs">14:00</span>
                    <span className="p-2 text-center bg-white border border-pink-200 text-stone-700 rounded-xl text-[10px]">15:30</span>
                    <span className="p-2 text-center bg-white border border-pink-200 text-stone-700 rounded-xl text-[10px]">17:00</span>
                  </div>
                </div>
              </div>

              {/* Botão de rodapé do celular */}
              <div className="p-3 bg-white border-t border-pink-100">
                <div className="w-full py-2.5 bg-pink-600 text-white text-center font-bold rounded-xl text-xs shadow-xs">
                  Confirmar Agendamento
                </div>
              </div>
            </div>

            {/* Cartão de PIX com ancoragem orgânica */}
            <div className="absolute -bottom-5 -right-5 bg-white/95 backdrop-blur-md p-3.5 rounded-2xl shadow-photo border border-stone-200/90 z-20 transform rotate-2 text-left space-y-0.5">
              <div className="flex items-center justify-between gap-3">
                <span className="text-[10px] text-emerald-700 font-bold uppercase tracking-wider">PIX Confirmado</span>
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
              </div>
              <span className="text-base font-extrabold text-stone-900 block">+ R$ 140,00</span>
              <span className="text-[9px] text-stone-500 font-medium block">Mariana O. • Sinal 50%</span>
            </div>
          </div>
        </div>
      </section>

      {/* Seção Como Funciona com Moldura de Folhagens Tropicais Aconchegantes */}
      <section id="como-funciona" className="py-24 px-6 bg-[#f4eee6]/80 border-y border-stone-200/90 relative overflow-hidden">
        {/* Folhagem Decorativa Esquerda */}
        <div className="absolute -top-10 -left-10 w-64 h-64 opacity-35 pointer-events-none hidden md:block">
          <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900">
            <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#2d4a3e" />
            <path d="M20,270 C50,160 100,70 200,20 C180,90 140,190 40,270 Z" fill="#3b5e4f" opacity="0.8" />
          </svg>
        </div>

        {/* Folhagem Decorativa Direita */}
        <div className="absolute -bottom-12 -right-10 w-72 h-72 opacity-35 pointer-events-none hidden md:block">
          <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900 transform scale-x-[-1]">
            <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#355648" />
            <path d="M10,260 C40,150 90,60 190,10 C170,80 130,180 30,260 Z" fill="#243d32" opacity="0.8" />
          </svg>
        </div>

        <div className="max-w-6xl mx-auto relative z-10">
          <div className="text-left max-w-2xl mb-16 space-y-2">
            <span className="text-xs font-extrabold uppercase tracking-wider text-pink-600 block">Simplicidade no dia a dia</span>
            <h2 className="text-3xl sm:text-4xl font-serif font-bold text-stone-900 leading-tight">
              Como funciona no dia a dia do seu salão
            </h2>
            <p className="text-stone-600 text-sm font-medium">
              Sem sistemas complicados. Três passos para descomplicar sua rotina:
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="p-8 rounded-3xl bg-white/95 border border-stone-200/80 space-y-4 text-left shadow-soft hover:border-pink-300 transition-all">
              <div className="w-9 h-9 rounded-xl bg-pink-600 text-white flex items-center justify-center font-serif font-bold text-sm shadow-xs">
                1
              </div>
              <h3 className="text-lg font-bold text-stone-900">Cadastre seus serviços e equipe</h3>
              <p className="text-stone-600 text-xs leading-relaxed font-medium">
                Coloque os procedimentos que você faz (manicure tradicional, blindagem, fibra), quanto cobra e o tempo de cada atendimento.
              </p>
            </div>

            <div className="p-8 rounded-3xl bg-white/95 border border-stone-200/80 space-y-4 text-left shadow-soft hover:border-pink-300 transition-all">
              <div className="w-9 h-9 rounded-xl bg-pink-600 text-white flex items-center justify-center font-serif font-bold text-sm shadow-xs">
                2
              </div>
              <h3 className="text-lg font-bold text-stone-900">Divulgue seu link exclusivo</h3>
              <p className="text-stone-600 text-xs leading-relaxed font-medium">
                Coloque o link na bio do Instagram ou configure a resposta automática no WhatsApp quando a cliente perguntar: "tem horário hoje?".
              </p>
            </div>

            <div className="p-8 rounded-3xl bg-white/95 border border-stone-200/80 space-y-4 text-left shadow-soft hover:border-pink-300 transition-all">
              <div className="w-9 h-9 rounded-xl bg-pink-600 text-white flex items-center justify-center font-serif font-bold text-sm shadow-xs">
                3
              </div>
              <h3 className="text-lg font-bold text-stone-900">Atenda sem interrupções</h3>
              <p className="text-stone-600 text-xs leading-relaxed font-medium">
                O horário cai direto na sua agenda, com nome, telefone e serviço escolhido pela cliente. Você só precisa atender!
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Depoimentos com Fotos Reais e Atmosfera Aconchegante */}
      <section id="depoimentos" className="py-24 px-6 max-w-6xl mx-auto relative overflow-hidden">
        {/* Folhagem Suave no Fundo de Depoimentos */}
        <div className="absolute top-1/2 -right-16 w-80 h-80 opacity-25 pointer-events-none hidden lg:block -translate-y-1/2">
          <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900">
            <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#2d4a3e" />
            <path d="M20,270 C50,160 100,70 200,20 C180,90 140,190 40,270 Z" fill="#3b5e4f" opacity="0.8" />
          </svg>
        </div>

        <div className="text-left max-w-2xl mb-14 space-y-2 relative z-10">
          <span className="text-xs font-extrabold uppercase tracking-wider text-pink-600 block">Experiência Real</span>
          <h2 className="text-3xl sm:text-4xl font-serif font-bold text-stone-900 mb-2 leading-tight">
            Quem já usa no dia a dia
          </h2>
          <p className="text-stone-600 text-sm font-medium">Depoimentos reais de profissionais que organizaram suas agendas:</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 relative z-10">
          <div className="bg-white p-8 rounded-3xl border border-stone-200/90 shadow-soft text-left space-y-5">
            <p className="text-stone-700 text-xs leading-relaxed italic font-normal">
              "Antes eu perdia clientes porque demorava 2 horas para responder no WhatsApp enquanto fazia uma fibra. Agora elas mesmas entram no link e escolhem o horário."
            </p>
            <div className="flex items-center gap-3 pt-3 border-t border-stone-100">
              <img src="https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&auto=format&fit=crop&q=80" alt="Renata" className="w-11 h-11 rounded-full object-cover ring-2 ring-pink-200" />
              <div>
                <span className="font-bold text-stone-900 text-xs block">Renata Silveira</span>
                <span className="text-[10px] text-pink-600 font-semibold">Espaço Renata Nails • SP</span>
              </div>
            </div>
          </div>

          <div className="bg-white p-8 rounded-3xl border border-stone-200/90 shadow-soft text-left space-y-5">
            <p className="text-stone-700 text-xs leading-relaxed italic font-normal">
              "Aqui no salão somos em 4 manicures. Cada uma tem sua comissão certinha e não dá mais confusão de duas clientes marcarem no mesmo horário."
            </p>
            <div className="flex items-center gap-3 pt-3 border-t border-stone-100">
              <img src="https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=100&auto=format&fit=crop&q=80" alt="Camila" className="w-11 h-11 rounded-full object-cover ring-2 ring-pink-200" />
              <div>
                <span className="font-bold text-stone-900 text-xs block">Camila Medeiros</span>
                <span className="text-[10px] text-pink-600 font-semibold">Studio Bella Esmalteria • RJ</span>
              </div>
            </div>
          </div>

          <div className="bg-white p-8 rounded-3xl border border-stone-200/90 shadow-soft text-left space-y-5">
            <p className="text-stone-700 text-xs leading-relaxed italic font-normal">
              "Muito fácil de mexer. Consegui cadastrar meus preços e mandar pras clientes no primeiro dia. Minha agenda de sexta e sábado lota sozinha."
            </p>
            <div className="flex items-center gap-3 pt-3 border-t border-stone-100">
              <img src="https://images.unsplash.com/photo-1580489944761-15a19d654956?w=100&auto=format&fit=crop&q=80" alt="Juliana" className="w-11 h-11 rounded-full object-cover ring-2 ring-pink-200" />
              <div>
                <span className="font-bold text-stone-900 text-xs block">Juliana Ferreira</span>
                <span className="text-[10px] text-pink-600 font-semibold">Manicure Autônoma • MG</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Planos e Preços com Moldura Botânica & Madeira Suave */}
      <section id="planos" className="py-24 px-6 bg-[#f4eee6]/80 border-t border-stone-200/90 relative overflow-hidden">
        {/* Folhagem Tropical no Rodapé de Planos */}
        <div className="absolute -bottom-16 -left-12 w-80 h-80 opacity-30 pointer-events-none hidden md:block">
          <svg viewBox="0 0 300 300" fill="none" xmlns="http://www.w3.org/2000/svg" className="w-full h-full text-emerald-900">
            <path d="M40,290 C70,180 140,80 260,30 C230,110 180,210 60,290 Z" fill="#2d4a3e" />
            <path d="M20,270 C50,160 100,70 200,20 C180,90 140,190 40,270 Z" fill="#3b5e4f" opacity="0.8" />
          </svg>
        </div>

        <div className="max-w-6xl mx-auto relative z-10">
          <div className="text-left max-w-xl mb-14 space-y-2">
            <span className="text-xs font-extrabold uppercase tracking-wider text-pink-600 block">Investimento Transparente</span>
            <h2 className="text-3xl sm:text-4xl font-serif font-bold text-stone-900 leading-tight">Valores simples e sem letras miúdas</h2>
            <p className="text-stone-600 text-sm font-medium">Escolha o plano de acordo com o tamanho do seu salão:</p>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-stretch pt-4">
            {/* FREE */}
            <div className="p-8 rounded-3xl border border-stone-200/90 bg-white flex flex-col justify-between text-left shadow-soft hover:border-pink-300 transition">
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-pink-600 block mb-1">Para quem está começando</span>
                <h3 className="text-xl font-bold text-stone-900 mb-3">Plano Gratuito</h3>
                <div className="mb-6 flex items-baseline gap-1">
                  <span className="text-4xl font-serif font-extrabold text-stone-900">R$ 0</span>
                  <span className="text-stone-500 text-xs font-medium"> /mês</span>
                </div>
                <ul className="space-y-3 text-xs text-stone-700 mb-8 font-medium">
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Até 2 Profissionais</li>
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Até 50 Agendamentos/mês</li>
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Link exclusivo com seu nome</li>
                </ul>
              </div>
              <Link
                href="/signup?plan=FREE"
                className="w-full py-3.5 text-center text-xs font-bold rounded-xl border border-stone-300 bg-stone-50 hover:bg-stone-100 text-stone-800 transition shadow-xs"
              >
                Começar Grátis
              </Link>
            </div>

            {/* PRO - Destaque */}
            <div className="p-8 sm:p-9 rounded-3xl border-2 border-pink-500 bg-white flex flex-col justify-between text-left relative shadow-card lg:-translate-y-2">
              <div className="absolute -top-3.5 left-8 px-4 py-1 bg-pink-600 text-white text-[11px] font-bold rounded-full uppercase tracking-wider shadow-sm">
                Mais Escolhido ✨
              </div>
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-pink-600 block mb-1">Para Salões em Crescimento</span>
                <h3 className="text-2xl font-bold text-stone-900 mb-3">Plano Profissional</h3>
                <div className="mb-6 flex items-baseline gap-1">
                  <span className="text-4xl font-serif font-extrabold text-stone-900">R$ 99</span>
                  <span className="text-stone-500 text-xs font-medium"> /mês</span>
                </div>
                <ul className="space-y-3 text-xs text-stone-800 mb-8 font-semibold">
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Até 10 Profissionais</li>
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Até 500 Agendamentos/mês</li>
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Sua logo e cores personalizadas</li>
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Relatório financeiro e comissões</li>
                </ul>
              </div>
              <Link
                href="/signup?plan=PRO"
                className="w-full py-4 text-center text-xs font-bold rounded-xl bg-pink-600 hover:bg-pink-700 text-white transition shadow-card text-sm"
              >
                Testar 14 Dias Grátis
              </Link>
            </div>

            {/* ENTERPRISE */}
            <div className="p-8 rounded-3xl border border-stone-200/90 bg-white flex flex-col justify-between text-left shadow-soft hover:border-pink-300 transition">
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-pink-600 block mb-1">Para grandes equipes</span>
                <h3 className="text-xl font-bold text-stone-900 mb-3">Plano Salão Completo</h3>
                <div className="mb-6 flex items-baseline gap-1">
                  <span className="text-4xl font-serif font-extrabold text-stone-900">R$ 249</span>
                  <span className="text-stone-500 text-xs font-medium"> /mês</span>
                </div>
                <ul className="space-y-3 text-xs text-stone-700 mb-8 font-medium">
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Profissionais Ilimitados</li>
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Agendamentos Ilimitados</li>
                  <li className="flex items-center gap-2.5"><Check className="w-4 h-4 text-pink-600 shrink-0" /> Suporte VIP no WhatsApp</li>
                </ul>
              </div>
              <Link
                href="/signup?plan=ENTERPRISE"
                className="w-full py-3.5 text-center text-xs font-bold rounded-xl border border-stone-300 bg-stone-50 hover:bg-stone-100 text-stone-800 transition shadow-xs"
              >
                Falar no WhatsApp
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Footer Limpo e Acolhedor */}
      <footer className="py-12 px-6 text-center text-xs text-stone-500 border-t border-stone-200/80 bg-[#faf7f2]">
        <div className="max-w-6xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <span className="font-serif font-bold text-stone-900 text-sm">BelasUnhas</span>
            <span>• Feito para salões de manicure, pedicure & nail designers</span>
          </div>
          <p>© 2026 BelasUnhas — Desenvolvido por David Santos</p>
        </div>
      </footer>
    </div>
  );
}

