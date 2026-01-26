package com.example.interview.entity.interview;

import com.example.interview.entity.base.BaseEnity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "interview_room_answer")
public class InterviewRoomAnswer extends BaseEnity {
    @Id
    @Column(name = "interview_room_answer_id", nullable = false, unique = true)
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_room_question_id")
    private InterviewRoomQuestion question;
    @Column(name = "answer", columnDefinition = "TEXT")
    private String answerText;
   @OneToOne(mappedBy = "answer", cascade = CascadeType.ALL)
    private InterviewRoomEvaluation evaluation;
}
