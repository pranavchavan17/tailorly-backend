package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.service.ResumeParserService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;


import java.io.File;
import java.io.FileInputStream;

@Service
@RequiredArgsConstructor
public class ResumeParserServiceImpl implements ResumeParserService {

    @Override
    public String extractText(File file) {

        try {

            String fileName = file.getName().toLowerCase();

            // PDF
            if (fileName.endsWith(".pdf")) {

                PDDocument document = Loader.loadPDF(file);

                PDFTextStripper stripper = new PDFTextStripper();

                String text = stripper.getText(document);

                document.close();

                return text;
            }

            // DOCX
            if (fileName.endsWith(".docx")) {

                XWPFDocument document = new XWPFDocument(new FileInputStream(file));

                XWPFWordExtractor extractor = new XWPFWordExtractor(document);

                String text = extractor.getText();

                extractor.close();
                document.close();

                return text;
            }

            throw new IllegalArgumentException("Unsupported file format");

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract resume text", e);
        }
    }
}