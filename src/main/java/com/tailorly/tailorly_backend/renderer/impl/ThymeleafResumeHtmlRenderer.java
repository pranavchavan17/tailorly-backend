package com.tailorly.tailorly_backend.renderer.impl;

import com.tailorly.tailorly_backend.dto.resume.ResumeData;
import com.tailorly.tailorly_backend.exception.PdfGenerationException;
import com.tailorly.tailorly_backend.renderer.ResumeHtmlRenderer;
import com.tailorly.tailorly_backend.util.ResumeContentComposer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ThymeleafResumeHtmlRenderer implements ResumeHtmlRenderer {

    private final TemplateEngine templateEngine;

    @Override
    public String render(ResumeData resume) {

        ResumeData safeResume = resume == null ? new ResumeData() : resume;

        Context context = new Context();
        context.setVariable("resume", safeResume);
        context.setVariable("contactLine", ResumeContentComposer.buildContactLine(safeResume.getContact()));
        //context.setVariable("resumeCss", loadResumeCss());

        String html = templateEngine.process("resume", context);

        System.out.println("=========== GENERATED HTML ===========");
        System.out.println(html);
        System.out.println("======================================");

        return html;
    }

    private String loadResumeCss() {
        ClassPathResource resource = new ClassPathResource("static/css/resume.css");

        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PdfGenerationException("Failed to load resume stylesheet", e);
        }
    }
}
