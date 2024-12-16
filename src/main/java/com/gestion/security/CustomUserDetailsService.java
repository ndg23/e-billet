package com.gestion.security;

//CustomUserDetailsService.java

import com.gestion.model.User;
import com.gestion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

 @Autowired
 private UserRepository userRepository;

 @Override
 public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
     try {
         User user = userRepository.findByEmail(email)
             .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));
         
         return new UserPrincipal(user);
     } catch (Exception e) {
         throw new UsernameNotFoundException("Erreur lors du chargement de l'utilisateur", e);
     }
 }
}