package com.tailorly.tailorly_backend.controller;

import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.ResumeResponse;
import com.tailorly.tailorly_backend.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ApiResponse<ResumeResponse> uploadResume(
            @RequestParam("file") MultipartFile file) {

        return resumeService.uploadResume(file);
    }

    @GetMapping
    public ApiResponse<ResumeResponse> getResume() {
        return resumeService.getResume();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteResume() {
        return resumeService.deleteResume();
    }
}