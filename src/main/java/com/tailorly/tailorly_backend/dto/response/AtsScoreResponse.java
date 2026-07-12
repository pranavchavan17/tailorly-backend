package com.tailorly.tailorly_backend.dto.response;

import com.tailorly.tailorly_backend.model.AtsScoreResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsScoreResponse {

    private AtsScoreResult atsScoreResult;

    private String format;
}
