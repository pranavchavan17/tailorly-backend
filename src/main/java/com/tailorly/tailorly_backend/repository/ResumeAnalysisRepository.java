package com.tailorly.tailorly_backend.repository;

import com.tailorly.tailorly_backend.model.ResumeAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ResumeAnalysisRepository
        extends MongoRepository<ResumeAnalysis, String> {

    Optional<ResumeAnalysis> findByUserId(String userId);
}
