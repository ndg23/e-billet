// EventController.java
package com.gestion.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.model.Event;
import com.gestion.model.EventCategory;
import com.gestion.model.EventType;

import com.gestion.services.EventService;

import com.gestion.dto.EventCategoryDTO;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;



@Controller
@RequestMapping("/events")
@Slf4j
public class EventController {

	private static final Logger logger = LoggerFactory.getLogger(EventController.class);

	@Autowired
    private EventService eventService;
	
	
	@GetMapping
    public String listEvents(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String category,
        @PageableDefault(size = 9, sort = "date") Pageable pageable,
        Model model
    ) {
        try {
            logger.info("Listing events with search: {}, category: {}", search, category);
            
            // Récupérer les événements avec filtrage
            Page<Event> eventsPage = eventService.findFilteredEvents(search, category, pageable);
            model.addAttribute("events", eventsPage);

            // Convertir les catégories en Map pour la vue
            Map<String, String> categories = Arrays.stream(EventCategory.values())
                .collect(Collectors.toMap(
                    Enum::name,
                    EventCategory::getLabel
                ));
            model.addAttribute("categories", categories);

            return "events/list";
        } catch (Exception e) {
            logger.error("Error listing events", e);
            throw e;
        }
    }


    @GetMapping("/{id}")
    public String showEvent(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "events/details";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "events/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createEvent(  @ModelAttribute Event event,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "events/create";
        }

        eventService.createEvent(event);
        redirectAttributes.addFlashAttribute("success", "Événement créé avec succès");
        return "redirect:/events";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "events/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/edit")
    public String updateEvent(@PathVariable Long id,
                            @Valid @ModelAttribute Event event,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "events/edit";
        }

        eventService.updateEvent(id, event);
        redirectAttributes.addFlashAttribute("success", "Événement mis à jour avec succès");
        return "redirect:/events";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("success", "Événement supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/events";
    }

    @GetMapping("/test-data")
    public String createTestData(RedirectAttributes redirectAttributes) {
        try {
            // Créer quelques événements de test
            Event event1 = new Event();
            event1.setName("Concert de Test");
            event1.setDescription("Un super concert de test");
            event1.setDate(LocalDateTime.now().plusDays(7));
            event1.setLocation("Salle de Test");
            event1.setPrice(new BigDecimal("29.99"));
            event1.setTotalSeats(100);
            event1.setAvailableSeats(100);
            event1.setCategory(EventCategory.MUSIC);
            event1.setActive(true);
            event1.setStatus(Event.EventStatus.UPCOMING);
            event1.setType(EventType.CONCERT);
            event1.setImageUrl("https://example.com/concert.jpg");
            eventService.createEvent(event1);

            Event event2 = new Event();
            event2.setName("Match de Football");
            event2.setDescription("Un match passionnant");
            event2.setDate(LocalDateTime.now().plusDays(14));
            event2.setLocation("Stade Municipal");
            event2.setPrice(new BigDecimal("19.99"));
            event2.setTotalSeats(200);
            event2.setAvailableSeats(200);
            event2.setCategory(EventCategory.SPORTS);
            event2.setActive(true);
            event2.setStatus(Event.EventStatus.UPCOMING);
            event2.setType(EventType.SPORTS);
            event2.setImageUrl("https://example.com/football.jpg");
            eventService.createEvent(event2);

            redirectAttributes.addFlashAttribute("success", "Données de test créées avec succès");
            return "redirect:/events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/events";
        }
    }
}