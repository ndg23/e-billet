package com.gestion.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gestion.model.User;
import lombok.Getter;

@Getter
public class UserPrincipal implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;
    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    // Méthodes supplémentaires pour accéder aux informations de l'utilisateur
    public String getFullName() {
        return user.getFullName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public Long getId() {
        return user.getId();
    }

    // Méthode pour accéder à l'utilisateur complet si nécessaire
    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
} 