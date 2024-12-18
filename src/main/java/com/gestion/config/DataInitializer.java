package com.gestion.config;

import com.gestion.model.Event;
import com.gestion.model.EventCategory;
import com.gestion.model.Role;
import com.gestion.model.User;
import com.gestion.repository.EventRepository;
import com.gestion.repository.RoleRepository;
import com.gestion.repository.UserRepository;
import com.gestion.model.Role.RoleType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.init-data:false}")
    private boolean shouldInitializeData;

    @Override
    @Transactional
    public void run(String... args) {
        if (!shouldInitializeData) {
            return;
        }

        // 1. Initialiser les rôles
        initRoles();

        // 2. Créer les utilisateurs par défaut
        createDefaultUsers();

        // 3. Créer quelques événements de test
        createSampleEvents();
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Arrays.stream(RoleType.values()).forEach(roleType -> {
                Role role = new Role();
                role.setName(roleType);
                roleRepository.save(role);
            });
        }
    }

    private void createDefaultUsers() {
        if (userRepository.count() <= 3) {
            // Récupérer les rôles
            Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role Admin non trouvé"));
            Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Role Customer non trouvé"));

            // Créer admin
            User admin = new User();
            admin.setFullName("Antoine Diouf");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("Admin123@"));
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            admin.setActive(true);
            userRepository.save(admin);

            // Créer utilisateur normal
            User user = new User();
            user.setFullName("Madiouf");
            user.setEmail("diouf@gmail.com");
            user.setPassword(passwordEncoder.encode("User123@"));
            user.setRoles(new HashSet<>(Arrays.asList(customerRole)));
            user.setActive(true);
            userRepository.save(user);
            User user2 = new User();
            user2.setFullName("Diop Ondo");
            user2.setEmail("diop@gmail.com");
            user2.setPassword(passwordEncoder.encode("User123@"));
            user2.setRoles(new HashSet<>(Arrays.asList(customerRole)));
            user2.setActive(true);
            userRepository.save(user2);
            User user3 = new User();
            user3.setFullName("Japhet Obame");
            user3.setEmail("japhet@gmail.com");
            user3.setPassword(passwordEncoder.encode("User123@"));
            user3.setRoles(new HashSet<>(Arrays.asList(customerRole)));
            user3.setActive(true);
            userRepository.save(user3);

        }
    }

    private void createSampleEvents() {
        if (eventRepository.count() <= 5) {
            Event event1 = new Event();
            event1.setName("Concert de Jazz");
            event1.setDescription("Une soirée jazz exceptionnelle");
            event1.setDate(LocalDateTime.now().plusDays(30));
            event1.setLocation("Salle Pleyel");
            event1.setPrice(new BigDecimal("50.00"));
            event1.setTotalSeats(100);
            event1.setAvailableSeats(100);
            event1.setCategory(EventCategory.MUSIC);
            event1.setActive(true);
            eventRepository.save(event1);

            Event event2 = new Event();
            event2.setName("Match de Football");
            event2.setDescription("Match de championnat");
            event2.setDate(LocalDateTime.now().plusDays(15));
            event2.setLocation("Stade Municipal");
            event2.setPrice(new BigDecimal("25.00"));
            event2.setTotalSeats(200);
            event2.setAvailableSeats(200);
            event2.setCategory(EventCategory.SPORTS);
            event2.setActive(true);
            eventRepository.save(event2);

            Event event3 = new Event();
            event3.setName("Exposition d'Art");
            event3.setDescription("Une exposition d'art moderne");
            event3.setDate(LocalDateTime.now().plusDays(45));
            event3.setLocation("Musée d'Art Moderne");
            event3.setPrice(new BigDecimal("30.00"));
            event3.setTotalSeats(150);
            event3.setAvailableSeats(150);
            event3.setCategory(EventCategory.MUSIC);
            event3.setActive(true);
            eventRepository.save(event3);

            Event event4 = new Event();
            event4.setName("Festival de Musique");
            event4.setDescription("Un festival de musique en plein air");
            event4.setDate(LocalDateTime.now().plusDays(60));
            event4.setLocation("Parc de la Villette");
            event4.setPrice(new BigDecimal("40.00"));
            event4.setTotalSeats(300);
            event4.setAvailableSeats(300);
            event4.setCategory(EventCategory.MUSIC);
            event4.setActive(true);
            eventRepository.save(event4);

            Event event5 = new Event();
            event5.setName("Concert de Jazz");
            event5.setDescription("Une soirée jazz exceptionnelle");
            event5.setDate(LocalDateTime.now().plusDays(30));
            event5.setLocation("Salle Pleyel");
            event5.setPrice(new BigDecimal("50.00"));
            event5.setTotalSeats(100);
            event5.setAvailableSeats(100);
            event5.setCategory(EventCategory.MUSIC);
            event5.setActive(true);
            eventRepository.save(event5);
        }
    }
}