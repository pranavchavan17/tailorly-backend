package com.tailorly.tailorly_backend.util;

import com.tailorly.tailorly_backend.exception.ResumeParsingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public final class MultipartFileTempFileExecutor {

    private MultipartFileTempFileExecutor() {
    }

    public static <T> T withTemporaryFile(
            MultipartFile file,
            String filePrefix,
            TemporaryFileCallback<T> callback) {

        File tempFile = null;

        try {
            tempFile = createTemporaryFile(file, filePrefix);
            return callback.apply(tempFile);
        } catch (IOException e) {
            throw new ResumeParsingException("Failed to process uploaded resume", e);
        } finally {
            deleteTemporaryFile(tempFile);
        }
    }

    private static File createTemporaryFile(MultipartFile file, String filePrefix) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResumeParsingException("Please select a file");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = extractSuffix(originalFilename);
        File tempFile = File.createTempFile(filePrefix, suffix);
        file.transferTo(tempFile);
        return tempFile;
    }

    private static String extractSuffix(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return ".tmp";
        }

        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex < 0) {
            return ".tmp";
        }

        String extension = originalFilename.substring(extensionIndex).toLowerCase();
        return extension.isBlank() ? ".tmp" : extension;
    }

    private static void deleteTemporaryFile(File tempFile) {
        if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
            tempFile.deleteOnExit();
        }
    }

    @FunctionalInterface
    public interface TemporaryFileCallback<T> {
        T apply(File tempFile);
    }
}
