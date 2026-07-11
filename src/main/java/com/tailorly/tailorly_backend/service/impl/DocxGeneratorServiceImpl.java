package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.resume.CertificationData;
import com.tailorly.tailorly_backend.dto.resume.EducationData;
import com.tailorly.tailorly_backend.dto.resume.ProjectData;
import com.tailorly.tailorly_backend.dto.resume.ResumeData;
import com.tailorly.tailorly_backend.exception.DocxGenerationException;
import com.tailorly.tailorly_backend.service.DocxGeneratorService;
import org.apache.xmlbeans.XmlCursor;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DocxGeneratorServiceImpl implements DocxGeneratorService {

    @Override
    public byte[] generateDocx(File originalDocx, ResumeData resume) {
        if (originalDocx == null) {
            throw new DocxGenerationException("Original DOCX file is required");
        }

        String fileName = originalDocx.getName().toLowerCase(Locale.ROOT);
        if (!fileName.endsWith(".docx")) {
            throw new DocxGenerationException("DOCX generation is only supported for DOCX source files");
        }

        try (FileInputStream inputStream = new FileInputStream(originalDocx);
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Map<SectionType, SectionContent> sectionContentMap = buildSectionContent(resume);
            SectionState sectionState = new SectionState();

            replaceBodyElements(document.getBodyElements(), sectionState, sectionContentMap);

            XWPFParagraph referenceAnchor = findTopLevelReferenceParagraph(document);
            XWPFParagraph headingTemplate = findParagraphTemplate(document, true);
            XWPFParagraph bodyTemplate = findParagraphTemplate(document, false);

            appendMissingSections(document, sectionContentMap, referenceAnchor, headingTemplate, bodyTemplate);

            document.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new DocxGenerationException("Failed to generate DOCX", e);
        }
    }

    private Map<SectionType, SectionContent> buildSectionContent(ResumeData resume) {
        Map<SectionType, SectionContent> sections = new EnumMap<>(SectionType.class);

        sections.put(SectionType.SUMMARY, new SectionContent(
                SectionType.SUMMARY.displayName(),
                buildSummaryBlocks(resume)
        ));
        sections.put(SectionType.SKILLS, new SectionContent(
                SectionType.SKILLS.displayName(),
                buildSkillsBlocks(resume)
        ));
        sections.put(SectionType.PROJECTS, new SectionContent(
                SectionType.PROJECTS.displayName(),
                buildProjectBlocks(resume)
        ));
        sections.put(SectionType.EXPERIENCE, new SectionContent(
                SectionType.EXPERIENCE.displayName(),
                List.of()
        ));
        sections.put(SectionType.EDUCATION, new SectionContent(
                SectionType.EDUCATION.displayName(),
                buildEducationBlocks(resume)
        ));
        sections.put(SectionType.CERTIFICATIONS, new SectionContent(
                SectionType.CERTIFICATIONS.displayName(),
                buildCertificationBlocks(resume)
        ));

        return sections;
    }

    private List<String> buildSummaryBlocks(ResumeData resume) {
        List<String> blocks = new ArrayList<>();
        addText(blocks, resume == null ? null : resume.getSummary());
        return blocks;
    }

    private List<String> buildSkillsBlocks(ResumeData resume) {
        List<String> blocks = new ArrayList<>();
        if (resume == null || resume.getSkills() == null) {
            return blocks;
        }

        for (String skill : resume.getSkills()) {
            addText(blocks, skill);
        }

        return blocks;
    }

    private List<String> buildProjectBlocks(ResumeData resume) {
        List<String> blocks = new ArrayList<>();
        if (resume == null || resume.getProjects() == null) {
            return blocks;
        }

        for (ProjectData project : resume.getProjects()) {
            if (project == null) {
                continue;
            }

            addText(blocks, project.getTitle());
            addText(blocks, project.getDescription());
        }

        return blocks;
    }

    private List<String> buildEducationBlocks(ResumeData resume) {
        List<String> blocks = new ArrayList<>();
        if (resume == null || resume.getEducation() == null) {
            return blocks;
        }

        for (EducationData education : resume.getEducation()) {
            if (education == null) {
                continue;
            }

            StringBuilder line = new StringBuilder();
            appendPart(line, education.getInstitution());
            appendPart(line, education.getDegree());
            appendPart(line, education.getFieldOfStudy());
            appendPart(line, education.getStartDate());
            appendPart(line, education.getEndDate());
            appendPart(line, education.getDetails());
            addText(blocks, line.toString());
        }

        return blocks;
    }

    private List<String> buildCertificationBlocks(ResumeData resume) {
        List<String> blocks = new ArrayList<>();
        if (resume == null || resume.getCertifications() == null) {
            return blocks;
        }

        for (CertificationData certification : resume.getCertifications()) {
            if (certification == null) {
                continue;
            }

            StringBuilder line = new StringBuilder();
            appendPart(line, certification.getName());
            appendPart(line, certification.getIssuer());
            appendPart(line, certification.getDate());
            addText(blocks, line.toString());
        }

        return blocks;
    }

    private void replaceBodyElements(List<IBodyElement> elements,
                                     SectionState sectionState,
                                     Map<SectionType, SectionContent> sectionContentMap) {
        for (IBodyElement element : elements) {
            if (element instanceof XWPFParagraph paragraph) {
                replaceParagraph(paragraph, sectionState, sectionContentMap);
                continue;
            }

            if (element instanceof XWPFTable table) {
                replaceTable(table, sectionState, sectionContentMap);
            }
        }
    }

    private void replaceTable(XWPFTable table,
                              SectionState sectionState,
                              Map<SectionType, SectionContent> sectionContentMap) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                replaceBodyElements(cell.getBodyElements(), sectionState, sectionContentMap);
            }
        }
    }

    private void replaceParagraph(XWPFParagraph paragraph,
                                  SectionState sectionState,
                                  Map<SectionType, SectionContent> sectionContentMap) {
        SectionType headingType = detectHeadingType(paragraph);
        if (headingType != null) {
            sectionState.currentSection = headingType == SectionType.REFERENCES ? null : headingType;
            SectionContent sectionContent = sectionContentMap.get(headingType);
            if (sectionContent != null) {
                sectionContent.markFound();
            }
            return;
        }

        SectionContent sectionContent = sectionContentMap.get(sectionState.currentSection);
        if (sectionContent == null || !sectionContent.hasBlocks()) {
            return;
        }

        String replacement = sectionContent.nextBlock();
        if (replacement != null) {
            overwriteParagraphText(paragraph, replacement);
            return;
        }

        clearParagraphText(paragraph);
    }

    private void appendMissingSections(XWPFDocument document,
                                       Map<SectionType, SectionContent> sectionContentMap,
                                       XWPFParagraph referenceAnchor,
                                       XWPFParagraph headingTemplate,
                                       XWPFParagraph bodyTemplate) {
        List<SectionContent> missingSections = new ArrayList<>();
        for (SectionType sectionType : List.of(
                SectionType.SUMMARY,
                SectionType.SKILLS,
                SectionType.PROJECTS,
                SectionType.EXPERIENCE,
                SectionType.EDUCATION,
                SectionType.CERTIFICATIONS)) {
            SectionContent sectionContent = sectionContentMap.get(sectionType);
            if (sectionContent != null && sectionContent.hasBlocks() && !sectionContent.isFound()) {
                missingSections.add(sectionContent);
            }
        }

        if (missingSections.isEmpty()) {
            return;
        }

        if (referenceAnchor != null) {
            for (int i = missingSections.size() - 1; i >= 0; i--) {
                insertSectionBefore(document, referenceAnchor, missingSections.get(i), headingTemplate, bodyTemplate);
            }
            return;
        }

        for (SectionContent sectionContent : missingSections) {
            appendSectionAtEnd(document, sectionContent, headingTemplate, bodyTemplate);
        }
    }

    private void insertSectionBefore(XWPFDocument document,
                                     XWPFParagraph anchor,
                                     SectionContent sectionContent,
                                     XWPFParagraph headingTemplate,
                                     XWPFParagraph bodyTemplate) {
        List<ParagraphSpec> specs = buildSectionParagraphSpecs(sectionContent);
        XmlCursor cursor = anchor.getCTP().newCursor();
        try {
            for (int i = specs.size() - 1; i >= 0; i--) {
                ParagraphSpec spec = specs.get(i);
                XWPFParagraph inserted = document.insertNewParagraph(cursor);
                copyTemplateParagraph(spec.heading() ? headingTemplate : bodyTemplate, inserted);
                overwriteParagraphText(inserted, spec.text());
            }
        } finally {
            cursor.dispose();
        }
    }

    private void appendSectionAtEnd(XWPFDocument document,
                                    SectionContent sectionContent,
                                    XWPFParagraph headingTemplate,
                                    XWPFParagraph bodyTemplate) {
        for (ParagraphSpec spec : buildSectionParagraphSpecs(sectionContent)) {
            XWPFParagraph paragraph = document.createParagraph();
            copyTemplateParagraph(spec.heading() ? headingTemplate : bodyTemplate, paragraph);
            overwriteParagraphText(paragraph, spec.text());
        }
    }

    private List<ParagraphSpec> buildSectionParagraphSpecs(SectionContent sectionContent) {
        List<ParagraphSpec> specs = new ArrayList<>();
        specs.add(new ParagraphSpec(sectionContent.displayHeading(), true));
        for (String block : sectionContent.blocks) {
            specs.add(new ParagraphSpec(block, false));
        }
        return specs;
    }

    private XWPFParagraph findTopLevelReferenceParagraph(XWPFDocument document) {
        for (IBodyElement element : document.getBodyElements()) {
            if (element instanceof XWPFParagraph paragraph && isReferenceHeading(paragraph)) {
                return paragraph;
            }
        }

        return null;
    }

    private XWPFParagraph findParagraphTemplate(XWPFDocument document, boolean headingTemplate) {
        XWPFParagraph paragraph = findParagraphTemplate(document.getBodyElements(), headingTemplate);
        if (paragraph != null) {
            return paragraph;
        }

        return findParagraphTemplate(document.getBodyElements(), false);
    }

    private XWPFParagraph findParagraphTemplate(List<IBodyElement> elements, boolean headingTemplate) {
        for (IBodyElement element : elements) {
            if (element instanceof XWPFParagraph paragraph) {
                if (headingTemplate ? isLikelyHeading(paragraph) : !isLikelyHeading(paragraph) && hasText(paragraph.getText())) {
                    return paragraph;
                }
                continue;
            }

            if (element instanceof XWPFTable table) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        XWPFParagraph paragraph = findParagraphTemplate(cell.getBodyElements(), headingTemplate);
                        if (paragraph != null) {
                            return paragraph;
                        }
                    }
                }
            }
        }

        return null;
    }

    private SectionType detectHeadingType(XWPFParagraph paragraph) {
        if (paragraph == null) {
            return null;
        }

        String normalized = normalizeHeading(paragraph.getText());
        if (!isLikelyHeading(paragraph)) {
            return null;
        }

        if (isReferenceHeading(normalized)) {
            return SectionType.REFERENCES;
        }

        for (SectionType sectionType : List.of(
                SectionType.SUMMARY,
                SectionType.SKILLS,
                SectionType.PROJECTS,
                SectionType.EXPERIENCE,
                SectionType.EDUCATION,
                SectionType.CERTIFICATIONS)) {
            if (sectionType.matches(normalized)) {
                return sectionType;
            }
        }

        return null;
    }

    private boolean isReferenceHeading(XWPFParagraph paragraph) {
        return paragraph != null && isReferenceHeading(normalizeHeading(paragraph.getText()));
    }

    private boolean isReferenceHeading(String normalizedHeading) {
        return hasAlias(normalizedHeading, "REFERENCE")
                || hasAlias(normalizedHeading, "REFERENCES")
                || hasAlias(normalizedHeading, "REFEREES");
    }

    private boolean isLikelyHeading(XWPFParagraph paragraph) {
        String text = paragraph == null ? null : paragraph.getText();
        if (!hasText(text)) {
            return false;
        }

        String normalized = normalizeHeading(text);
        if (normalized.isEmpty()) {
            return false;
        }

        String[] words = normalized.split(" ");
        if (words.length > 6) {
            return false;
        }

        String styleId = paragraph.getStyle();
        if (styleId != null && styleId.toLowerCase(Locale.ROOT).contains("heading")) {
            return true;
        }

        return detectHeadingTypeByText(normalized) != null || isReferenceHeading(normalized);
    }

    private SectionType detectHeadingTypeByText(String normalizedHeading) {
        for (SectionType sectionType : List.of(
                SectionType.SUMMARY,
                SectionType.SKILLS,
                SectionType.PROJECTS,
                SectionType.EXPERIENCE,
                SectionType.EDUCATION,
                SectionType.CERTIFICATIONS)) {
            if (sectionType.matches(normalizedHeading)) {
                return sectionType;
            }
        }

        return null;
    }

    private void overwriteParagraphText(XWPFParagraph paragraph, String replacement) {
        if (paragraph == null) {
            return;
        }

        String text = replacement == null ? "" : replacement;
        if (paragraph.getRuns().isEmpty()) {
            paragraph.createRun().setText(text);
            return;
        }

        clearParagraphText(paragraph);
        paragraph.getRuns().get(0).setText(text, 0);
    }

    private void clearParagraphText(XWPFParagraph paragraph) {
        if (paragraph == null) {
            return;
        }

        if (paragraph.getRuns().isEmpty()) {
            return;
        }

        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            while (paragraph.getRuns().get(i).getCTR().sizeOfTArray() > 0) {
                paragraph.getRuns().get(i).getCTR().removeT(0);
            }
        }
    }

    private void copyTemplateParagraph(XWPFParagraph template, XWPFParagraph target) {
        if (template == null || target == null) {
            return;
        }

        target.getCTP().set((CTP) template.getCTP().copy());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void addText(List<String> values, String value) {
        if (hasText(value)) {
            values.add(value.trim());
        }
    }

    private void appendPart(StringBuilder builder, String value) {
        if (!hasText(value)) {
            return;
        }

        if (builder.length() > 0) {
            builder.append(" | ");
        }
        builder.append(value.trim());
    }

    private String normalizeHeading(String value) {
        if (value == null) {
            return "";
        }

        return value.toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    private boolean hasAlias(String normalizedHeading, String alias) {
        if (!hasText(normalizedHeading) || !hasText(alias)) {
            return false;
        }

        String normalizedAlias = alias.toUpperCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        String paddedHeading = " " + normalizedHeading + " ";
        String paddedAlias = " " + normalizedAlias + " ";
        return paddedHeading.contains(paddedAlias);
    }

    private static final class SectionState {
        private SectionType currentSection;
    }

    private enum SectionType {
        SUMMARY("Summary", new String[]{"SUMMARY", "PROFILE", "ABOUT"}),
        SKILLS("Skills", new String[]{"SKILLS", "TECHNICAL SKILLS"}),
        PROJECTS("Projects", new String[]{"PROJECTS"}),
        EXPERIENCE("Experience", new String[]{"EXPERIENCE"}),
        EDUCATION("Education", new String[]{"EDUCATION"}),
        CERTIFICATIONS("Certifications", new String[]{"CERTIFICATIONS", "CERTIFICATION"}),


        REFERENCES("References", new String[]{"REFERENCES", "REFERENCE", "REFEREES"});

        private final String displayName;
        private final String[] aliases;

        SectionType(String displayName, String[] aliases) {
            this.displayName = displayName;
            this.aliases = aliases;
        }

        boolean matches(String normalizedHeading) {
            for (String alias : aliases) {
                if (hasAliasStatic(normalizedHeading, alias)) {
                    return true;
                }
            }
            return false;
        }

        String displayName() {
            return displayName;
        }
    }

    private static boolean hasAliasStatic(String normalizedHeading, String alias) {
        if (normalizedHeading == null || normalizedHeading.isBlank() || alias == null || alias.isBlank()) {
            return false;
        }

        String normalizedAlias = alias.toUpperCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        String paddedHeading = " " + normalizedHeading + " ";
        String paddedAlias = " " + normalizedAlias + " ";
        return paddedHeading.contains(paddedAlias);
    }

    private static final class SectionContent {
        private final String displayHeading;
        private final List<String> blocks;
        private boolean found;
        private int index;

        private SectionContent(String displayHeading, List<String> blocks) {
            this.displayHeading = displayHeading;
            this.blocks = blocks == null ? List.of() : List.copyOf(blocks);
        }

        private boolean hasBlocks() {
            return !blocks.isEmpty();
        }

        private String nextBlock() {
            if (index >= blocks.size()) {
                return null;
            }

            return blocks.get(index++);
        }

        private void markFound() {
            this.found = true;
        }

        private boolean isFound() {
            return found;
        }

        private String displayHeading() {
            return displayHeading;
        }
    }

    private record ParagraphSpec(String text, boolean heading) {
    }
}
