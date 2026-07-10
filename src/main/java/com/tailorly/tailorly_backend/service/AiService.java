package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;
import com.tailorly.tailorly_backend.dto.response.ResumeAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AiService {

    ApiResponse<String> analyzeResume(MultipartFile file);

    ApiResponse<ResumeAnalysisResponse> getResumeAnalysis();

    ApiResponse<GenerateResumeResponse> generateResume(
            MultipartFile file,
            String jobDescription,
            String customPrompt);

    byte[] generateResumePdf(
            MultipartFile file,
            String jobDescription,
            String customPrompt);
}
