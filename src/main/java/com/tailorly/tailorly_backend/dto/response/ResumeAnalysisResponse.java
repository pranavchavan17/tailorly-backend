package com.tailorly.tailorly_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResumeAnalysisResponse {

    private List<String> skills;

    private List<String> education;

    private List<String> experience;

    private List<String> projects;

    private List<String> certifications;

    private String summary;
}