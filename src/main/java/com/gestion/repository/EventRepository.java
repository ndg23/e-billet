// EventRepository.java
package com.gestion.repository;

import com.gestion.model.Event;
import com.gestion.model.EventCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    
    List<Event> findByDateAfterAndActiveTrue(LocalDateTime date);
    
    @Query("SELECT e FROM Event e WHERE " +
           "(:keyword IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "e.date > CURRENT_TIMESTAMP AND e.active = true")
    List<Event> searchEvents(String keyword, String category);
    
    @Query("SELECT DISTINCT e.category FROM Event e ORDER BY e.category")
    List<String> findAllCategories();
    
    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.event.id = :eventId AND t.status != 'CANCELLED'")
    boolean hasActiveTickets(@Param("eventId") Long eventId);
    
    // Pour le soft delete
    List<Event> findByActiveTrue();
    
    
    Page<Event> findByNameContaining(String name, Pageable pageable);
    Page<Event> findByCategory(EventCategory category, Pageable pageable);
    Page<Event> findByNameContainingAndCategory(String name, EventCategory category, Pageable pageable);
    
    List<Event> findTop10ByOrderByCreatedAtDesc();
    
    @Query("SELECT e FROM Event e WHERE e.date > CURRENT_TIMESTAMP ORDER BY e.date ASC")
    List<Event> findUpcomingEvents();
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.date > CURRENT_TIMESTAMP")
    long countUpcomingEvents();

    List<Event> findTop3ByActiveTrueAndDateAfterOrderByDateAsc(LocalDateTime date);
}
