// UserController.java
package com.gestion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.model.User;
import com.gestion.security.UserPrincipal;
import com.gestion.model.Ticket;
import com.gestion.services.UserService;
import com.gestion.services.TicketService;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public String showProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        User user = userPrincipal.getUser();
        model.addAttribute("user", user);
        List<Ticket> tickets = ticketService.getTicketsByUser(user);
        model.addAttribute("tickets", tickets);
        return "users/profile";
    }

    @GetMapping("/edit")
    public String showEditForm(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        User user = userPrincipal.getUser();
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @ModelAttribute User user,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "users/edit";
        }

        try {
            User currentUser = userPrincipal.getUser();
            userService.updateProfile(currentUser.getId(), user.getFullName(), user.getEmail());
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/edit";
        }
    }

    @GetMapping("/tickets")
    public String showTickets(@AuthenticationPrincipal UserPrincipal userPrincipal, Model model) {
        User user = userPrincipal.getUser();
        List<Ticket> tickets = ticketService.getTicketsByUser(user);
        model.addAttribute("tickets", tickets);
        return "users/tickets";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            User currentUser = userPrincipal.getUser();
            userService.changePassword(currentUser.getId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile";
        }
    }
}