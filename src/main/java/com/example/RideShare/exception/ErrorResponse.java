package com.example.RideShare.exception;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
	private String error;
	private String message;
	private Instant timestamp;
}

