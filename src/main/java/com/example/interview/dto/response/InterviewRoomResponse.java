package com.example.interview.dto.response;

import com.example.interview.entity.interview.InterviewStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InterviewRoomResponse {
    private UUID id;
    private String name;
    private UUID userId;
    private InterviewStatus status;
    private UUID professionalPositionId;
    private String professionalPositionName;
    private UUID difficultLevelId;
    private String difficultLevelName;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Float averageScore;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<QuestionResponse> questions;
}
