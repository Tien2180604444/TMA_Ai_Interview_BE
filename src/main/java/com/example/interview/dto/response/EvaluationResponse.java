package com.example.interview.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class EvaluationResponse {
    private UUID id;
    private String score; // JSON string
    private Double totalScore;
    private String aiFeedback;
}
