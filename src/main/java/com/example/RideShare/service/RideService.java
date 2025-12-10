package com.example.RideShare.service;

import com.example.RideShare.dto.CreateRideRequest;
import com.example.RideShare.dto.RideResponse;
import com.example.RideShare.exception.BadRequestException;
import com.example.RideShare.exception.NotFoundException;
import com.example.RideShare.model.Ride;
import com.example.RideShare.model.RideStatus;
import com.example.RideShare.model.User;
import com.example.RideShare.repository.RideRepository;
import com.example.RideShare.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideService {

	private final RideRepository rideRepository;
	private final UserRepository userRepository;

	public RideResponse requestRide(CreateRideRequest request, String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User not found"));
		if (!"ROLE_USER".equals(user.getRole())) {
			throw new BadRequestException("Only ROLE_USER can request rides");
		}
		Ride ride = Ride.builder()
				.userId(user.getId())
				.pickupLocation(request.getPickupLocation())
				.dropLocation(request.getDropLocation())
				.status(RideStatus.REQUESTED)
				.createdAt(Instant.now())
				.build();
		return toResponse(rideRepository.save(ride));
	}

	public List<RideResponse> getPendingRequests() {
		return rideRepository.findByStatus(RideStatus.REQUESTED)
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public RideResponse acceptRide(String rideId, String driverUsername) {
		User driver = userRepository.findByUsername(driverUsername)
				.orElseThrow(() -> new NotFoundException("Driver not found"));
		if (!"ROLE_DRIVER".equals(driver.getRole())) {
			throw new BadRequestException("Only ROLE_DRIVER can accept rides");
		}
		Ride ride = rideRepository.findById(rideId)
				.orElseThrow(() -> new NotFoundException("Ride not found"));
		if (ride.getStatus() != RideStatus.REQUESTED) {
			throw new BadRequestException("Ride is not in REQUESTED state");
		}
		ride.setDriverId(driver.getId());
		ride.setStatus(RideStatus.ACCEPTED);
		return toResponse(rideRepository.save(ride));
	}

	@Transactional
	public RideResponse completeRide(String rideId, String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User not found"));
		Ride ride = rideRepository.findById(rideId)
				.orElseThrow(() -> new NotFoundException("Ride not found"));
		if (ride.getStatus() != RideStatus.ACCEPTED) {
			throw new BadRequestException("Ride is not in ACCEPTED state");
		}
		boolean allowed = usernameMatches(user, ride.getUserId()) || usernameMatches(user, ride.getDriverId());
		if (!allowed) {
			throw new BadRequestException("Only assigned driver or passenger can complete ride");
		}
		ride.setStatus(RideStatus.COMPLETED);
		return toResponse(rideRepository.save(ride));
	}

	public List<RideResponse> getUserRides(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User not found"));
		return rideRepository.findByUserId(user.getId())
				.stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	private boolean usernameMatches(User user, String id) {
		return id != null && id.equals(user.getId());
	}

	private RideResponse toResponse(Ride ride) {
		return RideResponse.builder()
				.id(ride.getId())
				.userId(ride.getUserId())
				.driverId(ride.getDriverId())
				.pickupLocation(ride.getPickupLocation())
				.dropLocation(ride.getDropLocation())
				.status(ride.getStatus().name())
				.createdAt(ride.getCreatedAt())
				.build();
	}
}

