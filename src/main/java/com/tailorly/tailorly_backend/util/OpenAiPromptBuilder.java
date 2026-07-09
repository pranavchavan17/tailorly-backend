package com.tailorly.tailorly_backend.util;

public class OpenAiPromptBuilder {

    private OpenAiPromptBuilder() {
    }

    public static String buildResumePrompt(
            String resumeText,
            String jobDescription,
            String customPrompt) {

        return """
                You are an expert ATS Resume Writer.

                IMPORTANT RULES:
                1. Never invent fake experience.
                2. Never invent fake skills.
                3. Improve wording only.
                4. Improve ATS keywords.
                5. Keep professional formatting.
                6. Return ONLY the final resume.
                7. Do not add markdown.
                8. Do not explain your changes.

                Resume:
                %s

                Job Description:
                %s

                User Instructions:
                %s
                """.formatted(
                resumeText,
                jobDescription == null ? "" : jobDescription,
                customPrompt == null ? "" : customPrompt
        );
    }
}
