package com.letsplay.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean //creating an object to use in other parts of the application
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/users/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/products","/products/**").permitAll() // everybody can view
                    .requestMatchers(HttpMethod.POST, "/products/**").permitAll() // only admin can add
                    .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN") // only admin can update
                    .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN") // only admin can delete
                    .requestMatchers(HttpMethod.POST, "/users").permitAll()
                    .requestMatchers(HttpMethod.GET, "/users").permitAll()
                    .requestMatchers("/users/**").hasRole("ADMIN") // only admin can manage users
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

