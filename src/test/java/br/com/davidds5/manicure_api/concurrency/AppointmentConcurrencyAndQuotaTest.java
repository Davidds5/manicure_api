package br.com.davidds5.manicure_api.concurrency;

import br.com.davidds5.manicure_api.config.TenantContext;
import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.dto.AppointmentUpdateDTO;
import br.com.davidds5.manicure_api.dto.PaymentCreateDTO;
import br.com.davidds5.manicure_api.entity.*;
import br.com.davidds5.manicure_api.enums.ProfessionalRole;
import br.com.davidds5.manicure_api.enums.TenantPlan;
import br.com.davidds5.manicure_api.enums.TenantStatus;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.PlanLimitExceededException;
import br.com.davidds5.manicure_api.exceptions.TimeConflictException;
import br.com.davidds5.manicure_api.repository.*;
import br.com.davidds5.manicure_api.service.AppointmentService;
import br.com.davidds5.manicure_api.service.PaymentService;
import br.com.davidds5.manicure_api.service.SubscriptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class AppointmentConcurrencyAndQuotaTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private TenantEntity tenant;
    private ClientEntity client;
    private ProfessionalEntity professional;
    private ServiceEntity service;

    @BeforeEach
    void setUp() {
        tenant = tenantRepository.save(TenantEntity.builder()
                .name("Salão Concorrência")
                .slug("concorrencia-" + System.currentTimeMillis())
                .plan(TenantPlan.FREE)
                .status(TenantStatus.ACTIVE)
                .build());

        TenantContext.setTenantId(tenant.getId());

        client = clientRepository.save(ClientEntity.builder()
                .tenantId(tenant.getId())
                .name("Cliente Concorrencia")
                .email("cliente-" + System.currentTimeMillis() + "@test.com")
                .phone("(11) 97777-6666")
                .password("senha123")
                .build());

        professional = professionalRepository.save(ProfessionalEntity.builder()
                .tenantId(tenant.getId())
                .name("Profissional Concorrencia")
                .email("pro-" + System.currentTimeMillis() + "@test.com")
                .password("senha123")
                .specialty("Nails")
                .role(ProfessionalRole.PROFESSIONAL)
                .active(true)
                .build());

        service = serviceRepository.save(ServiceEntity.builder()
                .tenantId(tenant.getId())
                .name("Serviço Rápido")
                .description("Desc")
                .price(BigDecimal.valueOf(80.00))
                .duration(30)
                .active(true)
                .build());

        subscriptionService.createDefaultSubscription(tenant.getId(), TenantPlan.FREE);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Item 1: Atualizar agendamento com data no passado para COMPLETED deve funcionar sem falha de constraint")
    @Transactional
    void updatePastAppointmentToCompleted_Success() {
        // Cria agendamento no passado diretamente no repositório (simulando atendimento concluído ou histórico)
        AppointmentEntity pastAppointment = appointmentRepository.save(AppointmentEntity.builder()
                .tenantId(tenant.getId())
                .client(client)
                .professional(professional)
                .service(service)
                .dateTime(LocalDateTime.now().minusDays(3))
                .status(AppointmentEntity.AppointmentStatus.SCHEDULED)
                .build());

        // Atualização de status para COMPLETED não deve falhar
        AppointmentUpdateDTO updateDTO = new AppointmentUpdateDTO();
        updateDTO.setStatus(AppointmentEntity.AppointmentStatus.COMPLETED);

        AppointmentDTO updated = appointmentService.updateAppointment(pastAppointment.getId(), updateDTO);
        assertEquals(AppointmentEntity.AppointmentStatus.COMPLETED, updated.getStatus());
    }

    @Test
    @DisplayName("Item 1: Criação de agendamento no passado deve ser rejeitada pela camada de negócio")
    void createPastAppointment_Rejected() {
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setClientId(client.getId());
        dto.setProfessionalId(professional.getId());
        dto.setServiceId(service.getId());
        dto.setDateTime(LocalDateTime.now().minusHours(2));

        assertThrows(BusinessException.class, () -> appointmentService.createAppointment(dto));
    }

    @Test
    @DisplayName("Item 7: Cotas de agendamento devem contar apenas o mês atual no fuso America/Sao_Paulo e isoladas por tenant")
    @Transactional
    void monthlyQuota_CountsOnlyCurrentMonthAndCurrentTenant() {
        // Cria agendamentos no mês anterior
        LocalDateTime pastMonth = LocalDateTime.now().minusMonths(1);
        for (int i = 0; i < 50; i++) {
            appointmentRepository.save(AppointmentEntity.builder()
                    .tenantId(tenant.getId())
                    .client(client)
                    .professional(professional)
                    .service(service)
                    .dateTime(pastMonth.plusHours(i))
                    .status(AppointmentEntity.AppointmentStatus.COMPLETED)
                    .build());
        }

        // Limite do plano FREE é 50 agendamentos/mês. Os 50 do mês passado não devem bloquear o mês atual
        appointmentRepository.flush();
        assertDoesNotThrow(() -> subscriptionService.validateAppointmentLimit(tenant.getId()));

        // Preenche a cota do mês atual até atingir 50 (limite do plano FREE)
        LocalDateTime currentMonth = LocalDateTime.now();
        for (int i = 0; i < 50; i++) {
            appointmentRepository.save(AppointmentEntity.builder()
                    .tenantId(tenant.getId())
                    .client(client)
                    .professional(professional)
                    .service(service)
                    .dateTime(currentMonth.plusMinutes(i * 10))
                    .status(AppointmentEntity.AppointmentStatus.SCHEDULED)
                    .build());
        }
        appointmentRepository.flush();

        // Ao atingir a cota de 50 no mês atual, deve bloquear
        assertThrows(PlanLimitExceededException.class, () -> subscriptionService.validateAppointmentLimit(tenant.getId()));
    }

    @Test
    @DisplayName("Item 11: Concorrência e bloqueio pessimista — múltiplas threads tentando agendar o mesmo horário resultam em exatamente 1 sucesso")
    void testConcurrencyPessimisticLock_OverbookingPrevention() throws InterruptedException {
        int threadCount = 4;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());
        LocalDateTime bookingSlot = LocalDateTime.now().plusDays(5).withHour(14).withMinute(0).withSecond(0).withNano(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    TenantContext.setTenantId(tenant.getId());
                    AppointmentCreateDTO dto = new AppointmentCreateDTO();
                    dto.setClientId(client.getId());
                    dto.setProfessionalId(professional.getId());
                    dto.setServiceId(service.getId());
                    dto.setDateTime(bookingSlot);

                    appointmentService.createAppointment(dto);
                    results.add(true);
                } catch (BusinessException ex) {
                    results.add(false);
                } catch (Exception ex) {
                    results.add(false);
                } finally {
                    TenantContext.clear();
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        long successCount = results.stream().filter(b -> b).count();
        assertEquals(1, successCount, "Exatamente um agendamento deve ter sucesso no mesmo horário.");
    }

    @Test
    @DisplayName("Item 11: Horários adjacentes não conflitam e agendamento cancelado libera horário")
    @Transactional
    void adjacentAndCancelledAppointments_DoNotConflict() {
        LocalDateTime timeSlot1 = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime timeSlot2 = timeSlot1.plusMinutes(service.getDuration()); // 10:30

        AppointmentCreateDTO dto1 = new AppointmentCreateDTO();
        dto1.setClientId(client.getId());
        dto1.setProfessionalId(professional.getId());
        dto1.setServiceId(service.getId());
        dto1.setDateTime(timeSlot1);

        AppointmentDTO app1 = appointmentService.createAppointment(dto1);
        assertNotNull(app1);

        // Horário adjacente (imediatamente após) deve ter sucesso
        AppointmentCreateDTO dto2 = new AppointmentCreateDTO();
        dto2.setClientId(client.getId());
        dto2.setProfessionalId(professional.getId());
        dto2.setServiceId(service.getId());
        dto2.setDateTime(timeSlot2);

        AppointmentDTO app2 = appointmentService.createAppointment(dto2);
        assertNotNull(app2);

        // Cancelamento do app1 libera o horário para nova reserva
        appointmentService.cancelAppointment(app1.getId());

        AppointmentCreateDTO dtoRebook = new AppointmentCreateDTO();
        dtoRebook.setClientId(client.getId());
        dtoRebook.setProfessionalId(professional.getId());
        dtoRebook.setServiceId(service.getId());
        dtoRebook.setDateTime(timeSlot1);

        AppointmentDTO rebooked = appointmentService.createAppointment(dtoRebook);
        assertNotNull(rebooked);
    }

    @Test
    @DisplayName("Item 12: Pagamentos usam BigDecimal.compareTo para validação exata de valores monetários")
    @Transactional
    void paymentService_UsesBigDecimalCompareTo() {
        AppointmentEntity app = appointmentRepository.save(AppointmentEntity.builder()
                .tenantId(tenant.getId())
                .client(client)
                .professional(professional)
                .service(service)
                .dateTime(LocalDateTime.now().plusDays(4))
                .status(AppointmentEntity.AppointmentStatus.SCHEDULED)
                .build());

        // Pagamento com valor divergente (ex: 79.99 em vez de 80.00) deve ser rejeitado
        PaymentCreateDTO wrongAmount = PaymentCreateDTO.builder()
                .amount(BigDecimal.valueOf(79.99))
                .paymentMethod("CREDIT_CARD")
                .build();

        assertThrows(BusinessException.class, () -> paymentService.registerPayment(app.getId(), wrongAmount));

        // Pagamento com valor exato (80.00) usando compareTo deve ser aceito
        PaymentCreateDTO correctAmount = PaymentCreateDTO.builder()
                .amount(new BigDecimal("80.00"))
                .paymentMethod("PIX")
                .build();

        var paymentDTO = paymentService.registerPayment(app.getId(), correctAmount);
        assertNotNull(paymentDTO);
        assertEquals(0, new BigDecimal("80.00").compareTo(paymentDTO.getAmount()));
    }
}
