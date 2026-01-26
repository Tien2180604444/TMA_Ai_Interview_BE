package com.example.interview.entity.interview;

import com.example.interview.entity.base.BaseEnity;
import com.example.interview.entity.caching.DifficultLevel;
import com.example.interview.entity.caching.ProfessionalPosition;
import com.example.interview.entity.caching.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "interview_room")
public class InterviewRoom extends BaseEnity {
    @Id
    @Column(name = "interview_room_id", nullable = false, unique = true)
    private UUID id;
    @Column(name = "interview_room_name", nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status")
    private InterviewStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_position_id")
    private ProfessionalPosition professionalPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "difficult_level_id")
    private DifficultLevel difficultLevel;

    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Float averageScore;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "interviewRoom", cascade = CascadeType.ALL)
    private List<InterviewRoomQuestion> questions;


}
