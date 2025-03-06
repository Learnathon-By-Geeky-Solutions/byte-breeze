package com.bytebreeze.quickdrop.security;

import com.bytebreeze.quickdrop.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public RateLimiter registrationRateLimiter() {
        return RateLimiter.create(10.0); // 10 requests per second
    }

    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**") // Match requests starting with /admin/
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/login").permitAll()  // Allow login page for anyone
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Only admin can access /admin/** pages
                        .anyRequest().authenticated()  // Any other request must be authenticated
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard")  // Redirect to admin dashboard after successful login
                        .failureUrl("/admin/login?error=true")  // Redirect back to login on failure
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")  // Redirect to login page after logout
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // Use cookie-based CSRF token repository
                );

        return http.build();
    }


    @Bean
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/user/**", "/auth/**", "/logout")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/user/login")
                        .defaultSuccessUrl("/user/dashboard")
                        .failureUrl("/auth/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // Use cookie-based CSRF token repository
                );
        return http.build();
    }


    @Bean
    public SecurityFilterChain riderSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/rider/**") // Match requests starting with /rider/
                .authorizeHttpRequests(authorize -> authorize // Allow login page for anyone
                        .requestMatchers("/rider/login", "/rider/register", "/rider/onboarding/**").permitAll()
                        .requestMatchers("/rider/**").hasRole("RIDER")  // Only rider can access /rider/** pages
                        .anyRequest().authenticated()  // Any other request must be authenticated
                )
                .formLogin(form -> form
                        .loginPage("/rider/login")
                        .loginProcessingUrl("/rider/login")
                        .defaultSuccessUrl("/rider/dashboard")  // Redirect to rider dashboard after successful login
                        .failureUrl("/rider/login?error=true")  // Redirect back to login on failure
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")  // Redirect to login page after logout
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // Use cookie-based CSRF token repository
                );

        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
