package com.tailorly.tailorly_backend.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.tailorly.tailorly_backend.dto.resume.ResumeData;
import com.tailorly.tailorly_backend.exception.PdfGenerationException;
import com.tailorly.tailorly_backend.renderer.ResumeHtmlRenderer;
import com.tailorly.tailorly_backend.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private final ResumeHtmlRenderer resumeHtmlRenderer;

    @Override
    public byte[] generatePdf(ResumeData resume) {

        String html = resumeHtmlRenderer.render(resume);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new PdfGenerationException("Failed to generate PDF", e);
        }
    }
}
