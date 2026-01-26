package com.example.interview.entity.caching;

import com.example.interview.entity.base.BaseEnity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name ="difficult_level")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DifficultLevel extends BaseEnity implements Serializable {
    @Id
    @Column(name = "difficult_level_id",nullable = false, unique = true)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "min_score")
    private int minScore;
    @Column(name = "max_score")
    private int maxScore;
    @Column(name = "is_active")
    private boolean isActive = true;
}
