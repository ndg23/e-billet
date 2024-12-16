package com.gestion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "events")
@Data
@Setter
@Getter
// @NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<Ticket> tickets = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum EventStatus {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    private EventType type;

    // Constructeur par défaut
    public Event() {
        this.status = EventStatus.UPCOMING;
        this.type = EventType.OTHER;
        this.category = EventCategory.OTHER;
        this.active = true;
        this.tickets = new HashSet<>();
    }

    // Constructeur avec paramètres
    public Event(String name, String description, LocalDateTime date, String location, BigDecimal price, Integer totalSeats) {
        this();  // Appel du constructeur par défaut
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.price = price;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public EventCategory getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public EventStatus getStatus() {
        return status;
    }

    public EventType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Méthodes utilitaires pour la gestion des tickets
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setEvent(this);
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setEvent(null);
    }

    // Méthodes métier
    public boolean hasAvailableSeats(int quantity) {
        return this.availableSeats >= quantity;
    }

    public void decrementAvailableSeats(int quantity) {
        if (this.availableSeats >= quantity) {
            this.availableSeats -= quantity;
        } else {
            throw new IllegalStateException("Pas assez de places disponibles");
        }
    }

    public void incrementAvailableSeats(int quantity) {
        if (this.availableSeats + quantity <= this.totalSeats) {
            this.availableSeats += quantity;
        } else {
            throw new IllegalStateException("Impossible d'ajouter plus de places que le total");
        }
    }

    public boolean isUpcoming() {
        return this.date.isAfter(LocalDateTime.now());
    }

    // Equals et HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return id != null && id.equals(event.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // ToString
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", price=" + price +
                ", availableSeats=" + availableSeats +
                '}';
    }
}