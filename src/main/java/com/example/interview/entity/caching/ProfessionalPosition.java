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
@Table(name = "professional_position")
public class ProfessionalPosition extends BaseEnity implements Serializable {
    @Id
    @Column(name = "professional_position_id", nullable = false, unique = true)
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "is_active")
    private boolean isActive = true;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_group_id")
    private JobGroup jobGroup;
}
