package com.gestion.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "tickets")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @NotNull(message = "L'événement est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotNull(message = "L'utilisateur est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Min(value = 1, message = "La quantité doit être au moins 1")
    @Column(nullable = false)
    private Integer quantity = 1;

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_USED = "USED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    @Column(nullable = false)
    private String status = STATUS_ACTIVE;

    private LocalDateTime usedDate;

    // Constructeurs
    // public Ticket() {
    //     this.purchaseDate = LocalDateTime.now();
    //     this.status = STATUS_ACTIVE;
    // }

    // Méthodes utilitaires
    public boolean isActive() {
        return STATUS_ACTIVE.equals(this.status);
    }

    public boolean isUsed() {
        return STATUS_USED.equals(this.status);
    }

    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(this.status);
    }

    public String getStatusLabel() {
        return switch (status) {
            case STATUS_ACTIVE -> "En attente de validation";
            case STATUS_USED -> "Validé";
            case STATUS_CANCELLED -> "Annulé";
            default -> status;
        };
    }

    public String getStatusColor() {
        return switch (status) {
            case STATUS_ACTIVE -> "green";
            case STATUS_USED -> "blue";
            case STATUS_CANCELLED -> "gray";
            default -> "gray";
        };
    }

    // Getters manuels
    public Long getId() {
        return id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getUsedDate() {
        return usedDate;
    }

    // Setters manuels
    public void setId(Long id) {
        this.id = id;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUsedDate(LocalDateTime usedDate) {
        this.usedDate = usedDate;
    }
}