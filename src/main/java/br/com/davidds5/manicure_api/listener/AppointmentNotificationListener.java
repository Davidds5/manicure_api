package br.com.davidds5.manicure_api.listener;

import br.com.davidds5.manicure_api.event.AppointmentCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppointmentNotificationListener {

    @EventListener
    @Async
    public void onAppointmentCreatedEvent(AppointmentCreatedEvent event) {
        log.info("Iniciando envio de e-mail assicrono para:{}", event.clientEmail());

        try {
            Thread.sleep(5000);

            log.info("E-mail enviado com sucesso para {}! Agendamento ID:{}", event.clientName(),event.appointmentId());
        
        }catch (InterruptedException ex){
            log.error("Error ao simular envio de e-mail para {}", event.clientEmail(), ex);
            Thread.currentThread().interrupt();
        }
    }
    
}