package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.request.GenerateResumeRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;
import com.tailorly.tailorly_backend.dto.response.ResumeAnalysisResponse;
import com.tailorly.tailorly_backend.exception.ResumeParsingException;
import com.tailorly.tailorly_backend.service.AiService;
import com.tailorly.tailorly_backend.service.DocxGeneratorService;
import com.tailorly.tailorly_backend.service.OpenAiService;
import com.tailorly.tailorly_backend.service.PdfGeneratorService;
import com.tailorly.tailorly_backend.service.ResumeParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ResumeParserService resumeParserService;
    private final OpenAiService openAiService;
    private final PdfGeneratorService pdfGeneratorService;
    private final DocxGeneratorService docxGeneratorService;

    @Override
    public ApiResponse<String> analyzeResume(MultipartFile file) {
        return withTemporaryResumeFile(file, tempFile -> {
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

        return withTemporaryResumeFile(file, tempFile -> {
            GenerateResumeResponse response = buildTailoredResume(tempFile, jobDescription, customPrompt);

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

        return withTemporaryResumeFile(file, tempFile -> {
            GenerateResumeResponse response = buildTailoredResume(tempFile, jobDescription, customPrompt);
            return pdfGeneratorService.generatePdf(response.getResume());
        });
    }

    @Override
    public byte[] generateResumeDocx(
            MultipartFile file,
            String jobDescription,
            String customPrompt) {

        return withTemporaryResumeFile(file, tempFile -> {
            GenerateResumeResponse response = buildTailoredResume(tempFile, jobDescription, customPrompt);
            return docxGeneratorService.generateDocx(tempFile, response.getResume());
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

    private <T> T withTemporaryResumeFile(MultipartFile file, TemporaryFileCallback<T> callback) {
        File tempFile = null;

        try {
            tempFile = createTemporaryFile(file);
            return callback.apply(tempFile);
        } catch (IOException e) {
            throw new ResumeParsingException("Failed to process uploaded resume", e);
        } finally {
            deleteTemporaryFile(tempFile);
        }
    }

    private File createTemporaryFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResumeParsingException("Please select a file");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = extractSuffix(originalFilename);
        File tempFile = File.createTempFile("tailorly-resume-", suffix);
        file.transferTo(tempFile);
        return tempFile;
    }

    private String extractSuffix(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return ".tmp";
        }

        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex < 0) {
            return ".tmp";
        }

        String extension = originalFilename.substring(extensionIndex).toLowerCase();
        return extension.isBlank() ? ".tmp" : extension;
    }

    private void deleteTemporaryFile(File tempFile) {
        if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
            tempFile.deleteOnExit();
        }
    }

    @FunctionalInterface
    private interface TemporaryFileCallback<T> {
        T apply(File tempFile);
    }
}
