package com.gestion.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "roles")
@Data
@Setter
@Getter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    public enum RoleType {
        ROLE_ADMIN,
        ROLE_CUSTOMER
    }

    // Constructeurs
    public Role() {
    }

    public Role(RoleType name) {
        this.name = name;
    }

    // Getters et Setters explicites en plus de ceux générés par Lombok
    public RoleType getName() {
        return this.name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }

    // Equals et HashCode basés sur le nom du rôle
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return name == role.name;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name=" + name +
                '}';
    }
} 