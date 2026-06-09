package com.insurex.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    // ✅ Switch bean return type to use NoOpPasswordEncoder (Plain Text)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/about", "/coverages", "/contact", "/signup", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/signin")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 1. Path that triggers the logout process
                        .invalidateHttpSession(true) // 2. Destroys the server-side user session
                        .clearAuthentication(true) // 3. Wipes out security context authentication details
                        .deleteCookies("JSESSIONID") // 4. Removes the session identifier cookie from the browser
                        .logoutSuccessUrl("/signin?logout") // 5. Forwards back to sign-in with a success flag
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean admin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            response.sendRedirect(admin ? "/admin" : "/userDash");
        };
    }
}
