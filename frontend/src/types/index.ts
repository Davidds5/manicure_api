export interface Tenant {
  id: number;
  name: string;
  slug: string;
  plan: 'FREE' | 'PRO' | 'ENTERPRISE';
  status: 'ACTIVE' | 'TRIAL' | 'SUSPENDED';
  logoUrl?: string;
  brandColor?: string;
  ownerId?: number;
  createdAt: string;
}

export interface TenantDetails extends Tenant {
  subscriptionStatus: 'ACTIVE' | 'PAST_DUE' | 'CANCELLED' | 'TRIAL';
  maxProfessionals: number;
  maxAppointmentsPerMonth: number;
  nextBillingAt?: string;
}

export interface Professional {
  id: number;
  name: string;
  email: string;
  specialty: string;
  active: boolean;
  tenantId?: number;
}

export interface ServiceItem {
  id: number;
  name: string;
  description: string;
  price: number;
  duration: number; // minutos
  active: boolean;
  tenantId?: number;
}

export interface Client {
  id: number;
  name: string;
  email: string;
  phone: string;
  createdAt?: string;
  tenantId?: number;
}

export type AppointmentStatus = 'SCHEDULED' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';

export interface Appointment {
  id: number;
  clientId: number;
  clientName: string;
  professionalId: number;
  professionalName: string;
  serviceId: number;
  serviceName: string;
  servicePrice: number;
  dateTime: string;
  status: AppointmentStatus;
}

export interface AuthSession {
  token: string;
  tenantId?: number;
  user?: {
    id: number;
    name: string;
    email: string;
    role: string;
  };
}
