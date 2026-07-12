package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.request.AtsScoreRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.AtsScoreResponse;

public interface AtsScoreService {

    ApiResponse<AtsScoreResponse> scoreResume(AtsScoreRequest request);
}
