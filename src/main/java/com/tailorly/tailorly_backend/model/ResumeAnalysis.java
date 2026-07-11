package com.tailorly.tailorly_backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resume_analysis")
public class ResumeAnalysis {

    @Id
    private String id;

    private String userId;

    private List<String> skills;

    private List<String> education;

    private List<String> experience;

    private List<String> projects;

    private List<String> certifications;

    private String summary;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}