package com.example.RideShare.repository;

import java.util.List;
import com.example.RideShare.model.Ride;
import com.example.RideShare.model.RideStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RideRepository extends MongoRepository<Ride, String> {
	List<Ride> findByStatus(RideStatus status);

	List<Ride> findByUserId(String userId);
}

