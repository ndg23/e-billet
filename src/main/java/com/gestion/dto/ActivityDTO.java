package com.gestion.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivityDTO {
    private String type;
    private String description;
    private LocalDateTime timestamp;

    public ActivityDTO(String type, String description, LocalDateTime timestamp) {
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Méthodes utilitaires
    public static ActivityDTO createEventActivity(String eventName, LocalDateTime timestamp) {
        return new ActivityDTO("EVENT", "Événement créé : " + eventName, timestamp);
    }

    public static ActivityDTO createTicketActivity(String ticketNumber, LocalDateTime timestamp) {
        return new ActivityDTO("TICKET", "Billet créé : " + ticketNumber, timestamp);
    }
} 