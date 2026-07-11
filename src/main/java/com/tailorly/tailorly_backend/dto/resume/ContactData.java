package com.tailorly.tailorly_backend.dto.resume;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data

public class ContactData {


    private String email;


    private String phone;


    private String location;


    private String linkedin;


    private String github;


    private String website;
}
