package com.tailorly.tailorly_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "usage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usage {

    @Id
    private String id;

    private String userId;

    @Builder.Default
    private Integer resumeTailoringCount = 0;

    @LastModifiedDate
    private LocalDateTime lastUpdated;
}
