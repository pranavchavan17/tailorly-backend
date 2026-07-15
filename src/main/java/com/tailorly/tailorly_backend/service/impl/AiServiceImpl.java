package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.request.GenerateResumeRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;
import com.tailorly.tailorly_backend.dto.response.ResumeAnalysisResponse;
import com.tailorly.tailorly_backend.service.AiService;
import com.tailorly.tailorly_backend.service.DocxGeneratorService;
import com.tailorly.tailorly_backend.service.OpenAiService;
import com.tailorly.tailorly_backend.service.PdfGeneratorService;
import com.tailorly.tailorly_backend.service.ResumeParserService;
import com.tailorly.tailorly_backend.service.SubscriptionService;
import com.tailorly.tailorly_backend.util.MultipartFileTempFileExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ResumeParserService resumeParserService;
    private final OpenAiService openAiService;
    private final PdfGeneratorService pdfGeneratorService;
    private final DocxGeneratorService docxGeneratorService;
    private final SubscriptionService subscriptionService;

    @Override
    public ApiResponse<String> analyzeResume(MultipartFile file) {
        return MultipartFileTempFileExecutor.withTemporaryFile(file, "tailorly-resume-", tempFile -> {
            String extractedText = resumeParserService.extractText(tempFile);

            return ApiResponse.<String>builder()
                    .success(true)
                    .message("Resume text extracted successfully")
                    .data(extractedText)
                    .build();
        });
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

        subscriptionService.enforceResumeTailoringAccess();

        return MultipartFileTempFileExecutor.withTemporaryFile(file, "tailorly-resume-", tempFile -> {
            GenerateResumeResponse response = buildTailoredResume(tempFile, jobDescription, customPrompt);
            subscriptionService.recordSuccessfulResumeTailoring();

            return ApiResponse.<GenerateResumeResponse>builder()
                    .success(true)
                    .message("Resume generated successfully")
                    .data(response)
                    .build();
        });
    }

    @Override
    public byte[] generateResumePdf(
            MultipartFile file,
            String jobDescription,
            String customPrompt) {

        subscriptionService.enforceResumeTailoringAccess();

        return MultipartFileTempFileExecutor.withTemporaryFile(file, "tailorly-resume-", tempFile -> {
            GenerateResumeResponse response = buildTailoredResume(tempFile, jobDescription, customPrompt);
            byte[] pdf = pdfGeneratorService.generatePdf(response.getResume());
            subscriptionService.recordSuccessfulResumeTailoring();
            return pdf;
        });
    }

    @Override
    public byte[] generateResumeDocx(
            MultipartFile file,
            String jobDescription,
            String customPrompt) {

        subscriptionService.enforceResumeTailoringAccess();

        return MultipartFileTempFileExecutor.withTemporaryFile(file, "tailorly-resume-", tempFile -> {
            GenerateResumeResponse response = buildTailoredResume(tempFile, jobDescription, customPrompt);
            byte[] docx = docxGeneratorService.generateDocx(tempFile, response.getResume());
            subscriptionService.recordSuccessfulResumeTailoring();
            return docx;
        });
    }

    private GenerateResumeResponse buildTailoredResume(
            File tempFile,
            String jobDescription,
            String customPrompt) {

        String resumeText = resumeParserService.extractText(tempFile);

        GenerateResumeRequest request = GenerateResumeRequest.builder()
                .resumeText(resumeText)
                .jobDescription(jobDescription)
                .customPrompt(customPrompt)
                .build();

        return openAiService.generateResume(request);
    }

}
