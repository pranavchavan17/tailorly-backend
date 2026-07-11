package com.tailorly.tailorly_backend.renderer;

import com.tailorly.tailorly_backend.dto.resume.ResumeData;

public interface ResumeHtmlRenderer {

    String render(ResumeData resume);
}
