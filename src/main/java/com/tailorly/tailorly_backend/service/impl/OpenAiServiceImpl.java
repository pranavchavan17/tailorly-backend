package com.tailorly.tailorly_backend.service.impl;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.tailorly.tailorly_backend.config.OpenAiModels;
import com.tailorly.tailorly_backend.dto.request.GenerateResumeRequest;
import com.tailorly.tailorly_backend.dto.resume.CertificationData;
import com.tailorly.tailorly_backend.dto.resume.ContactData;
import com.tailorly.tailorly_backend.dto.resume.EducationData;
import com.tailorly.tailorly_backend.dto.resume.ProjectData;
import com.tailorly.tailorly_backend.dto.resume.ResumeData;
import com.tailorly.tailorly_backend.exception.OpenAiException;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;
import com.tailorly.tailorly_backend.service.OpenAiService;
import com.tailorly.tailorly_backend.util.OpenAiPromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
                .text(ResumeData.class)
                .build();

        var response = openAIClient.responses()
                .create(params)
                .validate();

        ResumeData resume = response
                .output().stream()
                .flatMap(outputItem -> outputItem.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new OpenAiException("OpenAI returned no structured resume content"));

        resume = normalizeResume(resume);

        if (resume.getFullName() == null || resume.getFullName().isBlank()) {
            throw new OpenAiException("OpenAI returned an invalid resume");
        }

        return GenerateResumeResponse.builder()
                .resume(resume)
                .format("JSON")
                .build();
    }

    private ResumeData normalizeResume(ResumeData resume) {
        if (resume == null) {
            throw new OpenAiException("OpenAI returned an empty resume");
        }

        if (resume.getContact() == null) {
            resume.setContact(new ContactData());
        }

        if (resume.getSkills() == null) {
            resume.setSkills(new ArrayList<>());
        }

        if (resume.getProjects() == null) {
            resume.setProjects(new ArrayList<>());
        } else {
            List<ProjectData> projects = new ArrayList<>();
            for (ProjectData project : resume.getProjects()) {
                if (project != null) {
                    projects.add(project);
                }
            }
            resume.setProjects(projects);
        }

        if (resume.getEducation() == null) {
            resume.setEducation(new ArrayList<>());
        } else {
            List<EducationData> educationList = new ArrayList<>();
            for (EducationData education : resume.getEducation()) {
                if (education != null) {
                    educationList.add(education);
                }
            }
            resume.setEducation(educationList);
        }

        if (resume.getCertifications() == null) {
            resume.setCertifications(new ArrayList<>());
        } else {
            List<CertificationData> certifications = new ArrayList<>();
            for (CertificationData certification : resume.getCertifications()) {
                if (certification != null) {
                    certifications.add(certification);
                }
            }
            resume.setCertifications(certifications);
        }

        trim(resume.getContact());
        trimResumeFields(resume);

        return resume;
    }

    private void trimResumeFields(ResumeData resume) {
        resume.setFullName(trimToEmpty(resume.getFullName()));
        resume.setHeadline(trimToEmpty(resume.getHeadline()));
        resume.setSummary(trimToEmpty(resume.getSummary()));

        if (resume.getSkills() != null) {
            resume.getSkills().replaceAll(this::trimToEmpty);
            resume.getSkills().removeIf(String::isBlank);
        }

        if (resume.getProjects() != null) {
            for (ProjectData project : resume.getProjects()) {
                project.setTitle(trimToEmpty(project.getTitle()));
                project.setDescription(trimToEmpty(project.getDescription()));
            }
        }

        if (resume.getEducation() != null) {
            for (EducationData education : resume.getEducation()) {
                education.setInstitution(trimToEmpty(education.getInstitution()));
                education.setDegree(trimToEmpty(education.getDegree()));
                education.setFieldOfStudy(trimToEmpty(education.getFieldOfStudy()));
                education.setStartDate(trimToEmpty(education.getStartDate()));
                education.setEndDate(trimToEmpty(education.getEndDate()));
                education.setDetails(trimToEmpty(education.getDetails()));
            }
        }

        if (resume.getCertifications() != null) {
            for (CertificationData certification : resume.getCertifications()) {
                certification.setName(trimToEmpty(certification.getName()));
                certification.setIssuer(trimToEmpty(certification.getIssuer()));
                certification.setDate(trimToEmpty(certification.getDate()));
            }
        }
    }

    private void trim(ContactData contact) {
        if (contact == null) {
            return;
        }

        contact.setEmail(trimToEmpty(contact.getEmail()));
        contact.setPhone(trimToEmpty(contact.getPhone()));
        contact.setLocation(trimToEmpty(contact.getLocation()));
        contact.setLinkedin(trimToEmpty(contact.getLinkedin()));
        contact.setGithub(trimToEmpty(contact.getGithub()));
        contact.setWebsite(trimToEmpty(contact.getWebsite()));
    }

    private String trimToEmpty(String value) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        return trimmed;
    }
}
