package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.service.HtmlResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class HtmlResumeServiceImpl implements HtmlResumeService {

    private final TemplateEngine templateEngine;

    @Override
    public String generateHtml(String resumeContent) {

        Context context = new Context();

        context.setVariable("resume", resumeContent);

        return templateEngine.process("resume", context);
    }
}