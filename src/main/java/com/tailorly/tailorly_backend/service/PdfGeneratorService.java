package com.tailorly.tailorly_backend.service;

public interface PdfGeneratorService {

    byte[] generatePdf(String generatedResume);
}