package com.example.RideShare.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class Ride {
	@Id
	private String id;

	private String userId;

	private String driverId;

	private String pickupLocation;

	private String dropLocation;

	private RideStatus status;

	private Instant createdAt;
}

