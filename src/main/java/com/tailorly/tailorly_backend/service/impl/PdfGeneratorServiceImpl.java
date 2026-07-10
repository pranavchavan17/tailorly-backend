package com.tailorly.tailorly_backend.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.tailorly.tailorly_backend.service.PdfGeneratorService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Override
    public byte[] generatePdf(String generatedResume) {

        try {

            Document document = new Document(PageSize.A4, 40, 40, 40, 40);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("Tailorly AI Resume", titleFont));
            document.add(Chunk.NEWLINE);

            String[] lines = generatedResume.split("\\r?\\n");

            for (String line : lines) {

                line = line.trim();

                if (line.isBlank()) {
                    document.add(Chunk.NEWLINE);
                    continue;
                }

                if (isHeading(line)) {

                    Paragraph heading = new Paragraph(line, headingFont);
                    heading.setSpacingBefore(10);
                    heading.setSpacingAfter(5);

                    document.add(heading);
                }

                else if (line.startsWith("- ")) {

                    Paragraph bullet =
                            new Paragraph("• " + line.substring(2), bodyFont);

                    bullet.setIndentationLeft(20);

                    document.add(bullet);
                }

                else {

                    Paragraph body =
                            new Paragraph(line, bodyFont);

                    body.setSpacingAfter(3);

                    document.add(body);
                }
            }

            document.close();

            return outputStream.toByteArray();

        }

        catch (Exception e) {

            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private boolean isHeading(String line) {

        String value = line.toLowerCase();

        return value.contains("summary")
                || value.contains("skill")
                || value.contains("project")
                || value.contains("education")
                || value.contains("experience")
                || value.contains("certification")
                || value.contains("achievement")
                || value.contains("profile");
    }
}