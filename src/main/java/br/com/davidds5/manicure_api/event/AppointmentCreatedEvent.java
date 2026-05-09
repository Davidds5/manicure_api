package br.com.davidds5.manicure_api.event;
import java.time.LocalDateTime;

public record AppointmentCreatedEvent(
    Long appointmentId, 
    String clientEmail, 
    String clientName, 
    LocalDateTime dateTime
) {}