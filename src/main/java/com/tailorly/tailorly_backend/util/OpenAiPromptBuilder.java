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
                
                IMPORTANT RULES
                
                1. Never invent fake experience.
                2. Never invent fake skills.
                3. Improve wording only.
                4. Keep all information truthful.
                5. Keep the resume ATS friendly.
                6. Preserve the user's resume structure.
                7. If a Job Description is provided, tailor the resume for that JD.
                8. If Custom Instructions are provided, follow them.
                9. Return ONLY Markdown.
                10. Do NOT wrap the response inside ```markdown.
                
                Return using this structure.
                
                # Professional Summary
                
                ...
                
                # Technical Skills
                
                - Java
                - Spring Boot
                
                # Projects
                
                ## Project Name
                
                Description
                
                ## Project Name
                
                Description
                
                # Education
                
                ...
                
                # Certifications
                
                ...
                
                Resume
                
                %s
                
                Job Description
                
                %s
                
                Custom Instructions
            
                %s
                """.formatted(
                                resumeText,
                                jobDescription == null ? "" : jobDescription,
                                customPrompt == null ? "" : customPrompt
                        );
    }
}
