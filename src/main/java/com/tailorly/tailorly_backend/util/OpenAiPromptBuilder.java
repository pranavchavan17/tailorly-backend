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

    public static String buildAtsScorePrompt(
            String resumeText,
            String jobDescription) {

        return """
                You are an ATS resume scoring engine.

                Hard rules:
                - Evaluate only the uploaded resume text.
                - If a job description is provided, score the resume against that role and its ATS expectations.
                - If no job description is provided, score against general ATS best practices for the likely role suggested by the resume.
                - Do not invent experience, skills, education, certifications, or keywords.
                - Use only the source resume and the optional job description.
                - Return integer scores from 0 to 100.
                - Return concise missing keywords and concise recommendations only.

                Score these dimensions:
                - Overall Score
                - Keyword Match
                - Formatting
                - Professional Summary
                - Skills
                - Projects
                - Experience
                - Education
                - Grammar
                - ATS Friendliness

                Return ONLY valid JSON that matches this exact structure:
                {
                  "overallScore": 0,
                  "keywordScore": 0,
                  "formatScore": 0,
                  "summaryScore": 0,
                  "skillsScore": 0,
                  "experienceScore": 0,
                  "educationScore": 0,
                  "grammarScore": 0,
                  "atsScore": 0,
                  "missingKeywords": [],
                  "recommendations": []
                }

                Resume text:
                %s

                Job description:
                %s

                Output rules:
                - Return JSON only.
                - No markdown.
                - No explanation.
                - No code fences.
                """.formatted(
                resumeText == null ? "" : resumeText,
                jobDescription == null ? "" : jobDescription
        );
    }
}
