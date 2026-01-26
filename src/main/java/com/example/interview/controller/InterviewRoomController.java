package com.example.interview.controller;

import com.example.interview.dto.request.*;
import com.example.interview.dto.response.EvaluationResponse;
import com.example.interview.dto.response.InterviewRoomResponse;
import com.example.interview.dto.response.QuestionResponse;
import com.example.interview.entity.caching.User;
import com.example.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/interview-rooms")
@RequiredArgsConstructor
public class InterviewRoomController {
    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewRoomResponse> createRoom(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody CreateInterviewRoomRequest request) {
        User user = interviewService.getCurrentUser(oidcUser);
        InterviewRoomResponse response = interviewService.createRoom(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<InterviewRoomResponse>> getAllRooms(
            @AuthenticationPrincipal OidcUser oidcUser,
            Pageable pageable) {
        User user = interviewService.getCurrentUser(oidcUser);
        Page<InterviewRoomResponse> rooms = interviewService.getAllRoomsByUser(user.getId(), pageable);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewRoomResponse> getRoomById(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable UUID id) {
        User user = interviewService.getCurrentUser(oidcUser);
        InterviewRoomResponse response = interviewService.getRoomById(id, user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterviewRoomResponse> updateRoom(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable UUID id,
            @RequestBody UpdateInterviewRoomRequest request) {
        User user = interviewService.getCurrentUser(oidcUser);
        InterviewRoomResponse response = interviewService.updateRoom(id, user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable UUID id) {
        User user = interviewService.getCurrentUser(oidcUser);
        interviewService.deleteRoom(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/generate-question")
    public ResponseEntity<QuestionResponse> generateQuestion(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable UUID roomId,
            @RequestBody GenerateQuestionRequest request) {
        User user = interviewService.getCurrentUser(oidcUser);
        QuestionResponse response = interviewService.generateQuestion(roomId, user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId}/submit-answer")
    public ResponseEntity<EvaluationResponse> submitAnswer(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable UUID roomId,
            @RequestBody SubmitAnswerRequest request) {
        User user = interviewService.getCurrentUser(oidcUser);
        EvaluationResponse response = interviewService.submitAnswer(roomId, user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId}/start")
    public ResponseEntity<InterviewRoomResponse> startInterview(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable UUID roomId) {
        User user = interviewService.getCurrentUser(oidcUser);
        InterviewRoomResponse response = interviewService.startInterview(roomId, user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId}/complete")
    public ResponseEntity<InterviewRoomResponse> completeInterview(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable UUID roomId) {
        User user = interviewService.getCurrentUser(oidcUser);
        InterviewRoomResponse response = interviewService.completeInterview(roomId, user.getId());
        return ResponseEntity.ok(response);
    }
}
