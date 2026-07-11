package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.resume.ResumeData;

import java.io.File;

public interface DocxGeneratorService {

    byte[] generateDocx(File originalDocx, ResumeData resume);
}
