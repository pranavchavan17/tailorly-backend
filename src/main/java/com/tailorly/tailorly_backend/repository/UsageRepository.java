package com.tailorly.tailorly_backend.repository;

import com.tailorly.tailorly_backend.model.Usage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsageRepository extends MongoRepository<Usage, String> {

    Optional<Usage> findByUserId(String userId);
}
