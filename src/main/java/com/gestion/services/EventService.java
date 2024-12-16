// EventService.java
package com.gestion.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gestion.exception.EventNotFoundException;
import com.gestion.model.Event;
import com.gestion.model.EventCategory;
import com.gestion.repository.EventRepository;
import com.gestion.repository.TicketRepository;
import com.gestion.dto.ActivityDTO;

import jakarta.transaction.Transactional;
@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TicketRepository ticketRepository;

    public Page<Event> findFilteredEvents(String search, String categoryName, Pageable pageable) {
        // Add debug logging
        System.out.println("Search: " + search);
        System.out.println("Category: " + categoryName);
        
        if (search != null && categoryName != null) {
            try {
                EventCategory category = EventCategory.valueOf(categoryName);
                return eventRepository.findByNameContainingAndCategory(search, category, pageable);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid category: " + categoryName);
                return eventRepository.findByNameContaining(search, pageable);
            }
        } else if (search != null) {
            return eventRepository.findByNameContaining(search, pageable);
        } else if (categoryName != null) {
            try {
                EventCategory category = EventCategory.valueOf(categoryName);
                return eventRepository.findByCategory(category, pageable);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid category: " + categoryName);
                return eventRepository.findAll(pageable);
            }
        }
        return eventRepository.findAll(pageable);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
    }



//    @Override
    public Event updateEvent(Long id, Event eventDetails) {
        Event event = getEventById(id);
        
        // Mise à jour des champs
        event.setName(eventDetails.getName());
        event.setDescription(eventDetails.getDescription());
        event.setDate(eventDetails.getDate());
        event.setLocation(eventDetails.getLocation());
        event.setPrice(eventDetails.getPrice());
        event.setCategory(eventDetails.getCategory());
        event.setType(eventDetails.getType());
        event.setTotalSeats(eventDetails.getTotalSeats());
        event.setAvailableSeats(eventDetails.getAvailableSeats());
        event.setImageUrl(eventDetails.getImageUrl());  // Ajout de la mise à jour de l'URL de l'image
        event.setStatus(eventDetails.getStatus());
        
        return eventRepository.save(event);
    }

  
    
//    public Event createEvent(Event event) {
//        // Validation logique
//        if (event.getTotalSeats() < 1) {
//            throw new IllegalArgumentException("Le nombre de places doit être positif");
//        }
//        event.setAvailableSeats(event.getTotalSeats());
//        return eventRepository.save(event);
//    }

    public List<Event> getAllActiveEvents() {
        return eventRepository.findByActiveTrue();
    }

//    public Event getEventById(Long id) {
//        return eventRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
//    }

//    public Event updateEvent(Long id, Event eventDetails) {
//        Event event = getEventById(id);
//        event.setName(eventDetails.getName());
//        event.setDescription(eventDetails.getDescription());
//        event.setDate(eventDetails.getDate());
//        event.setLocation(eventDetails.getLocation());
//        event.setPrice(eventDetails.getPrice());
//        return eventRepository.save(event);
//    }
   
    public List<String> getAllCategories() {
        return Arrays.stream(EventCategory.values())
                .map(category -> category.getLabel())
                .collect(Collectors.toList());
    }

    /**
     * Suppression d'un événement
     */
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Événement non trouvé"));

        // Vérifier si l'événement a des billets vendus
        if (ticketRepository.existsByEventIdAndStatusNot(eventId, "CANCELLED")) {
            throw new RuntimeException("Impossible de supprimer un événement avec des billets vendus");
        }

        // Supprimer l'événement
        eventRepository.delete(event);
    }
    
    public List<Event> findFilteredEvents1(String search, String category, Pageable pageable) {
        // Création d'une specification dynamique
        Specification<Event> spec = Specification.where(null);

        // Filtrage par recherche si présent
        if (StringUtils.hasText(search)) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                )
            );
        }

        // Filtrage par catégorie si présent
        if (StringUtils.hasText(category)) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("category"), category)
            );
        }

        // Filtrage pour les événements futurs
        spec = spec.and((root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThan(root.get("date"), LocalDateTime.now())
        );

        // Pagination et tri
        return eventRepository.findAll();
    }
    public List<Event> searchEvents(String keyword, String category) {
        return eventRepository.searchEvents(keyword, category);
    }

    public long countTotalEvents() {
        return eventRepository.count();
    }

    public List<ActivityDTO> getRecentActivities() {
        return eventRepository.findTop10ByOrderByCreatedAtDesc()
            .stream()
            .map(event -> ActivityDTO.createEventActivity(event.getName(), event.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public List<Event> getFeaturedEvents() {
        return eventRepository.findTop3ByActiveTrueAndDateAfterOrderByDateAsc(LocalDateTime.now());
    }

    /**
     * Récupérer tous les événements
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Créer un événement
     */
    public Event createEvent(Event event) {
        // Validation logique
        if (event.getTotalSeats() < 1) {
            throw new IllegalArgumentException("Le nombre de places doit être positif");
        }
        
        // Initialisation des valeurs par défaut
        event.setAvailableSeats(event.getTotalSeats());
        event.setActive(true);
        event.setCreatedAt(LocalDateTime.now());
        
        return eventRepository.save(event);
    }
}