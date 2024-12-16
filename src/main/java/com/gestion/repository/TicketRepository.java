package com.gestion.repository;

import com.gestion.model.Event;
import com.gestion.model.Ticket;
import com.gestion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Méthode pour vérifier l'existence de billets non annulés
    boolean existsByEventIdAndStatusNot(Long eventId, String status);

    // Alternative avec @Query
    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.event.id = :eventId AND t.status != :status")
    boolean hasTicketsWithStatusNot(@Param("eventId") Long eventId, @Param("status") String status);

    // Autres méthodes utiles
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByEventId(Long eventId);
    List<Ticket> findByStatus(String status);
    List<Ticket> findByEventIdAndStatus(Long eventId, String status);

    // Compter le nombre de billets par statut
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event.id = :eventId AND t.status = :status")
    long countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") String status);

    List<Ticket> findByUserOrderByPurchaseDateDesc(User user);
    List<Ticket> findByEvent(Event event);

    long countByStatus(String status);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status AND t.event.id = :eventId")
    long countByStatusAndEventId(String status, Long eventId);
}