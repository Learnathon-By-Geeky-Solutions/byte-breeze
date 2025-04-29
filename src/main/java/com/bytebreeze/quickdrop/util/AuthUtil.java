package com.bytebreeze.quickdrop.util;

import java.util.Collections;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUtil {
	private AuthUtil() {}

	public static String getAuthenticatedUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails userDetails) {
				return userDetails.getUsername();
			}
			return principal.toString();
		}
		return null;
	}

	public static boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getPrincipal() instanceof UserDetails;
	}

	public static List<String> getAuthenticatedUserRoles() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getAuthorities() != null) {
			return authentication.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.toList();
		}
		return Collections.emptyList();
	}
}
