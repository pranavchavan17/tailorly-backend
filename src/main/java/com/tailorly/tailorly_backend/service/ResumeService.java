package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.ResumeResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    ApiResponse<ResumeResponse> uploadResume(MultipartFile file);

    ApiResponse<ResumeResponse> getResume();

    ApiResponse<Void> deleteResume();
}