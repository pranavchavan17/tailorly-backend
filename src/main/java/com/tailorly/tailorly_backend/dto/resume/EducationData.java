package com.tailorly.tailorly_backend.dto.resume;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data

public class EducationData {

    private String institution;

    private String degree;

    private String fieldOfStudy;

    private String startDate;

    private String endDate;

    private String details;
}
