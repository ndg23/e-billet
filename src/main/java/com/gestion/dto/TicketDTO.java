package com.gestion.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private String ticketNumber;
    private String eventName;
    private LocalDateTime eventDate;
    private Integer quantity;
    private String status;
    private LocalDateTime purchaseDate;
}
