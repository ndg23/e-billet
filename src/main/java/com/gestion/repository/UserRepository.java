// UserRepository.java
package com.gestion.repository;

import com.gestion.model.Role;
import com.gestion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByActiveTrue();
    List<User> findTop10ByOrderByCreatedAtDesc();
    @Query("SELECT u FROM User u WHERE u.active = true AND u.roles IS EMPTY")
    List<User> findActiveUsersWithoutRoles();
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(String roleName);
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRolesName(Role.RoleType roleName);
}