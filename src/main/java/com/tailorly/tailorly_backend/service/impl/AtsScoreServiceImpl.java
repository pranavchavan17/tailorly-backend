package com.tailorly.tailorly_backend.service.impl;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.tailorly.tailorly_backend.config.OpenAiModels;
import com.tailorly.tailorly_backend.dto.request.AtsScoreRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.AtsScoreResponse;
import com.tailorly.tailorly_backend.exception.OpenAiException;
import com.tailorly.tailorly_backend.model.AtsScoreResult;
import com.tailorly.tailorly_backend.service.AtsScoreService;
import com.tailorly.tailorly_backend.service.ResumeParserService;
import com.tailorly.tailorly_backend.util.MultipartFileTempFileExecutor;
import com.tailorly.tailorly_backend.util.OpenAiPromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AtsScoreServiceImpl implements AtsScoreService {

    private final ResumeParserService resumeParserService;
    private final OpenAIClient openAIClient;

    @Override
    public ApiResponse<AtsScoreResponse> scoreResume(AtsScoreRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("ATS score request is required");
        }

        return MultipartFileTempFileExecutor.withTemporaryFile(
                request.getResumeFile(),
                "tailorly-ats-resume-",
                tempFile -> {
                    String resumeText = resumeParserService.extractText(tempFile);
                    AtsScoreResult result = buildAtsScoreResult(resumeText, request.getJobDescription());

                    return ApiResponse.<AtsScoreResponse>builder()
                            .success(true)
                            .message("ATS score generated successfully")
                            .data(AtsScoreResponse.builder()
                                    .atsScoreResult(result)
                                    .format("JSON")
                                    .build())
                            .build();
                });
    }

    private AtsScoreResult buildAtsScoreResult(String resumeText, String jobDescription) {
        var params = ResponseCreateParams.builder()
                .model(ChatModel.of(OpenAiModels.MODEL))
                .input(OpenAiPromptBuilder.buildAtsScorePrompt(resumeText, jobDescription))
                .text(AtsScoreResult.class)
                .build();

        var response = openAIClient.responses()
                .create(params)
                .validate();

        AtsScoreResult result = response
                .output().stream()
                .flatMap(outputItem -> outputItem.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new OpenAiException("OpenAI returned no structured ATS score content"));

        return normalizeAtsScoreResult(result);
    }

    private AtsScoreResult normalizeAtsScoreResult(AtsScoreResult result) {
        if (result == null) {
            throw new OpenAiException("OpenAI returned an empty ATS score");
        }

        validateScore("overallScore", result.getOverallScore());
        validateScore("keywordScore", result.getKeywordScore());
        validateScore("formatScore", result.getFormatScore());
        validateScore("summaryScore", result.getSummaryScore());
        validateScore("skillsScore", result.getSkillsScore());
        validateScore("experienceScore", result.getExperienceScore());
        validateScore("educationScore", result.getEducationScore());
        validateScore("grammarScore", result.getGrammarScore());
        validateScore("atsScore", result.getAtsScore());

        result.setMissingKeywords(normalizeList(result.getMissingKeywords()));
        result.setRecommendations(normalizeList(result.getRecommendations()));

        return result;
    }

    private void validateScore(String fieldName, Integer score) {
        if (score == null) {
            throw new OpenAiException("OpenAI returned an invalid ATS score: missing " + fieldName);
        }

        if (score < 0 || score > 100) {
            throw new OpenAiException("OpenAI returned an invalid ATS score: " + fieldName + " must be between 0 and 100");
        }
    }

    private List<String> normalizeList(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }

        List<String> normalized = new ArrayList<>();
        for (String value : values) {
            if (value != null) {
                String trimmed = value.trim();
                if (!trimmed.isBlank()) {
                    normalized.add(trimmed);
                }
            }
        }

        return normalized;
    }
}
