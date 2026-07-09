package com.tailorly.tailorly_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateResumeRequest {

    @NotBlank(message = "Resume is required")
    private String resumeText;

    private String jobDescription;

    private String customPrompt;
}