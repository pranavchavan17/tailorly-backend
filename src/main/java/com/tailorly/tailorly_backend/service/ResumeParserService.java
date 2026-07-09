package com.tailorly.tailorly_backend.service;

import java.io.File;

public interface ResumeParserService {

    String extractText(File file);
}