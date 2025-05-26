package com.aditu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Obtener valores de entorno para logout dinámico
        String auth0ClientId = System.getenv("AUTH0_CLIENT_ID");
        String logoutReturnTo = System.getenv("LOGOUT_RETURN_URL");
        String logoutUrl = "https://dev-48dcolodzd0zfepb.us.auth0.com/v2/logout"
                + "?client_id=" + auth0ClientId
                + "&returnTo=" + logoutReturnTo;

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/oauth2/**", "/login/oauth2/**", "/register", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/home", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl(logoutUrl)
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
