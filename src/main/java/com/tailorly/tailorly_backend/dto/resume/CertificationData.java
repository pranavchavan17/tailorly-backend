package com.tailorly.tailorly_backend.dto.resume;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data

public class CertificationData {


    private String name;

    private String issuer;

    private String date;
}
