package com.example.interview.repository.CacheRepository;

import com.example.interview.entity.caching.DifficultLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DifficultLevelRepository extends JpaRepository<DifficultLevel, UUID> {
}
