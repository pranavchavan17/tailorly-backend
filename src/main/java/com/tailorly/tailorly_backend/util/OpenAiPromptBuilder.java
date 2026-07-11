package com.tailorly.tailorly_backend.util;

public class OpenAiPromptBuilder {

    private OpenAiPromptBuilder() {
    }

    public static String buildResumePrompt(
            String resumeText,
            String jobDescription,
            String customPrompt) {

        return """
                You are tailoring a real resume from the source resume below.

                Hard rules:
                - Never invent a name, company, title, project, degree, certification, date, or experience.
                - Never add claims that are not supported by the source resume.
                - Only improve wording, clarity, ATS keyword alignment, and relevance to the job description.
                - Only add new skills when the custom instructions explicitly request new skills.
                - If information is missing, return an empty string or an empty array.
                - Keep the candidate truthful.

                Return ONLY valid JSON that matches this exact structure:
                {
                  "fullName": "",
                  "headline": "",
                  "contact": {
                    "email": "",
                    "phone": "",
                    "location": "",
                    "linkedin": "",
                    "github": "",
                    "website": ""
                  },
                  "summary": "",
                  "skills": [],
                  "projects": [
                    {
                      "title": "",
                      "description": ""
                    }
                  ],
                  "education": [
                    {
                      "institution": "",
                      "degree": "",
                      "fieldOfStudy": "",
                      "startDate": "",
                      "endDate": "",
                      "details": ""
                    }
                  ],
                  "certifications": [
                    {
                      "name": "",
                      "issuer": "",
                      "date": ""
                    }
                  ]
                }

                Source resume:
                %s

                Job description:
                %s

                Custom instructions:
                %s

                Output rules:
                - Return JSON only.
                - No markdown.
                - No explanation.
                - No code fences.
                """.formatted(
                resumeText == null ? "" : resumeText,
                jobDescription == null ? "" : jobDescription,
                customPrompt == null ? "" : customPrompt
        );
    }
}
