package com.bytebreeze.quickdrop.security;

import com.bytebreeze.quickdrop.repository.UserRepository;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	private static final String ADMIN_LOGIN_URL = "/admin/login";
	private static final String REMEMBER_ME = "remember-me";
	private static final String RIDER_LOGIN = "/rider/login";

	@Value("${quickdrop.security.remember-me.key}")
	private String rememberMeKey;

	private int rememberMeTokenValidityInSeconds = 30 * 24 * 60 * 60; // one month

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
		http.securityMatcher("/admin/**") // Match requests starting with /admin/
				.authorizeHttpRequests(
						authorize -> authorize
								.requestMatchers(ADMIN_LOGIN_URL)
								.permitAll() // Allow login page for anyone
								.requestMatchers("/admin/**")
								.hasRole("ADMIN") // Only admin can access /admin/** pages
								.anyRequest()
								.authenticated() // Any other request must be authenticated
						)
				.formLogin(
						form -> form.loginPage(ADMIN_LOGIN_URL)
								.loginProcessingUrl(ADMIN_LOGIN_URL)
								.defaultSuccessUrl(
										"/admin/dashboard") // Redirect to admin dashboard after successful login
								.failureUrl("/admin/login?error=true") // Redirect back to login on failure
						)
				.rememberMe(rememberMe -> rememberMe
						.key(rememberMeKey)
						.rememberMeParameter(REMEMBER_ME)
						.tokenValiditySeconds(rememberMeTokenValidityInSeconds)
						.rememberMeCookieName("admin-remember-me"))
				.logout(logout -> logout.logoutUrl("/admin/logout")
						.logoutSuccessUrl(ADMIN_LOGIN_URL) // Redirect to login page after logout
						.invalidateHttpSession(true)
						.clearAuthentication(true))
				.csrf(
						csrf -> csrf.csrfTokenRepository(
								new CookieCsrfTokenRepository()) // Use cookie-based CSRF token repository
						);

		return http.build();
	}

	@Bean
	public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/user/**", "/auth/**", "/user/logout", "parcel/shipping-cost", "/")
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/auth/login")
						.permitAll()
						.requestMatchers("parcel/shipping-cost")
						.permitAll()
						.requestMatchers("/")
						.permitAll()
						.requestMatchers("/auth/register")
						.permitAll()
						.requestMatchers("/user/**")
						.hasRole("USER")
						.anyRequest()
						.authenticated())
				.formLogin(form -> form.loginPage("/auth/login")
						.loginProcessingUrl("/user/login")
						.defaultSuccessUrl("/user/dashboard")
						.failureUrl("/auth/login?error=true"))
				.rememberMe(rememberMe -> rememberMe
						.key(rememberMeKey)
						.rememberMeParameter(REMEMBER_ME)
						.tokenValiditySeconds(rememberMeTokenValidityInSeconds)
						.rememberMeCookieName("user-remember-me"))
				.logout(logout -> logout.logoutUrl("/user/logout")
						.logoutSuccessUrl("/auth/logout")
						.invalidateHttpSession(true)
						.clearAuthentication(true))
				.csrf(
						csrf -> csrf.csrfTokenRepository(
								new CookieCsrfTokenRepository()) // Use cookie-based CSRF token repository
						);
		return http.build();
	}

	@Bean
	public SecurityFilterChain riderSecurityFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/rider/**") // Match requests starting with /rider/
				.authorizeHttpRequests(
						authorize -> authorize // Allow login page for anyone
								.requestMatchers(RIDER_LOGIN, "/rider/register", "/rider/onboarding/**")
								.permitAll()
								.requestMatchers("/rider/**")
								.hasRole("RIDER") // Only rider can access /rider/** pages
								.anyRequest()
								.authenticated() // Any other request must be authenticated
						)
				.formLogin(
						form -> form.loginPage(RIDER_LOGIN)
								.loginProcessingUrl(RIDER_LOGIN)
								.defaultSuccessUrl(
										"/rider/dashboard") // Redirect to rider dashboard after successful login
								.failureUrl("/rider/login?error=true") // Redirect back to login on failure
						)
				.rememberMe(rememberMe -> rememberMe
						.key(rememberMeKey)
						.rememberMeParameter(REMEMBER_ME)
						.tokenValiditySeconds(rememberMeTokenValidityInSeconds)
						.rememberMeCookieName("rider-remember-me"))
				.logout(logout -> logout.logoutUrl("/rider/logout")
						.logoutSuccessUrl(RIDER_LOGIN) // Redirect to login page after logout
						.invalidateHttpSession(true)
						.clearAuthentication(true))
				.csrf(
						csrf -> csrf.csrfTokenRepository(
								new CookieCsrfTokenRepository()) // Use cookie-based CSRF token repository
						);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}
}
