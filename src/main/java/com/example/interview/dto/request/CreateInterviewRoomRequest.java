package com.example.interview.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateInterviewRoomRequest {
    private String name;
    private UUID professionalPositionId;
    private UUID difficultLevelId;
}
