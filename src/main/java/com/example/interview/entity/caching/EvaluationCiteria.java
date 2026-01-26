package com.example.interview.entity.caching;

import com.example.interview.entity.base.BaseEnity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "evaluation_criteria")
public class EvaluationCiteria extends BaseEnity implements Serializable {
    @Id
    @Column(name = "evaluation_criteria_id", nullable = false, unique = true)
    private UUID id;
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;
    @Column(name = "min_score")
    private int minScore;
    @Column(name = "max_score")
    private int maxScore;
    @Column(name = "is_active")
    private boolean isActive = true;
}
