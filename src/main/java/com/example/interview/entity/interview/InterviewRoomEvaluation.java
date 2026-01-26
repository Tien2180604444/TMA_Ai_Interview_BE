package com.example.interview.entity.interview;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "interview_room_evaluation")
public class InterviewRoomEvaluation {
    @Id
    @Column(name = "interview_room_evaluation_id", nullable = false, unique = true)
    private UUID id;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scores",columnDefinition = "JSONB")
    private String score;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_room_answer_id")
    private InterviewRoomAnswer answer;
    @Column(name = "total_score")
    private double total;
    @Column(name = "ai_feedback",columnDefinition = "TEXT")
    private String aiFeedback;
}
