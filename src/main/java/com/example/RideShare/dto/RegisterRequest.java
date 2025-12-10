package com.example.RideShare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	@NotBlank(message = "Username is required")
	@Size(min = 3, message = "Username must be at least 3 characters")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 4, message = "Password must be at least 4 characters")
	private String password;

	@NotBlank(message = "Role is required")
	@Pattern(regexp = "ROLE_USER|ROLE_DRIVER", message = "Role must be ROLE_USER or ROLE_DRIVER")
	private String role;
}

