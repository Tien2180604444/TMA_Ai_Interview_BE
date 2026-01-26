package com.example.interview.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class QuestionResponse {
    private UUID id;
    private String question;
    private Integer questionOrder;
    private UUID questionTypeId;
    private String questionTypeName;
    private AnswerResponse answer;
}
