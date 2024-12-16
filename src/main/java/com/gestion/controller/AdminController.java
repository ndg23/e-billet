package com.gestion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.gestion.services.EventService;
import com.gestion.services.TicketService;
import com.gestion.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.gestion.dto.AdminDashboardStats;
import com.gestion.model.Event;
import com.gestion.model.EventCategory;
import com.gestion.model.EventType;
import com.gestion.model.Role;
import com.gestion.model.Ticket;
import com.gestion.model.User;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Dashboard
    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("stats", new AdminDashboardStats(
            eventService.countTotalEvents(),
            ticketService.countActiveTickets(),
            userService.countTotalUsers()
        ));
        return "admin/dashboard";
    }

    // Gestion des utilisateurs
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", Role.RoleType.values());
        return "admin/users/create";
    }

    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("user") User user, 
                            @RequestParam(required = false) List<String> roleNames,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", Role.RoleType.values());
            return "admin/users/create";
        }

        try {
            // Encoder le mot de passe
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);
            
            // Créer l'utilisateur d'abord
            User savedUser = userService.createUser(user);

            // Ajouter les rôles
            if (roleNames != null && !roleNames.isEmpty()) {
                for (String roleName : roleNames) {
                    userService.addRoleToUser(savedUser.getId(), Role.RoleType.valueOf(roleName));
                }
            } else {
                // Ajouter le rôle CUSTOMER par défaut
                userService.addRoleToUser(savedUser.getId(), Role.RoleType.ROLE_CUSTOMER);
            }

            redirectAttributes.addFlashAttribute("success", "Utilisateur créé avec succès");
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("allRoles", Role.RoleType.values());
            model.addAttribute("error", e.getMessage());
            return "admin/users/create";
        }
    }

    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur activé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur désactivé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Gestion des événements
    @GetMapping("/events")
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "admin/events";
    }

    @GetMapping("/events/create")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", EventCategory.values());
        model.addAttribute("types", EventType.values());
        return "admin/events/create";
    }

    @PostMapping("/events/create")
    public String createEvent(@Valid @ModelAttribute Event event,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", EventCategory.values());
            model.addAttribute("types", EventType.values());
            return "admin/events/create";
        }

        try {
            eventService.createEvent(event);
            redirectAttributes.addFlashAttribute("success", "Événement créé avec succès");
            return "redirect:/admin/events";
        } catch (Exception e) {
            model.addAttribute("categories", EventCategory.values());
            model.addAttribute("types", EventType.values());
            model.addAttribute("error", e.getMessage());
            return "admin/events/create";
        }
    }

    @PostMapping("/events/{id}/edit")
    public String updateEvent(@PathVariable Long id,
                             @Valid @ModelAttribute Event event,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", EventCategory.values());
            model.addAttribute("types", EventType.values());
            return "admin/events/edit";
        }

        try {
            eventService.updateEvent(id, event);
            redirectAttributes.addFlashAttribute("success", "Événement mis à jour avec succès");
            return "redirect:/admin/events";
        } catch (Exception e) {
            model.addAttribute("categories", EventCategory.values());
            model.addAttribute("types", EventType.values());
            model.addAttribute("error", e.getMessage());
            return "admin/events/edit";
        }
    }

    // Gestion des réservations
    @GetMapping("/reservations")
    public String listReservations(Model model) {
        model.addAttribute("tickets", ticketService.getAllTickets());
        return "admin/reservations";
    }

    @PostMapping("/tickets/{id}/validate")
    public String validateTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.validateTicket(id);
            redirectAttributes.addFlashAttribute("success", "Billet validé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @PostMapping("/tickets/{id}/cancel")
    public String cancelTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.adminCancelTicket(id);
            redirectAttributes.addFlashAttribute("success", "Billet annulé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservations";
    }
} 