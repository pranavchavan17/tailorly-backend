package com.tailorly.tailorly_backend.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiPromptBuilderTest {

    @Test
    void buildResumePrompt_shouldRequestDirectResumePayload() {
        String prompt = OpenAiPromptBuilder.buildResumePrompt(
                "source resume text",
                "job description",
                "custom prompt"
        );

        assertThat(prompt).contains("\"fullName\": \"\"");
        assertThat(prompt).contains("\"contact\": {");
        assertThat(prompt).contains("\"skills\": []");
        assertThat(prompt).doesNotContain("\"resume\": {");
    }
}
