package com.example.interview.repository.InterviewRepository;

import com.example.interview.entity.interview.InterviewRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InterviewRoomRepository extends JpaRepository<InterviewRoom, UUID>, QuerydslPredicateExecutor<InterviewRoom> {
}
