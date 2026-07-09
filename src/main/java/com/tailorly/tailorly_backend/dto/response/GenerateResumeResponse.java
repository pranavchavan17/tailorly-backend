package com.tailorly.tailorly_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateResumeResponse {

    private String generatedResume;

    private String format;
}