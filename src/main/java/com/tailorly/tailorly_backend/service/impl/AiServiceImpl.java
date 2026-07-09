package com.tailorly.tailorly_backend.service.impl;


import com.tailorly.tailorly_backend.dto.request.GenerateResumeRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;
import com.tailorly.tailorly_backend.dto.response.ResumeAnalysisResponse;
import com.tailorly.tailorly_backend.service.AiService;
import com.tailorly.tailorly_backend.service.OpenAiService;
import com.tailorly.tailorly_backend.service.ResumeParserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ResumeParserService resumeParserService;
    private final OpenAiService openAiService;

    @Override
    public ApiResponse<String> analyzeResume(MultipartFile file) {

        try {

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            File tempFile = File.createTempFile("resume-", extension);

            file.transferTo(tempFile);

            String extractedText = resumeParserService.extractText(tempFile);

            tempFile.delete();

            return ApiResponse.<String>builder()
                    .success(true)
                    .message("Resume text extracted successfully")
                    .data(extractedText)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze resume", e);
        }
    }

    @Override
    public ApiResponse<ResumeAnalysisResponse> getResumeAnalysis() {
        return null;
    }

    @Override
    public ApiResponse<GenerateResumeResponse> generateResume(
            MultipartFile file,
            String jobDescription,
            String customPrompt) {

        try {

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            File tempFile = File.createTempFile("resume-", extension);

            file.transferTo(tempFile);

            String resumeText = resumeParserService.extractText(tempFile);

            tempFile.delete();

            GenerateResumeRequest request = GenerateResumeRequest.builder()
                    .resumeText(resumeText)
                    .jobDescription(jobDescription)
                    .customPrompt(customPrompt)
                    .build();

            GenerateResumeResponse response =
                    openAiService.generateResume(request);

            return ApiResponse.<GenerateResumeResponse>builder()
                    .success(true)
                    .message("Resume generated successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate resume", e);
        }
    }
}