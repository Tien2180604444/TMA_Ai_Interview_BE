package com.example.interview.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class SubmitAnswerRequest {
    private UUID questionId;
    private String answerText;
}
