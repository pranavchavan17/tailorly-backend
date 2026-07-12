package com.tailorly.tailorly_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsScoreRequest {

    @NotNull(message = "Resume file is required")
    private MultipartFile resumeFile;

    private String jobDescription;
}
