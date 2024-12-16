package com.gestion.security;

//CustomUserDetails.java

import com.gestion.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
 private final User user;

 public CustomUserDetails(User user) {
     this.user = user;
 }

 public Long getUserId() {
     return user.getId();
 }

 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
     return user.getRoles().stream()
         .map(role -> new SimpleGrantedAuthority(role.getName().name()))
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
     return true;
 }

 @Override
 public boolean isCredentialsNonExpired() {
     return true;
 }

 @Override
 public boolean isEnabled() {
     return user.isActive();
 }
}