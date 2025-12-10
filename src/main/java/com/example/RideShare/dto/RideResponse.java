package com.example.RideShare.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RideResponse {
	private String id;
	private String userId;
	private String driverId;
	private String pickupLocation;
	private String dropLocation;
	private String status;
	private Instant createdAt;
}

