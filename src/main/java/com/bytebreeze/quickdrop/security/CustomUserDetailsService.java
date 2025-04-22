package com.bytebreeze.quickdrop.security;

import com.bytebreeze.quickdrop.entity.User;
import com.bytebreeze.quickdrop.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
	private UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository
				.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found by email :" + email));

		return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
				.password(user.getPassword())
				.authorities(user.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority(role.name()))
						.toList())
				.build();
	}
}
