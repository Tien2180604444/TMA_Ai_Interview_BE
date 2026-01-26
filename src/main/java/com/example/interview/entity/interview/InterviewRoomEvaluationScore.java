package com.example.interview.entity.interview;

import com.example.interview.entity.base.BaseEnity;
import com.example.interview.entity.caching.EvaluationCiteria;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "interview_room_evaluation_score")
@Getter
@Setter
@RequiredArgsConstructor
public class InterviewRoomEvaluationScore extends BaseEnity {
    @Id
    @GeneratedValue
    @Column(name = "interview_room_evaluation_score_id", nullable = false, unique = true)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_room_evaluation_id")
    private InterviewRoomEvaluation evaluation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_criteria_id")
    private EvaluationCiteria evaluationCiteria;
    @Column(name = "score",columnDefinition = "jsonb")
    private double score;
}
