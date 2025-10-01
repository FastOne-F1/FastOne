package com.f1.fastone.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserRepository;

public class TestUserFactory {

	public static User createUser(
		String username,
		String password,
		String email,
		UserRole role,
		UserRepository userRepository,
		PasswordEncoder passwordEncoder
	) {
		User user = User.builder()
			.username(username)
			.password(passwordEncoder.encode(password))
			.email(email)
			.nickname(username + "_nick")
			.role(role)
			.isPublic(true)
			.build();

		return userRepository.save(user);
	}

	public static void setSecurityContext(User user) {
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(
				userDetails,
				userDetails.getPassword(),
				userDetails.getAuthorities()
			);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public static User createAndAuthenticateUser(
		String username,
		String password,
		String email,
		UserRole role,
		UserRepository userRepository,
		PasswordEncoder passwordEncoder
	) {
		User user = createUser(username, password, email, role, userRepository, passwordEncoder);
		setSecurityContext(user);
		return user;
	}

	public static void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}
}