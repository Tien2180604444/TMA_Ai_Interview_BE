package com.example.interview.repository.InterviewRepository;

import com.example.interview.entity.interview.InterviewRoom;
import com.example.interview.entity.interview.InterviewRoomQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InterviewRoomQuestionRepository extends JpaRepository<InterviewRoomQuestion, UUID>{

    void deleteByInterviewRoom(InterviewRoom interviewRoom);

    void deleteByInterviewRoomId(UUID interviewRoomId);
}
