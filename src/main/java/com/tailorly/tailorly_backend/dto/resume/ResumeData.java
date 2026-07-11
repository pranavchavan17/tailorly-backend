package com.tailorly.tailorly_backend.dto.resume;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class ResumeData {

    private String fullName;

    private String headline;

    private ContactData contact;

    private String summary;

    private List<String> skills;

    private List<ProjectData> projects;

    private List<EducationData> education;

    private List<CertificationData> certifications;
}
