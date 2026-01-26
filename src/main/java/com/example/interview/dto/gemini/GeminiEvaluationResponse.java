package com.example.interview.dto.gemini;

import lombok.Data;
import java.util.Map;

@Data
public class GeminiEvaluationResponse {
    private Map<String, Double> scores; // Map of criteria name -> score
    private Double totalScore; // 0-100
    private String feedback; // Detailed feedback text
}
