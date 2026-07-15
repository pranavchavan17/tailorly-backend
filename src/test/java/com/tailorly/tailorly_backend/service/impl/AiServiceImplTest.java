package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.GenerateResumeResponse;
import com.tailorly.tailorly_backend.dto.resume.CertificationData;
import com.tailorly.tailorly_backend.dto.resume.ContactData;
import com.tailorly.tailorly_backend.dto.resume.EducationData;
import com.tailorly.tailorly_backend.dto.resume.ProjectData;
import com.tailorly.tailorly_backend.dto.resume.ResumeData;
import com.tailorly.tailorly_backend.service.DocxGeneratorService;
import com.tailorly.tailorly_backend.service.OpenAiService;
import com.tailorly.tailorly_backend.service.PdfGeneratorService;
import com.tailorly.tailorly_backend.service.ResumeParserService;
import com.tailorly.tailorly_backend.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceImplTest {

    @Mock
    private ResumeParserService resumeParserService;

    @Mock
    private OpenAiService openAiService;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @Mock
    private DocxGeneratorService docxGeneratorService;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private AiServiceImpl aiService;

    @Test
    void generateResumePdf_shouldUseTailoredResumeAndReturnPdfBytes() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "source resume content".getBytes(StandardCharsets.UTF_8)
        );

        ResumeData resume = sampleResume();

        when(resumeParserService.extractText(any(File.class))).thenReturn("extracted resume text");
        when(openAiService.generateResume(any()))
                .thenReturn(GenerateResumeResponse.builder()
                        .resume(resume)
                        .format("JSON")
                        .build());
        when(pdfGeneratorService.generatePdf(any())).thenReturn(new byte[]{1, 2, 3});

        org.mockito.Mockito.doNothing().when(subscriptionService).enforceResumeTailoringAccess();
        org.mockito.Mockito.doNothing().when(subscriptionService).recordSuccessfulResumeTailoring();

        byte[] result = aiService.generateResumePdf(file, "job description", "custom prompt");

        assertThat(result).containsExactly(1, 2, 3);
        verify(resumeParserService).extractText(any(File.class));
        verify(openAiService).generateResume(any());
        verify(pdfGeneratorService).generatePdf(any());
    }

    @Test
    void generateResume_shouldReturnApiResponseWithTailoredResume() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "source resume content".getBytes(StandardCharsets.UTF_8)
        );

        ResumeData resume = sampleResume();

        when(resumeParserService.extractText(any(File.class))).thenReturn("extracted resume text");
        when(openAiService.generateResume(any()))
                .thenReturn(GenerateResumeResponse.builder()
                        .resume(resume)
                        .format("JSON")
                        .build());

        org.mockito.Mockito.doNothing().when(subscriptionService).enforceResumeTailoringAccess();
        org.mockito.Mockito.doNothing().when(subscriptionService).recordSuccessfulResumeTailoring();

        ApiResponse<GenerateResumeResponse> response =
                aiService.generateResume(file, "job description", "custom prompt");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Resume generated successfully");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getFormat()).isEqualTo("JSON");
        assertThat(response.getData().getResume().getFullName()).isEqualTo("Jane Doe");
        verify(resumeParserService).extractText(any(File.class));
        verify(openAiService).generateResume(any());
    }

    private ResumeData sampleResume() {
        ResumeData resume = new ResumeData();
        resume.setFullName("Jane Doe");
        resume.setHeadline("Software Engineer");

        ContactData contact = new ContactData();
        contact.setEmail("jane@example.com");
        contact.setPhone("1234567890");
        contact.setLocation("Bengaluru, India");
        contact.setLinkedin("https://linkedin.com/in/janedoe");
        contact.setGithub("https://github.com/janedoe");
        contact.setWebsite("https://janedoe.dev");
        resume.setContact(contact);

        resume.setSummary("Experienced engineer building reliable backend systems.");
        resume.setSkills(List.of("Java", "Spring Boot", "MongoDB"));

        ProjectData project = new ProjectData();
        project.setTitle("Resume Tailoring Platform");
        project.setDescription("Built an AI resume tailoring workflow.");
        resume.setProjects(List.of(project));

        EducationData education = new EducationData();
        education.setInstitution("Example University");
        education.setDegree("B.Tech");
        education.setFieldOfStudy("Computer Science");
        education.setStartDate("2018");
        education.setEndDate("2022");
        education.setDetails("Graduated with distinction.");
        resume.setEducation(List.of(education));

        CertificationData certification = new CertificationData();
        certification.setName("AWS Certified Developer");
        certification.setIssuer("Amazon");
        certification.setDate("2024");
        resume.setCertifications(List.of(certification));

        return resume;
    }
}
