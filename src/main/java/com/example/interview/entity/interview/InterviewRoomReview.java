package com.example.interview.entity.interview;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.processing.SQL;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "interview_room_review")
public class InterviewRoomReview {
    @Id
    @Column(name = "interview_room_review_id", nullable = false, unique = true)
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "interview_room_id")
    private InterviewRoom interviewRoom;
    @Column(name = "total_score")
    private double total;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "review_json", columnDefinition = "JSONB")
    private String review;
}
