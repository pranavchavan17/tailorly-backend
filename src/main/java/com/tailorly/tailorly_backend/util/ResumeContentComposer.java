package com.tailorly.tailorly_backend.util;

import com.tailorly.tailorly_backend.dto.resume.CertificationData;
import com.tailorly.tailorly_backend.dto.resume.ContactData;
import com.tailorly.tailorly_backend.dto.resume.EducationData;
import com.tailorly.tailorly_backend.dto.resume.ProjectData;
import com.tailorly.tailorly_backend.dto.resume.ResumeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class ResumeContentComposer {

    private ResumeContentComposer() {
    }

    public static String buildContactLine(ContactData contact) {
        if (contact == null) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(" | ");

        addPart(joiner, contact.getEmail());
        addPart(joiner, contact.getPhone());
        addPart(joiner, contact.getLocation());
        addPart(joiner, contact.getLinkedin());
        addPart(joiner, contact.getGithub());
        addPart(joiner, contact.getWebsite());

        return joiner.toString();
    }

    public static List<String> buildDocxBlocks(ResumeData resume) {
        List<String> blocks = new ArrayList<>();

        addBlock(blocks, resume.getFullName());
        addBlock(blocks, resume.getHeadline());
        addBlock(blocks, buildContactLine(resume.getContact()));

        addSection(blocks, "Summary", resume.getSummary());
        addSection(blocks, "Skills", resume.getSkills());
        addProjectSection(blocks, resume.getProjects());
        addEducationSection(blocks, resume.getEducation());
        addCertificationSection(blocks, resume.getCertifications());

        return blocks;
    }

    private static void addSection(List<String> blocks, String sectionTitle, String value) {
        if (!hasText(value)) {
            return;
        }

        addBlock(blocks, sectionTitle);
        addBlock(blocks, value);
    }

    private static void addSection(List<String> blocks, String sectionTitle, List<String> values) {
        if (values == null || values.stream().noneMatch(ResumeContentComposer::hasText)) {
            return;
        }

        addBlock(blocks, sectionTitle);
        values.stream()
                .filter(ResumeContentComposer::hasText)
                .map(String::trim)
                .forEach(blocks::add);
    }

    private static void addProjectSection(List<String> blocks, List<ProjectData> projects) {
        if (projects == null || projects.stream().noneMatch(Objects::nonNull)) {
            return;
        }

        List<String> filtered = new ArrayList<>();
        for (ProjectData project : projects) {
            if (project == null) {
                continue;
            }

            if (hasText(project.getTitle())) {
                filtered.add(project.getTitle().trim());
            }

            if (hasText(project.getDescription())) {
                filtered.add(project.getDescription().trim());
            }
        }

        if (filtered.isEmpty()) {
            return;
        }

        addBlock(blocks, "Projects");
        blocks.addAll(filtered);
    }

    private static void addEducationSection(List<String> blocks, List<EducationData> educationList) {
        if (educationList == null || educationList.stream().noneMatch(Objects::nonNull)) {
            return;
        }

        List<String> filtered = new ArrayList<>();
        for (EducationData education : educationList) {
            if (education == null) {
                continue;
            }

            StringJoiner joiner = new StringJoiner(" | ");
            addPart(joiner, education.getInstitution());
            addPart(joiner, education.getDegree());
            addPart(joiner, education.getFieldOfStudy());
            addPart(joiner, education.getStartDate());
            addPart(joiner, education.getEndDate());
            addPart(joiner, education.getDetails());

            String line = joiner.toString();
            if (hasText(line)) {
                filtered.add(line.trim());
            }
        }

        if (filtered.isEmpty()) {
            return;
        }

        addBlock(blocks, "Education");
        blocks.addAll(filtered);
    }

    private static void addCertificationSection(List<String> blocks, List<CertificationData> certifications) {
        if (certifications == null || certifications.stream().noneMatch(Objects::nonNull)) {
            return;
        }

        List<String> filtered = new ArrayList<>();
        for (CertificationData certification : certifications) {
            if (certification == null) {
                continue;
            }

            StringJoiner joiner = new StringJoiner(" | ");
            addPart(joiner, certification.getName());
            addPart(joiner, certification.getIssuer());
            addPart(joiner, certification.getDate());

            String line = joiner.toString();
            if (hasText(line)) {
                filtered.add(line.trim());
            }
        }

        if (filtered.isEmpty()) {
            return;
        }

        addBlock(blocks, "Certifications");
        blocks.addAll(filtered);
    }

    private static void addBlock(List<String> blocks, String value) {
        if (hasText(value)) {
            blocks.add(value.trim());
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static void addPart(StringJoiner joiner, String value) {
        if (hasText(value)) {
            joiner.add(value.trim());
        }
    }
}
