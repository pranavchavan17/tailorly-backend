package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.exception.ResumeParsingException;
import com.tailorly.tailorly_backend.service.ResumeParserService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ResumeParserServiceImpl implements ResumeParserService {

    @Override
    public String extractText(File file) {

        if (file == null) {
            throw new ResumeParsingException("Resume file is required");
        }

        String fileName = file.getName().toLowerCase();

        try {

            if (fileName.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(file))  {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                }
            }

            if (fileName.endsWith(".docx")) {
                try (FileInputStream inputStream = new FileInputStream(file);
                     XWPFDocument document = new XWPFDocument(inputStream);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

                    return extractor.getText();
                }
            }

            throw new ResumeParsingException("Unsupported file format. Only PDF and DOCX files are supported.");

        } catch (IOException e) {
            throw new ResumeParsingException("Failed to extract resume text", e);
        }
    }
}
