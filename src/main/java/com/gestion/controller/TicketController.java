// TicketController.java
package com.gestion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gestion.security.UserPrincipal;
import com.gestion.model.Event;
import com.gestion.model.Ticket;
import com.gestion.model.User;
import com.gestion.security.CustomUserDetails;
import com.gestion.security.UserPrincipal;
import com.gestion.services.EventService;
import com.gestion.services.TicketService;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private EventService eventService;

    @GetMapping("/purchase/{eventId}")
    public String showPurchaseForm(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        return "tickets/purchase";
    }

    @PostMapping("/purchase")
    public String purchaseTicket(
            @RequestParam Long eventId,
            @RequestParam Integer quantity,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Ticket ticket = ticketService.purchaseTicket(eventId, userDetails.getUserId(), quantity);
            redirectAttributes.addFlashAttribute("success", "Billets achetés avec succès");
            return "redirect:/tickets/" + ticket.getId() + "/confirmation";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/events/" + eventId;
        }
    }

    @GetMapping("/{id}/confirmation")
    public String showConfirmation(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id);
        model.addAttribute("ticket", ticket);
        return "tickets/confirmation";
    }

//    @GetMapping("/my-tickets")
//    public String showMyTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
//        model.addAttribute("tickets", ticketService.getUserTickets(userDetails.getUsername()));
//        return "tickets/my-tickets";
//    }
    
    @GetMapping("/my-tickets")
    public String showMyTickets(@AuthenticationPrincipal CustomUserDetails userDetails, 
                              Model model) {
        List<Ticket> tickets = ticketService.getUserTickets(userDetails.getUserId());
        model.addAttribute("tickets", tickets);
        return "tickets/my-tickets";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createTicket(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @RequestParam Long eventId,
                             @RequestParam int quantity,
                             RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(eventId);
            User user = userPrincipal.getUser();

            if (event == null || user == null) {
                throw new IllegalArgumentException("L'événement et l'utilisateur sont obligatoires");
            }

            ticketService.createTicket(event, user, quantity);
            redirectAttributes.addFlashAttribute("success", "Billet réservé avec succès");
            return "redirect:/profile/tickets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/events/" + eventId;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/cancel")
    public String cancelTicket(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userPrincipal.getUser();
            ticketService.cancelTicket(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Billet annulé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile/tickets";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/validate/{ticketNumber}")
    public String validateTicket(@PathVariable String ticketNumber,
                               RedirectAttributes redirectAttributes) {
        try {
            boolean isValid = ticketService.validateTicket(ticketNumber);
            if (isValid) {
                redirectAttributes.addFlashAttribute("success", "Billet validé avec succès");
            } else {
                redirectAttributes.addFlashAttribute("error", "Billet invalide ou déjà utilisé");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/tickets";
    }
    
    
    @GetMapping("/{id}")
    public String showTicketDetails(@PathVariable Long id,
                                  @AuthenticationPrincipal UserPrincipal userPrincipal,
                                  Model model) {
        try {
            Ticket ticket = ticketService.getTicketById(id);
            User user = userPrincipal.getUser();
            
            // Vérifier que l'utilisateur est propriétaire du billet
            if (!ticket.getUser().getId().equals(user.getId())) {
                return "redirect:/profile/tickets";
            }
            model.addAttribute("ticket", ticket);
            return "tickets/details";
        } catch (Exception e) {
            return "redirect:/profile/tickets";
        }
    }
    
    
//    @GetMapping("/history")
//    public String showTicketHistory(@AuthenticationPrincipal CustomUserDetails userDetails,
//                                  Model model) {
//        List<Ticket> ticketHistory = ticketService.getUserTicketHistory(userDetails.getUserId());
//        model.addAttribute("tickets", ticketHistory);
//        return "tickets/history";
//    }

    @GetMapping("/{id}/download")
    public String downloadTicket(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails) {
        // Logique pour télécharger le billet
        return "tickets/download";
    }
}