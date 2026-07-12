package com.tailorly.tailorly_backend.controller;

import com.tailorly.tailorly_backend.dto.request.AtsScoreRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.AtsScoreResponse;
import com.tailorly.tailorly_backend.service.AtsScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ats")
@RequiredArgsConstructor
public class AtsController {

    private final AtsScoreService atsScoreService;

    @PostMapping("/score")
    public ApiResponse<AtsScoreResponse> scoreResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription) {

        AtsScoreRequest request = AtsScoreRequest.builder()
                .resumeFile(file)
                .jobDescription(jobDescription)
                .build();

        return atsScoreService.scoreResume(request);
    }
}
