package jroullet.mswebapp.config;

import jroullet.mswebapp.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SpringSecurityConfig(CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    /**
     * Password encoder bean using BCrypt algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * SPRING SECURITY OWN MANAGEMENT
     * Provides an AuthenticationManager with a custom UserDetailsService and PasswordEncoder.
     */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, AuthenticationService authenticationService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(authenticationService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
    /**
     * Main security filter chain definition.
     * Handles route access rules, login success handlers, CSRF, and session management.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/signin", "/authentication", "/signup", "/static/**", "/css/**", "/images/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/client/**").hasAnyRole("CLIENT","ADMIN")
                        .requestMatchers("/teacher/**").hasAnyRole("TEACHER" + "ADMIN")
                        .anyRequest()
                        .authenticated()

                )
                .formLogin((form) -> form
                        // customized login form
                        .loginPage("/signin")
                        .loginProcessingUrl("/authentication") // To go through your postmapping /authentication with spring security own means
                        .usernameParameter("email")
                        .permitAll()
                        .defaultSuccessUrl("/home", true) // when successful, goes to "/" URL, => always
                        .failureHandler(customAuthenticationFailureHandler) // custom errors management
                        // Add an error code to the URL when login fails
                        .failureUrl("/signin?authError=Invalid+email+or+password")
                )
                // logout access
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/signin")
                        .permitAll());

        return http.build();
    }



}
