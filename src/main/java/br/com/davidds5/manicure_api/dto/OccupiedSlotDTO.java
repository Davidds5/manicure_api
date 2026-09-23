package br.com.davidds5.manicure_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupiedSlotDTO {
    private String time;             // Ex: "14:00"
    private String endTime;          // Ex: "15:00"
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer durationMinutes;
}
