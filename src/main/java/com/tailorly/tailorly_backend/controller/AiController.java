package com.tailorly.tailorly_backend.controller;

import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;
import com.tailorly.tailorly_backend.dto.response.ResumeAnalysisResponse;
import com.tailorly.tailorly_backend.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/analyze-resume")
    public ApiResponse<String> analyzeResume(
            @RequestParam("file") MultipartFile file) {

        return aiService.analyzeResume(file);
    }

    @GetMapping("/resume-analysis")
    public ApiResponse<ResumeAnalysisResponse> getResumeAnalysis() {
        return aiService.getResumeAnalysis();
    }

    @PostMapping("/generate-resume")
    public ApiResponse<GenerateResumeResponse> generateResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription,
            @RequestParam(value = "customPrompt", required = false) String customPrompt) {

        return aiService.generateResume(
                file,
                jobDescription,
                customPrompt
        );
    }
    @PostMapping("/generate-resume/pdf")
    public ResponseEntity<byte[]> generateResumePdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription,
            @RequestParam(value = "customPrompt", required = false) String customPrompt) {

        byte[] pdf = aiService.generateResumePdf(
                file,
                jobDescription,
                customPrompt
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=tailorly_resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}