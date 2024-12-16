package com.gestion.config;

import com.gestion.model.Event;
import com.gestion.model.EventCategory;
import com.gestion.model.Role;
import com.gestion.model.Ticket;
import com.gestion.model.User;
import com.gestion.repository.EventRepository;
import com.gestion.repository.RoleRepository;
import com.gestion.repository.TicketRepository;
import com.gestion.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initializeData() {
        // Initialiser les rôles d'abord
        initRoles();
        
        // Nettoyer les données existantes
        ticketRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();

        // Créer des utilisateurs avec rôles
        User admin = createAdminUser("admin@example.com", "Admin");
        User user1 = createCustomerUser("user1@example.com", "John Doe");
        User user2 = createCustomerUser("user2@example.com", "Jane Smith");

        // Créer des événements
        List<Event> events = createEvents();

        // Créer des tickets
        createTickets(events, List.of(admin, user1, user2));
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialiser les rôles
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(Role.RoleType.ROLE_ADMIN));
            roleRepository.save(new Role(Role.RoleType.ROLE_CUSTOMER));
        }

        // Créer un admin par défaut si aucun n'existe
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("Admin123@"));
            admin.setFullName("Admin");
            admin.setActive(true);
           
            Role adminRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role Admin non trouvé"));
            admin.addRole(adminRole);
            
            userRepository.save(admin);
        }
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(Role.RoleType.ROLE_ADMIN));
            roleRepository.save(new Role(Role.RoleType.ROLE_CUSTOMER));
        }
    }

    private User createAdminUser(String email, String fullName) {
        User user = createBaseUser(email, fullName);
        Role adminRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN)
            .orElseThrow(() -> new RuntimeException("Role Admin non trouvé"));
        user.addRole(adminRole);
        return userRepository.save(user);
    }

    private User createCustomerUser(String email, String fullName) {
        User user = createBaseUser(email, fullName);
        Role customerRole = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER)
            .orElseThrow(() -> new RuntimeException("Role Customer non trouvé"));
        user.addRole(customerRole);
        return userRepository.save(user);
    }

    private User createBaseUser(String email, String fullName) {
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode("Password123@"));
        user.setActive(true);
        return user;
    }

    private List<Event> createEvents() {
        String[] eventNames = {
            "Conférence Tech 2024", 
            "Festival de Musique", 
            "Marathon Urbain", 
            "Exposition d'Art", 
            "Salon du Livre",
            "Concert de Jazz",
            "Festival de Danse",
            "Conférence sur l'IA",
            "Exposition de Photographie",
            "Festival de Cuisine",
            "Concert de Pop",
            "Festival de Mode",
            "Concert de Rock",
            "Festival de Musique",
            "Concert de Jazz",
            "Festival de Danse",
            "Concert de Pop",
            "Festival de Mode",
            "Concert de Rock",
        };

        EventCategory[] categories = EventCategory.values();
        Random random = new Random();

        return IntStream.range(0, 15)
            .mapToObj(i -> {
                Event event = new Event();
                event.setName(eventNames[i]);
                event.setDescription("Description détaillée pour " + eventNames[i]);
                event.setDate(LocalDateTime.now().plusDays(random.nextInt(30) + 15));
                event.setLocation("Lieu " + (i + 1));
                event.setTotalSeats(random.nextInt(200) + 100);
                event.setAvailableSeats(event.getTotalSeats());
                event.setPrice(BigDecimal.valueOf(random.nextDouble(20, 100)));
                event.setCategory(categories[random.nextInt(categories.length)]);
                event.setImageUrl("/images/event-" + (i + 1) + ".jpg");
                event.setActive(true);
                return eventRepository.save(event);
            })
            .toList();
    }

    private void createTickets(List<Event> events, List<User> users) {
        Random random = new Random();

        events.forEach(event -> {
            // Créer quelques tickets pour chaque événement
            IntStream.range(0, random.nextInt(10) + 5)
                .forEach(i -> {
                    Ticket ticket = new Ticket();
                    ticket.setEvent(event);
                    ticket.setUser(users.get(random.nextInt(users.size())));
                    ticket.setTicketNumber(UUID.randomUUID().toString());
                    ticket.setPurchaseDate(LocalDateTime.now());
                    ticket.setQuantity(1);
                    ticket.setStatus("ACTIVE");
                    
                    // Réduire les places disponibles
                    event.setAvailableSeats(event.getAvailableSeats() - 1);
                    eventRepository.save(event);

                    ticketRepository.save(ticket);
                });
        });
    }
}