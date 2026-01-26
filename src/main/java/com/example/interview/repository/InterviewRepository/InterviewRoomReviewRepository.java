package com.example.interview.repository.InterviewRepository;

import com.example.interview.entity.interview.InterviewRoomReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InterviewRoomReviewRepository extends JpaRepository<InterviewRoomReview, UUID>{
    void deleteByInterviewRoomId(UUID interviewRoomId);
}
