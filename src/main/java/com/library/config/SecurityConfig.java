package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF if not needed
                .authorizeHttpRequests()
                .requestMatchers("/api/library/auth/login").permitAll() // Allow access to this endpoint

                //books
                .requestMatchers(HttpMethod.POST, "/api/library/books").hasAnyAuthority("ROLE_ADMIN", "ROLE_LIBRARIAN")
                .requestMatchers(HttpMethod.PUT, "/api/library/books/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_LIBRARIAN")
                .requestMatchers(HttpMethod.DELETE, "/api/library/books/*").hasAnyAuthority("ROLE_ADMIN")

                //users
                .requestMatchers(HttpMethod.POST, "/api/library/users").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/library/users").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/library/users/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_LIBRARIAN")
                .requestMatchers(HttpMethod.PUT, "/api/library/users/*").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/library/users/*").hasAnyAuthority("ROLE_ADMIN")

                //borrow
                .requestMatchers(HttpMethod.POST, "/api/library/borrow").hasAnyAuthority("ROLE_MEMBER", "ROLE_LIBRARIAN")
                .requestMatchers(HttpMethod.PUT, "/api/library/borrow/return/*").hasAnyAuthority("ROLE_MEMBER", "ROLE_LIBRARIAN")
                .requestMatchers(HttpMethod.GET, "/api/library/borrow").hasAnyAuthority("ROLE_LIBRARIAN")

                //others
                .anyRequest().authenticated()// Protect all other endpoints

                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_LIBRARIAN > ROLE_MEMBER");
        return roleHierarchy;
    }
}