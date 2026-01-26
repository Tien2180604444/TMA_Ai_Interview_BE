package com.example.interview.service;

import com.example.interview.dto.gemini.GeminiEvaluationResponse;
import com.example.interview.dto.gemini.GeminiQuestionResponse;
import com.example.interview.dto.request.*;
import com.example.interview.dto.response.*;
import com.example.interview.entity.caching.*;
import com.example.interview.entity.interview.*;
import com.example.interview.repository.CacheRepository.*;
import com.example.interview.repository.InterviewRepository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {
    private final InterviewRoomRepository interviewRoomRepository;
    private final InterviewRoomQuestionRepository questionRepository;
    private final InterviewRoomAnswerRepository answerRepository;
    private final InterviewRoomEvaluationRepository evaluationRepository;
    private final InterviewRoomReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProfessionalPositionRepository professionalPositionRepository;
    private final DifficultLevelRepository difficultLevelRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final EvaluationCriteriaService evaluationCriteriaService;
    private final GeminiClientService geminiClientService;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public User getCurrentUser(OidcUser oidcUser) {
        return userRepository.findByCognitoId(oidcUser.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public InterviewRoomResponse createRoom(UUID userId, CreateInterviewRoomRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ProfessionalPosition position = professionalPositionRepository.findById(request.getProfessionalPositionId())
                .orElseThrow(() -> new RuntimeException("Professional position not found"));
        DifficultLevel level = difficultLevelRepository.findById(request.getDifficultLevelId())
                .orElseThrow(() -> new RuntimeException("Difficult level not found"));

        InterviewRoom room = new InterviewRoom();
        room.setId(UUID.randomUUID());
        room.setName(request.getName());
        room.setUser(user);
        room.setProfessionalPosition(position);
        room.setDifficultLevel(level);
        room.setStatus(InterviewStatus.pending);
        room.setTotalQuestions(0);
        room.setAnsweredQuestions(0);
        room.setAverageScore(0.0f);
        room.setDeleted(false);

        room = interviewRoomRepository.save(room);
        return mapToResponse(room);
    }

    public Page<InterviewRoomResponse> getAllRoomsByUser(UUID userId, Pageable pageable) {
        List<InterviewRoom> allRooms = interviewRoomRepository.findAll();
        
        List<InterviewRoom> filteredRooms = allRooms.stream()
                .filter(room -> room.getUser().getId().equals(userId) && !room.isDeleted())
                .sorted((r1, r2) -> {
                    if (r1.getCreatedAt() != null && r2.getCreatedAt() != null) {
                        return r2.getCreatedAt().compareTo(r1.getCreatedAt());
                    }
                    return 0;
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredRooms.size());
        List<InterviewRoom> pagedRooms = start < filteredRooms.size() 
                ? filteredRooms.subList(start, end) 
                : List.of();

        List<InterviewRoomResponse> responses = pagedRooms.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, filteredRooms.size());
    }

    public InterviewRoomResponse getRoomById(UUID roomId, UUID userId) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Interview room not found"));

        if (!room.getUser().getId().equals(userId) || room.isDeleted()) {
            throw new RuntimeException("Unauthorized or room not found");
        }

        return mapToResponse(room);
    }

    @Transactional
    public InterviewRoomResponse updateRoom(UUID roomId, UUID userId, UpdateInterviewRoomRequest request) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Interview room not found"));

        if (!room.getUser().getId().equals(userId) || room.isDeleted()) {
            throw new RuntimeException("Unauthorized or room not found");
        }

        if (request.getName() != null) {
            room.setName(request.getName());
        }
        if (request.getProfessionalPositionId() != null) {
            ProfessionalPosition position = professionalPositionRepository.findById(request.getProfessionalPositionId())
                    .orElseThrow(() -> new RuntimeException("Professional position not found"));
            room.setProfessionalPosition(position);
        }
        if (request.getDifficultLevelId() != null) {
            DifficultLevel level = difficultLevelRepository.findById(request.getDifficultLevelId())
                    .orElseThrow(() -> new RuntimeException("Difficult level not found"));
            room.setDifficultLevel(level);
        }

        room = interviewRoomRepository.save(room);
        return mapToResponse(room);
    }

    @Transactional
    public void deleteRoom(UUID roomId, UUID userId) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Interview room not found"));

        if (!room.getUser().getId().equals(userId) || room.isDeleted()) {
            throw new RuntimeException("Unauthorized or room not found");
        }

        room.setDeleted(true);
        interviewRoomRepository.save(room);
        if(room.isDeleted()) {
            questionRepository.deleteByInterviewRoomId(roomId);
            reviewRepository.deleteByInterviewRoomId(roomId);
            interviewRoomRepository.deleteById(roomId);
        }
    }

    @Transactional
    public QuestionResponse generateQuestion(UUID roomId, UUID userId, GenerateQuestionRequest request) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Interview room not found"));

        if (!room.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        QuestionType questionType = questionTypeRepository.findById(request.getQuestionTypeId())
                .orElseThrow(() -> new RuntimeException("Question type not found"));

        // Generate question using Gemini
        String jobGroup = room.getProfessionalPosition().getJobGroup().getName();
        String position = room.getProfessionalPosition().getName();
        String difficulty = room.getDifficultLevel().getName();
        String qType = questionType.getName();

        GeminiQuestionResponse geminiResponse = geminiClientService.generateQuestion(
                jobGroup, position, difficulty, qType
        );

        // Create question entity
        InterviewRoomQuestion question = new InterviewRoomQuestion();
        question.setId(UUID.randomUUID());
        question.setQuestion(geminiResponse.getQuestion());
        question.setInterviewRoom(room);
        question.setQuestionType(questionType);
        question.setQuestionOrder(room.getTotalQuestions() + 1);
        question.setAiModelVersion(geminiResponse.getModelVersion());
        question.setDeleted(false);

        question = questionRepository.save(question);

        // Update room
        room.setTotalQuestions(room.getTotalQuestions() + 1);
        if (room.getStatus() == InterviewStatus.pending) {
            room.setStatus(InterviewStatus.in_progress);
        }
        interviewRoomRepository.save(room);

        return mapToQuestionResponse(question);
    }

    @Transactional
    public EvaluationResponse submitAnswer(UUID roomId, UUID userId, SubmitAnswerRequest request) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Interview room not found"));

        if (!room.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        InterviewRoomQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getInterviewRoom().getId().equals(roomId)) {
            throw new RuntimeException("Question does not belong to this room");
        }

        // Create answer
        InterviewRoomAnswer answer = new InterviewRoomAnswer();
        answer.setId(UUID.randomUUID());
        answer.setQuestion(question);
        answer.setAnswerText(request.getAnswerText());
        answer.setDeleted(false);
        answer = answerRepository.save(answer);

        // Get evaluation criteria
        List<EvaluationCiteria> criteriaList = evaluationCriteriaService.getAllEvaluationCriterias();
        List<String> criteriaNames = criteriaList.stream()
                .map(EvaluationCiteria::getName)
                .collect(Collectors.toList());

        // Evaluate answer using Gemini
        String jobGroup = room.getProfessionalPosition().getJobGroup().getName();
        String position = room.getProfessionalPosition().getName();
        String difficulty = room.getDifficultLevel().getName();

        GeminiEvaluationResponse geminiEvaluation = geminiClientService.evaluateAnswer(
                question.getQuestion(),
                request.getAnswerText(),
                jobGroup,
                position,
                difficulty,
                criteriaNames
        );

        // Convert scores to JSON string
        String scoresJson;
        try {
            scoresJson = objectMapper.writeValueAsString(geminiEvaluation.getScores());
        } catch (Exception e) {
            log.error("Error converting scores to JSON: {}", e.getMessage());
            scoresJson = "{}";
        }

        // Create evaluation
        InterviewRoomEvaluation evaluation = new InterviewRoomEvaluation();
        evaluation.setId(UUID.randomUUID());
        evaluation.setAnswer(answer);
        evaluation.setScore(scoresJson);
        evaluation.setTotal(geminiEvaluation.getTotalScore());
        evaluation.setAiFeedback(geminiEvaluation.getFeedback());
        evaluation = evaluationRepository.save(evaluation);

        // Update room statistics
        room.setAnsweredQuestions(room.getAnsweredQuestions() + 1);
        updateRoomAverageScore(room);
        interviewRoomRepository.save(room);

        return mapToEvaluationResponse(evaluation);
    }

    @Transactional
    public InterviewRoomResponse startInterview(UUID roomId, UUID userId) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Interview room not found"));

        if (!room.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        room.setStatus(InterviewStatus.in_progress);
        room.setStartedAt(LocalDateTime.now());
        room = interviewRoomRepository.save(room);

        return mapToResponse(room);
    }

    @Transactional
    public InterviewRoomResponse completeInterview(UUID roomId, UUID userId) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Interview room not found"));

        if (!room.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        room.setStatus(InterviewStatus.completed);
        room.setCompletedAt(LocalDateTime.now());
        room = interviewRoomRepository.save(room);

        // Create final review
        createFinalReview(room);

        return mapToResponse(room);
    }

    // Helper methods
    private InterviewRoomResponse mapToResponse(InterviewRoom room) {
        InterviewRoomResponse response = new InterviewRoomResponse();
        response.setId(room.getId());
        response.setName(room.getName());
        response.setUserId(room.getUser().getId());
        response.setStatus(room.getStatus());
        response.setProfessionalPositionId(room.getProfessionalPosition().getId());
        response.setProfessionalPositionName(room.getProfessionalPosition().getName());
        response.setDifficultLevelId(room.getDifficultLevel().getId());
        response.setDifficultLevelName(room.getDifficultLevel().getName());
        response.setTotalQuestions(room.getTotalQuestions());
        response.setAnsweredQuestions(room.getAnsweredQuestions());
        response.setAverageScore(room.getAverageScore());
        response.setStartedAt(room.getStartedAt());
        response.setCompletedAt(room.getCompletedAt());

        if (room.getQuestions() != null) {
            List<QuestionResponse> questions = room.getQuestions().stream()
                    .map(this::mapToQuestionResponse)
                    .collect(Collectors.toList());
            response.setQuestions(questions);
        }

        return response;
    }

    private QuestionResponse mapToQuestionResponse(InterviewRoomQuestion question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestion(question.getQuestion());
        response.setQuestionOrder(question.getQuestionOrder());
        if (question.getQuestionType() != null) {
            response.setQuestionTypeId(question.getQuestionType().getId());
            response.setQuestionTypeName(question.getQuestionType().getName());
        }
        if (question.getAnswer() != null) {
            response.setAnswer(mapToAnswerResponse(question.getAnswer()));
        }
        return response;
    }

    private AnswerResponse mapToAnswerResponse(InterviewRoomAnswer answer) {
        AnswerResponse response = new AnswerResponse();
        response.setId(answer.getId());
        response.setAnswerText(answer.getAnswerText());
        if (answer.getEvaluation() != null) {
            response.setEvaluation(mapToEvaluationResponse(answer.getEvaluation()));
        }
        return response;
    }

    private EvaluationResponse mapToEvaluationResponse(InterviewRoomEvaluation evaluation) {
        EvaluationResponse response = new EvaluationResponse();
        response.setId(evaluation.getId());
        response.setScore(evaluation.getScore());
        response.setTotalScore(evaluation.getTotal());
        response.setAiFeedback(evaluation.getAiFeedback());
        return response;
    }

    private void updateRoomAverageScore(InterviewRoom room) {
        // Calculate average score from all evaluations in this room
        List<InterviewRoomQuestion> questions = questionRepository.findAll().stream()
                .filter(q -> q.getInterviewRoom().getId().equals(room.getId()))
                .collect(Collectors.toList());

        double totalScore = 0.0;
        int count = 0;

        for (InterviewRoomQuestion q : questions) {
            if (q.getAnswer() != null && q.getAnswer().getEvaluation() != null) {
                totalScore += q.getAnswer().getEvaluation().getTotal();
                count++;
            }
        }

        if (count > 0) {
            room.setAverageScore((float) (totalScore / count));
        }
    }

    private void createFinalReview(InterviewRoom room) {
        InterviewRoomReview review = new InterviewRoomReview();
        review.setId(UUID.randomUUID());
        review.setInterviewRoom(room);
        review.setTotal(room.getAverageScore() != null ? room.getAverageScore() : 0.0);
        review.setReview("{}"); // You can generate a comprehensive review here
        reviewRepository.save(review);
    }
}
