package com.gestion.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.model.User;
import com.gestion.model.Role;
import com.gestion.model.Ticket;
import com.gestion.repository.RoleRepository;
import com.gestion.repository.UserRepository;
import com.gestion.dto.SignupRequest;
import com.gestion.exception.UserNotFoundException;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Inscription d'un nouvel utilisateur
     */
    public User registerNewUser(SignupRequest signupRequest) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFullName(signupRequest.getFullName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setActive(true);

        // Attribuer le rôle CUSTOMER par défaut
        Role customerRole = roleRepository.findByName(Role.RoleType.ROLE_CUSTOMER)
            .orElseThrow(() -> new RuntimeException("Rôle CUSTOMER non trouvé"));
        user.addRole(customerRole);

        return userRepository.save(user);
    }

    /**
     * Connexion utilisateur
     */
    public User loginUser(String email, String password) {
        User user = getUserByEmail(email);
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        return user;
    }

    /**
     * Mise à jour du profil utilisateur
     */
    public User updateProfile(Long userId, String fullName, String email) {
        User user = getUserById(userId);

        user.setFullName(fullName);
        if (!user.getEmail().equals(email)) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            user.setEmail(email);
        }

        return userRepository.save(user);
    }

    /**
     * Changement de mot de passe
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Mot de passe actuel incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Récupération des billets de l'utilisateur
     */
    @SuppressWarnings("unchecked")
	public List<Ticket> getUserTickets(Long userId) {
        User user = getUserById(userId);
        return (List<Ticket>) user.getTickets();
    }

    /**
     * Suppression du compte utilisateur
     */
    public void deleteAccount(Long userId, String password) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        userRepository.delete(user);
    }

    /**
     * Méthodes utilitaires
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void activateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        // Empêcher la désactivation du dernier admin
        if (user.isAdmin() && countAdminUsers() <= 1) {
            throw new RuntimeException("Impossible de désactiver le dernier administrateur");
        }
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void addRoleToUser(Long userId, Role.RoleType roleType) {
        User user = getUserById(userId);
        Role role = roleRepository.findByName(roleType)
            .orElseThrow(() -> new RuntimeException("Role " + roleType + " non trouvé"));
        user.addRole(role);
        userRepository.save(user);
    }

   

    public List<User> getRecentUsers() {
        return userRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

   

    private long countAdminUsers() {
        return userRepository.countByRolesName(Role.RoleType.ROLE_ADMIN);
    }

    /**
     * Créer un nouvel utilisateur
     */
    @Transactional
    public User createUser(User user) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Validation basique du mot de passe
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 4 caractères");
        }

        return userRepository.save(user);
    }

    /**
     * Récupérer tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Compter le nombre total d'utilisateurs
     */
    public long countTotalUsers() {
        return userRepository.count();
    }
}