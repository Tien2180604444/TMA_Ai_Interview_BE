package com.example.interview.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class GenerateQuestionRequest {
    private UUID questionTypeId;
}
