package jroullet.mswebapp.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SpringSecurityConfig(CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
                                CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    /**
     * SPRING SECURITY OWN MANAGEMENT
     * Provides an AuthenticationManager with a custom UserDetailsService and PasswordEncoder.
     */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, AuthenticationService authenticationService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        // Providing our custom service as AuthenticationProvider instead of UserDetailsService (verification is done in ms-identity)
        authenticationManagerBuilder.authenticationProvider(authenticationService);

        return authenticationManagerBuilder.build();
    }

    /**
     * Main security filter chain definition.
     * Handles route access rules, login success handlers, CSRF, and session management.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/signup", "/signin") // Désactive CSRF pour ces endpoints
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/signin", "/signup", "/static/**", "/error", "/css/**", "/images/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/client/**").hasAnyRole("CLIENT")
                        .requestMatchers("/teacher/**").hasAnyRole("TEACHER")
                        .anyRequest()
                        .authenticated()

                )
                .formLogin((form) -> form
                        // customized login form
                        .loginPage("/signin")
                        .loginProcessingUrl("/authenticate") // To go through postmapping /authentication, using our own AuthenticationProvider (to retrieve information from ms-identity)
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler) // custom routes by authenticated role
                        .failureHandler(customAuthenticationFailureHandler) // custom errors management
                        .permitAll()
                )
                // logout management
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/signin?logout=true")
                        .permitAll());

        return http.build();
    }



}
