package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.resume.ResumeData;

public interface PdfGeneratorService {

    byte[] generatePdf(ResumeData resume);
}