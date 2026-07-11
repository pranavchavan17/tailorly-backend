package com.tailorly.tailorly_backend.dto.response;
import com.tailorly.tailorly_backend.dto.resume.ResumeData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateResumeResponse {

    private ResumeData resume;

    private String format;
}