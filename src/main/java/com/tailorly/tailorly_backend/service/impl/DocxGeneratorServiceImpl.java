package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.resume.ResumeData;
import com.tailorly.tailorly_backend.exception.DocxGenerationException;
import com.tailorly.tailorly_backend.service.DocxGeneratorService;
import com.tailorly.tailorly_backend.util.ResumeContentComposer;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DocxGeneratorServiceImpl implements DocxGeneratorService {

    @Override
    public byte[] generateDocx(File originalDocx, ResumeData resume) {

        if (originalDocx == null) {
            throw new DocxGenerationException("Original DOCX file is required");
        }

        String fileName = originalDocx.getName().toLowerCase();
        if (!fileName.endsWith(".docx")) {
            throw new DocxGenerationException("DOCX generation is only supported for DOCX source files");
        }

        try (FileInputStream inputStream = new FileInputStream(originalDocx);
             XWPFDocument document = new XWPFDocument(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            List<String> blocks = ResumeContentComposer.buildDocxBlocks(resume);
            AtomicInteger index = new AtomicInteger(0);

            replaceBodyElements(document.getBodyElements(), blocks, index);
            appendRemainingBlocks(document, blocks, index);

            document.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new DocxGenerationException("Failed to generate DOCX", e);
        }
    }

    private void replaceBodyElements(List<IBodyElement> elements, List<String> blocks, AtomicInteger index) {
        for (IBodyElement element : elements) {
            if (element instanceof XWPFParagraph paragraph) {
                replaceParagraph(paragraph, blocks, index);
            } else if (element instanceof XWPFTable table) {
                replaceTable(table, blocks, index);
            }
        }
    }

    private void replaceTable(XWPFTable table, List<String> blocks, AtomicInteger index) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                replaceBodyElements(cell.getBodyElements(), blocks, index);
            }
        }
    }

    private void replaceParagraph(XWPFParagraph paragraph, List<String> blocks, AtomicInteger index) {
        if (paragraph == null || !hasText(paragraph.getText())) {
            return;
        }

        int current = index.getAndIncrement();
        String replacement = current < blocks.size() ? blocks.get(current) : "";
        overwriteParagraphText(paragraph, replacement);
    }

    private void overwriteParagraphText(XWPFParagraph paragraph, String replacement) {
        if (paragraph.getRuns().isEmpty()) {
            paragraph.createRun().setText(replacement);
            return;
        }

        XWPFRun firstRun = paragraph.getRuns().get(0);
        firstRun.setText(replacement, 0);

        for (int i = 1; i < paragraph.getRuns().size(); i++) {
            paragraph.getRuns().get(i).setText("", 0);
        }
    }

    private void appendRemainingBlocks(XWPFDocument document, List<String> blocks, AtomicInteger index) {
        while (index.get() < blocks.size()) {
            String block = blocks.get(index.getAndIncrement());
            if (!hasText(block)) {
                continue;
            }

            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText(block);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
