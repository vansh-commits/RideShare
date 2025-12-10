package com.example.RideShare.service;

import com.example.RideShare.dto.AuthResponse;
import com.example.RideShare.dto.LoginRequest;
import com.example.RideShare.dto.RegisterRequest;
import com.example.RideShare.exception.BadRequestException;
import com.example.RideShare.model.User;
import com.example.RideShare.repository.UserRepository;
import com.example.RideShare.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final CustomUserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;

	public void register(RegisterRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new BadRequestException("Username already taken");
		}
		if (!("ROLE_USER".equals(request.getRole()) || "ROLE_DRIVER".equals(request.getRole()))) {
			throw new BadRequestException("Role must be ROLE_USER or ROLE_DRIVER");
		}
		User user = User.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.build();
		userRepository.save(user);
	}

	public AuthResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
		String token = jwtUtil.generateToken(userDetails);
		return new AuthResponse(token);
	}
}

