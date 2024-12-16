package com.gestion.services;

import com.gestion.model.Event;
import com.gestion.model.Ticket;
import com.gestion.model.User;
import com.gestion.repository.EventRepository;
import com.gestion.repository.TicketRepository;
import com.gestion.exception.EventNotFoundException;
import com.gestion.exception.TicketNotFoundException;
import com.gestion.repository.UserRepository;
import com.gestion.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    /**
     * Achat de billets
     */
    public Ticket purchaseTicket(Long eventId, Long userId, int quantity) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Événement non trouvé"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        // Vérifier la disponibilité
        if (event.getAvailableSeats() < quantity) {
            throw new RuntimeException("Pas assez de places disponibles");
        }

        // Créer le billet
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setUser(user);
        ticket.setQuantity(quantity);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setStatus("ACTIVE");

        // Mettre à jour les places
        event.setAvailableSeats(event.getAvailableSeats() - quantity);
        
        return ticketRepository.save(ticket);
    }

    /**
     * Validation d'un billet
     */
    public boolean validateTicket(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
            .orElseThrow(() -> new TicketNotFoundException("Billet non trouvé"));

        if (!"ACTIVE".equals(ticket.getStatus())) {
            return false;
        }

        ticket.setStatus("USED");
        ticket.setUsedDate(LocalDateTime.now());
        ticketRepository.save(ticket);

        return true;
    }

    /**
     * Annulation d'un billet
     */
    public void cancelTicket(Long ticketId, Long userId) {
        Ticket ticket = getTicketById(ticketId);

        if (!ticket.getUser().getId().equals(userId)) {
            throw new RuntimeException("Non autorisé");
        }

        ticket.setStatus("CANCELLED");
        
        Event event = ticket.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + ticket.getQuantity());

        ticketRepository.save(ticket);
    }

    /**
     * Récupération des billets
     */
    public List<Ticket> getUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    public List<Ticket> getEventTickets(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    /**
     * Méthodes utilitaires
     */
    private String generateTicketNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new TicketNotFoundException("Billet non trouvé"));
    }

    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepository.findByUserOrderByPurchaseDateDesc(user);
    }

    public Ticket createTicket(Event event, User user, int quantity) {
        if (event == null || user == null) {
            throw new IllegalArgumentException("L'événement et l'utilisateur sont obligatoires");
        }

        if (!event.hasAvailableSeats(quantity)) {
            throw new RuntimeException("Pas assez de places disponibles");
        }

        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setUser(user);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setQuantity(quantity);
        ticket.setStatus("ACTIVE");

        // Mettre à jour les places disponibles
        event.decrementAvailableSeats(quantity);
        eventRepository.save(event);

        return ticketRepository.save(ticket);
    }

    public void cancelTicket(Ticket ticket) {
        if (!"ACTIVE".equals(ticket.getStatus())) {
            throw new RuntimeException("Le billet est déjà annulé");
        }

        // Remettre les places en disponibilité
        Event event = ticket.getEvent();
        event.incrementAvailableSeats(ticket.getQuantity());
        eventRepository.save(event);

        // Marquer le billet comme annulé
        ticket.setStatus("CANCELLED");
        ticketRepository.save(ticket);
    }

    public List<Ticket> getActiveTickets() {
        return ticketRepository.findByStatus("ACTIVE");
    }

    public List<Ticket> getTicketsByEvent(Event event) {
        return ticketRepository.findByEvent(event);
    }

    public long countActiveTickets() {
        return ticketRepository.countByStatus("ACTIVE");
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public void validateTicket(Long id) {
        Ticket ticket = getTicketById(id);
        if (!Ticket.STATUS_ACTIVE.equals(ticket.getStatus())) {
            throw new RuntimeException("Ce billet ne peut pas être validé");
        }
        ticket.setStatus(Ticket.STATUS_USED);
        ticket.setUsedDate(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    public void adminCancelTicket(Long id) {
        Ticket ticket = getTicketById(id);
        if (!Ticket.STATUS_ACTIVE.equals(ticket.getStatus())) {
            throw new RuntimeException("Ce billet ne peut pas être annulé");
        }
        
        // Remettre les places disponibles
        Event event = ticket.getEvent();
        event.incrementAvailableSeats(ticket.getQuantity());
        eventRepository.save(event);

        // Annuler le billet
        ticket.setStatus(Ticket.STATUS_CANCELLED);
        ticketRepository.save(ticket);
    }
}