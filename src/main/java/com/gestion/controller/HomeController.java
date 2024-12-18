package com.gestion.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gestion.dto.EventCategoryDTO;
import com.gestion.model.Event;
import com.gestion.model.EventCategory;
import com.gestion.services.EventService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private EventService eventService;

    @GetMapping({"/", "/home"})
    public String home(Model model, HttpServletRequest request) {
        logger.info("Accessing home page from URL: {}", request.getRequestURL());
        
        try {
            List<Event> featuredEvents = eventService.getFeaturedEvents();
            model.addAttribute("featuredEvents", featuredEvents);

            List<EventCategoryDTO> categories = Arrays.stream(EventCategory.values())
                .map(cat -> new EventCategoryDTO(cat.name(), cat.getLabel()))
                .collect(Collectors.toList());
            model.addAttribute("categories", categories);

            return "home";
        } catch (Exception e) {
            logger.error("Error in home page", e);
            throw e;
        }
    }
} 