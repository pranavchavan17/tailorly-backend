package com.tailorly.tailorly_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumeResponse {

    private String resumeUrl;
    private boolean uploaded;
}