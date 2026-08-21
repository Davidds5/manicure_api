'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { 
  Sparkles, 
  LayoutDashboard, 
  Calendar, 
  Users, 
  Scissors, 
  Settings, 
  LogOut, 
  ExternalLink,
  ShieldCheck,
  ChevronRight,
  TrendingUp,
  AlertTriangle
} from 'lucide-react';
import { fetchApi, getAuthToken, removeAuthToken } from '@/lib/api';
import { TenantDetails } from '@/types';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const [tenant, setTenant] = useState<TenantDetails | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = getAuthToken();
    if (!token) {
      router.push('/login');
      return;
    }

    fetchApi<TenantDetails>('/tenants/me')
      .then((data) => {
        setTenant(data);
      })
      .catch((err) => {
        console.error('Erro ao carregar tenant:', err);
        if (err?.status === 403 || err?.status === 401) {
          removeAuthToken();
          router.push('/login?expired=true');
        } else {
          // Fallback gracioso para dados locais
          setTenant({
            id: 1,
            name: 'Studio Bella Nails',
            slug: 'studio-bella',
            plan: 'PRO',
            status: 'ACTIVE',
            brandColor: '#ec4899',
            maxProfessionals: 10,
            maxAppointmentsPerMonth: 1000,
          } as any);
        }
      })
      .finally(() => {
        setLoading(false);
      });
  }, [router]);

  const handleLogout = () => {
    removeAuthToken();
    router.push('/login');
  };

  const navItems = [
    { label: 'Visão Geral', href: '/dashboard', icon: LayoutDashboard },
    { label: 'Agendamentos', href: '/dashboard/agendamentos', icon: Calendar },
    { label: 'Profissionais', href: '/dashboard/profissionais', icon: Users },
    { label: 'Serviços', href: '/dashboard/servicos', icon: Scissors },
    { label: 'Configurações', href: '/dashboard/configuracoes', icon: Settings },
  ];

  return (
    <div className="min-h-screen text-stone-100 flex bg-[#161311]">
      {/* Sidebar Clara e Bem Definida */}
      <aside className="w-64 border-r border-stone-800 bg-[#1c1815] flex flex-col justify-between hidden md:flex shrink-0">
        <div>
          {/* Logo & Tenant Header */}
          <div className="p-6 border-b border-stone-800">
            <Link href="/dashboard" className="flex items-center gap-3">
              <div 
                className="w-11 h-11 rounded-xl flex items-center justify-center shadow-md font-bold text-white uppercase text-lg bg-pink-600 shadow-pink-600/20"
              >
                {tenant?.name ? tenant.name.charAt(0) : '💅'}
              </div>
              <div className="overflow-hidden">
                <span className="block font-bold text-stone-100 truncate text-sm">
                  {tenant?.name || 'Studio Belas Unhas'}
                </span>
                <span className="block text-[11px] font-mono text-pink-400 font-bold truncate">
                  /{tenant?.slug || 'studio-bella'}
                </span>
              </div>
            </Link>

            {/* Plan Badge */}
            {tenant && (
              <div className="mt-4 flex items-center justify-between px-3 py-1.5 rounded-lg bg-stone-900 border border-stone-700 text-xs">
                <span className="text-stone-400 font-medium">Plano</span>
                <span className="font-bold text-pink-400">{tenant.plan}</span>
              </div>
            )}
          </div>

          {/* Navigation Links */}
          <nav className="p-4 space-y-1.5">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = pathname === item.href;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-semibold transition cursor-pointer ${
                    isActive
                      ? 'bg-pink-600 text-white font-bold shadow-md shadow-pink-600/20'
                      : 'text-stone-300 hover:text-white hover:bg-stone-800/80'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  <span>{item.label}</span>
                </Link>
              );
            })}
          </nav>
        </div>

        {/* Bottom Actions */}
        <div className="p-4 border-t border-stone-800 space-y-3">
          {tenant?.slug && (
            <a
              href={`/agendar/${tenant.slug}`}
              target="_blank"
              rel="noreferrer"
              className="flex items-center justify-between w-full px-3.5 py-2.5 rounded-xl border border-pink-500/40 bg-pink-950/20 hover:bg-pink-950/40 text-xs font-bold text-pink-300 transition"
            >
              <span className="flex items-center gap-2">
                <ExternalLink className="w-3.5 h-3.5 text-pink-400" />
                Portal da Cliente
              </span>
              <ChevronRight className="w-3.5 h-3.5 text-pink-400" />
            </a>
          )}

          <button
            onClick={handleLogout}
            className="flex items-center gap-2.5 w-full px-3.5 py-2 rounded-xl text-xs font-semibold text-stone-400 hover:text-rose-400 hover:bg-stone-900 transition cursor-pointer"
          >
            <LogOut className="w-3.5 h-3.5" />
            <span>Sair da Conta</span>
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 bg-[#161311]">
        {/* Top Navbar */}
        <header className="h-16 border-b border-stone-800 bg-[#1c1815] px-6 flex items-center justify-between">
          <div className="flex items-center gap-2 text-xs text-stone-400">
            <span className="font-medium">Espaço do Salão</span>
            <span>/</span>
            <span className="text-white font-bold capitalize">
              {pathname.split('/')[2] || 'Visão Geral'}
            </span>
          </div>

          <div className="flex items-center gap-4">
            <span className="text-xs px-3 py-1 bg-stone-900 text-stone-200 border border-stone-700 rounded-full font-bold flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              Tenant #{tenant?.id || 1}
            </span>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 p-6 md:p-8 overflow-y-auto max-w-7xl w-full">
          {children}
        </main>
      </div>
    </div>
  );
}
