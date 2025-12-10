package com.example.RideShare.controller;

import com.example.RideShare.dto.CreateRideRequest;
import com.example.RideShare.dto.RideResponse;
import com.example.RideShare.service.RideService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RideController {

	private final RideService rideService;

	@PostMapping("/rides")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<RideResponse> requestRide(@Valid @RequestBody CreateRideRequest request) {
		return ResponseEntity.ok(rideService.requestRide(request, currentUsername()));
	}

	@GetMapping("/user/rides")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<List<RideResponse>> myRides() {
		return ResponseEntity.ok(rideService.getUserRides(currentUsername()));
	}

	@GetMapping("/driver/rides/requests")
	@PreAuthorize("hasAuthority('ROLE_DRIVER')")
	public ResponseEntity<List<RideResponse>> pendingRides() {
		return ResponseEntity.ok(rideService.getPendingRequests());
	}

	@PostMapping("/driver/rides/{rideId}/accept")
	@PreAuthorize("hasAuthority('ROLE_DRIVER')")
	public ResponseEntity<RideResponse> acceptRide(@PathVariable String rideId) {
		return ResponseEntity.ok(rideService.acceptRide(rideId, currentUsername()));
	}

	@PostMapping("/rides/{rideId}/complete")
	@PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_DRIVER')")
	public ResponseEntity<RideResponse> completeRide(@PathVariable String rideId) {
		return ResponseEntity.ok(rideService.completeRide(rideId, currentUsername()));
	}

	private String currentUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null ? auth.getName() : null;
	}
}

