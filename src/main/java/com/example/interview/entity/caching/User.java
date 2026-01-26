//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.interview.entity.caching;

import com.example.interview.entity.base.BaseEnity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "users"
)
@Getter
@Setter

public class User extends BaseEnity implements Serializable {
    @Id
    @Column(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private UUID id;
    @Column(name = "cognito_sub", nullable = false, unique = true)
    private String cognitoId;
    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email;
    @Column(
            name = "full_name",
            nullable = false
    )
    private String fullName;
    @Column(
            name = "is_active"
    )
    private boolean isActive = true;
}
