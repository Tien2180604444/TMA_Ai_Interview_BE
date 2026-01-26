package com.example.interview.entity.interview;

import com.example.interview.entity.base.BaseEnity;
import com.example.interview.entity.caching.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "interview_room_question")
public class InterviewRoomQuestion extends BaseEnity {
    @Id
    @Column(name = "interview_room_question_id", nullable = false, unique = true)
    private UUID id;
    private String question;
    @Column(name = "question_order")
    private int questionOrder;
    @Column(name = "ai_model_version")
    private String aiModelVersion;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "interview_room_id")
    private InterviewRoom interviewRoom;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_type_id")
    private QuestionType questionType;
    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private InterviewRoomAnswer answer;
}
