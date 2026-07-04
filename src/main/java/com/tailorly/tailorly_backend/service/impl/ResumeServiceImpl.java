package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.ResumeResponse;
import com.tailorly.tailorly_backend.model.User;
import com.tailorly.tailorly_backend.repository.UserRepository;
import com.tailorly.tailorly_backend.service.CloudinaryService;
import com.tailorly.tailorly_backend.service.CurrentUserService;
import com.tailorly.tailorly_backend.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final CurrentUserService currentUserService;

    @Override
    public ApiResponse<ResumeResponse> uploadResume(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file");
        }

        String contentType = file.getContentType();

        if (!"application/pdf".equals(contentType)
                && !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            throw new IllegalArgumentException("Only PDF and DOCX files are allowed");
        }

        long maxSize = 5 * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size cannot exceed 5 MB");
        }

        User user = currentUserService.getCurrentUser();

        try {

            if (user.getResumePublicId() != null
                    && !user.getResumePublicId().isBlank()) {

                cloudinaryService.deleteFile(user.getResumePublicId());
            }

            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);

            String resumeUrl = uploadResult.get("secure_url").toString();
            String resumePublicId = uploadResult.get("public_id").toString();

            user.setResumeUrl(resumeUrl);
            user.setResumePublicId(resumePublicId);

            User updatedUser = userRepository.save(user);

            ResumeResponse response = ResumeResponse.builder()
                    .resumeUrl(updatedUser.getResumeUrl())
                    .uploaded(true)
                    .build();

            return ApiResponse.<ResumeResponse>builder()
                    .success(true)
                    .message("Resume uploaded successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload resume", e);
        }
    }

    @Override
    public ApiResponse<ResumeResponse> getResume() {

        User user = currentUserService.getCurrentUser();

        ResumeResponse response = ResumeResponse.builder()
                .resumeUrl(user.getResumeUrl())
                .uploaded(user.getResumeUrl() != null && !user.getResumeUrl().isBlank())
                .build();

        return ApiResponse.<ResumeResponse>builder()
                .success(true)
                .message("Resume fetched successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<Void> deleteResume() {

        User user = currentUserService.getCurrentUser();

        try {

            if (user.getResumePublicId() != null
                    && !user.getResumePublicId().isBlank()) {

                cloudinaryService.deleteFile(user.getResumePublicId());
            }

            user.setResumeUrl(null);
            user.setResumePublicId(null);

            userRepository.save(user);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("Resume deleted successfully")
                    .data(null)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete resume", e);
        }
    }
}