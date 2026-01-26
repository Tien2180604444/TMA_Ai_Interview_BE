package com.example.interview.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class AnswerResponse {
    private UUID id;
    private String answerText;
    private EvaluationResponse evaluation;
}
