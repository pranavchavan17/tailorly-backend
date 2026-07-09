package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.request.GenerateResumeRequest;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;

public interface OpenAiService {

    GenerateResumeResponse generateResume(
            GenerateResumeRequest request
    );
}