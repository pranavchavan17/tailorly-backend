package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.resume.ResumeData;
import com.tailorly.tailorly_backend.renderer.ResumeHtmlRenderer;
import com.tailorly.tailorly_backend.service.HtmlResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HtmlResumeServiceImpl implements HtmlResumeService {

    private final ResumeHtmlRenderer resumeHtmlRenderer;

    @Override
    public String generateHtml(ResumeData resume) {
        return resumeHtmlRenderer.render(resume);
    }
}
