package com.example.interview.dto.gemini;

import lombok.Data;

@Data
public class GeminiQuestionResponse {
    private String question;
    private String modelVersion;
}
