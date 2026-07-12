package com.tailorly.tailorly_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AtsScoreResult {

    private Integer overallScore;

    private Integer keywordScore;

    private Integer formatScore;

    private Integer summaryScore;

    private Integer skillsScore;

    private Integer experienceScore;

    private Integer educationScore;

    private Integer grammarScore;

    private Integer atsScore;

    @Default
    private List<String> missingKeywords = new ArrayList<>();

    @Default
    private List<String> recommendations = new ArrayList<>();
}
