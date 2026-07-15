package com.tailorly.tailorly_backend.repository;

import com.tailorly.tailorly_backend.model.Subscription;
import com.tailorly.tailorly_backend.model.enums.SubscriptionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    Optional<Subscription> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Subscription> findTopByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Subscription> findTopByUserIdAndStatusOrderByCreatedAtDesc(
            String userId,
            SubscriptionStatus status);
}
