package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.config.TenantContext;
import br.com.davidds5.manicure_api.entity.SubscriptionEntity;
import br.com.davidds5.manicure_api.enums.SubscriptionStatus;
import br.com.davidds5.manicure_api.enums.TenantPlan;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.PlanLimitExceededException;
import br.com.davidds5.manicure_api.repository.AppointmentRepository;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import br.com.davidds5.manicure_api.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ProfessionalRepository professionalRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public SubscriptionEntity createDefaultSubscription(Long tenantId, TenantPlan plan) {
        var existing = subscriptionRepository.findByTenantId(tenantId);
        if (existing.isPresent()) {
            return existing.get();
        }

        int maxProf = plan == TenantPlan.FREE ? 1 : (plan == TenantPlan.PRO ? 10 : 999);
        int maxApp = plan == TenantPlan.FREE ? 50 : (plan == TenantPlan.PRO ? 1000 : 99999);

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .tenantId(tenantId)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .maxProfessionals(maxProf)
                .maxAppointmentsPerMonth(maxApp)
                .startedAt(LocalDateTime.now())
                .nextBillingAt(LocalDateTime.now().plusDays(30))
                .build();

        return subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public void validateProfessionalLimit(Long tenantId) {
        if (tenantId == null) return;

        SubscriptionEntity sub = subscriptionRepository.findByTenantId(tenantId)
                .orElse(null);

        if (sub == null) return;

        if (sub.getStatus() == SubscriptionStatus.PAST_DUE || sub.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new PlanLimitExceededException("A assinatura do salão está suspensa ou inadimplente. Atualize seu pagamento.");
        }

        long currentProfessionals = professionalRepository.count();
        if (currentProfessionals >= sub.getMaxProfessionals()) {
            throw new PlanLimitExceededException(
                    String.format("Limite de profissionais do seu plano (%s: máx %d) foi atingido. Faça upgrade de plano para adicionar mais profissionais.",
                            sub.getPlan(), sub.getMaxProfessionals())
            );
        }
    }

    @Transactional(readOnly = true)
    public void validateAppointmentLimit(Long tenantId) {
        if (tenantId == null) return;

        SubscriptionEntity sub = subscriptionRepository.findByTenantId(tenantId)
                .orElse(null);

        if (sub == null) return;

        if (sub.getStatus() == SubscriptionStatus.PAST_DUE || sub.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new PlanLimitExceededException("A assinatura do salão está suspensa ou inadimplente. Atualize seu pagamento.");
        }

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

        long appointmentsThisMonth = appointmentRepository.count();
        if (appointmentsThisMonth >= sub.getMaxAppointmentsPerMonth()) {
            throw new PlanLimitExceededException(
                    String.format("Limite de agendamentos do seu plano (%s: máx %d/mês) foi atingido. Faça upgrade de plano para continuar agendando.",
                            sub.getPlan(), sub.getMaxAppointmentsPerMonth())
            );
        }
    }
}
