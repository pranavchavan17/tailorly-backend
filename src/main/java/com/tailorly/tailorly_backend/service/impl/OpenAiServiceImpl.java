package com.tailorly.tailorly_backend.service.impl;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.tailorly.tailorly_backend.config.OpenAiModels;
import com.tailorly.tailorly_backend.dto.ai.AiResumeResult;
import com.tailorly.tailorly_backend.dto.request.GenerateResumeRequest;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;
import com.tailorly.tailorly_backend.service.OpenAiService;
import com.tailorly.tailorly_backend.util.OpenAiPromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAIClient openAIClient;

    @Override
    public GenerateResumeResponse generateResume(GenerateResumeRequest request) {
        String prompt = OpenAiPromptBuilder.buildResumePrompt(
                request.getResumeText(),
                request.getJobDescription(),
                request.getCustomPrompt()
        );

        var params = ResponseCreateParams.builder()
                .model(ChatModel.of(OpenAiModels.MODEL))
                .input(prompt)
                .text(AiResumeResult.class)
                .build();

        AiResumeResult aiResult = openAIClient.responses()
                .create(params)
                .output().stream()
                .flatMap(outputItem -> outputItem.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("OpenAI returned no structured resume content"));

        if (aiResult.generatedResume == null || aiResult.generatedResume.isBlank()) {
            throw new IllegalStateException("OpenAI returned an empty generated resume");
        }

        return GenerateResumeResponse.builder()
                .generatedResume(aiResult.generatedResume)
                .format("PDF")
                .build();
    }
}