package com.example.interview.service;

import com.example.interview.dto.gemini.GeminiEvaluationResponse;
import com.example.interview.dto.gemini.GeminiQuestionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiClientService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.api.temperature:0.3}")
    private Double temperature;

    /**
     * Generate interview question using Gemini API
     */
    public GeminiQuestionResponse generateQuestion(String jobGroup, String position,
                                                   String difficulty, String questionType) {
        String prompt = buildQuestionPrompt(jobGroup, position, difficulty, questionType);

        try {
            Map<String, Object> requestBody = buildRequestBody(prompt);

            Map<String, Object> response = restClient.post()
                    .uri("/{model}:generateContent?key={key}", model, apiKey.trim())
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            String questionText = extractTextFromResponse(response);

            GeminiQuestionResponse result = new GeminiQuestionResponse();
            result.setQuestion(questionText);
            result.setModelVersion(model);

            log.info("Generated question successfully for position: {}, type: {}", position, questionType);
            return result;

        } catch (Exception e) {
            log.error("Error generating question: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate question: " + e.getMessage(), e);
        }
    }

    /**
     * Evaluate answer and provide scoring with feedback
     */
    public GeminiEvaluationResponse evaluateAnswer(String question, String answer,
                                                   String jobGroup, String position,
                                                   String difficulty, List<String> criteriaNames) {
        String prompt = buildEvaluationPrompt(question, answer, jobGroup, position, difficulty, criteriaNames);

        try {
            Map<String, Object> requestBody = buildRequestBody(prompt);

            Map<String, Object> response = restClient.post()
                    .uri("/{model}:generateContent?key={key}", model, apiKey.trim())
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            String evaluationText = extractTextFromResponse(response);

            GeminiEvaluationResponse result = parseEvaluationResponse(evaluationText, criteriaNames);

            log.info("Evaluated answer successfully. Total score: {}", result.getTotalScore());
            return result;

        } catch (Exception e) {
            log.error("Error evaluating answer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to evaluate answer: " + e.getMessage(), e);
        }
    }

    /**
     * Build request body for Gemini API
     */
    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", prompt)
                ))
        ));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", temperature);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 2048);
        body.put("generationConfig", generationConfig);

        return body;
    }

    /**
     * Build prompt for question generation
     */
    private String buildQuestionPrompt(String jobGroup, String position, String difficulty, String questionType) {
        return String.format(
                "Bạn là một chuyên gia tuyển dụng kỹ thuật cao cấp. Hãy tạo ra 3 bài toán phỏng vấn thực tế cho vị trí [{{job_title}}].\n" +
                        "\n" +
                        "YÊU CẦU NỘI DUNG:\n" +
                        "- Cấp độ: [{{difficulty_level}}]\n" +
                        "- Dạng câu hỏi: [{{question_type}}]\n" +
                        "- Phong cách: Đặt ứng viên vào một tình huống thực tế (scenario-based). Câu hỏi phải có tính liên kết, bao gồm nhiều khía cạnh từ thiết kế, giải pháp đến xử lý lỗi.\n" +
                        "- Mục tiêu: Câu hỏi phải đủ sâu để ứng viên bộc lộ tư duy hệ thống và kỹ năng thực hiện.\n" +
                        "\n" +
                        "YÊU CẦU ĐỊNH DẠNG KHẮT KHE:\n" +
                        "1. CHỈ trả về nội dung các câu hỏi.\n" +
                        "2. Mỗi câu hỏi nằm trên một dòng mới.\n" +
                        "3. KHÔNG có số thứ tự (1, 2, 3), KHÔNG có lời chào, KHÔNG có định dạng JSON.",
                position, difficulty, questionType
        );
    }

    /**
     * Build prompt for answer evaluation
     */
    private String buildEvaluationPrompt(String question, String answer, String jobGroup,
                                         String position, String difficulty, List<String> criteriaNames) {
        StringBuilder criteriaList = new StringBuilder();
        for (int i = 0; i < criteriaNames.size(); i++) {
            criteriaList.append(String.format("%d. %s\n", i + 1, criteriaNames.get(i)));
        }

        return String.format(
                "Bạn là người tuyển dụng tại công ty đang tuyển vị trí [{{job_title}}]. Ứng viên vừa trả lời câu hỏi:\n" +
                        "\n" +
                        "**Câu hỏi:** \"\"{{question}}\"\"\n" +
                        "\n" +
                        "**Trả lời:** \"\"{{answer}}\"\"\n" +
                        "\n" +
                        "Hãy đánh giá câu trả lời theo các tiêu chí sau:\n" +
                        "- Mức độ liên quan đến câu hỏi (0–5)\n" +
                        "- Kỹ năng trình bày, thuyết phục (0–5)\n" +
                        "- Kỹ năng chuyên môn hoặc tình huống thực tế (0–5)\n" +
                        "\n" +
                        "Sau đó, đưa ra nhận xét ngắn gọn (1–2 câu) và gợi ý cải thiện.\n" +
                        "\n" +
                        "Output ở dạng JSON:\n" +
                        "{\n" +
                        "  \"\"relevance\"\": 4,\n" +
                        "  \"\"presentation\"\": 3,\n" +
                        "  \"\"skill\"\": 2,\n" +
                        "  \"\"comment\"\": \"\"Câu trả lời chưa đi sâu vào ví dụ thực tế. Hãy mô tả một tình huống cụ thể bạn đã xử lý.\n" +
                        "}\"",
                question, answer, jobGroup, position, difficulty, criteriaList.toString()
        );
    }

   
    private String extractTextFromResponse(Map<String, Object> response) {
        try {
            if (response == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates in response");
            }

            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            if (content == null) {
                throw new RuntimeException("No content in candidate");
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("No parts in content");
            }

            String text = (String) parts.get(0).get("text");
            if (text == null || text.trim().isEmpty()) {
                throw new RuntimeException("Empty text in response");
            }

            return text.trim();

        } catch (Exception e) {
            log.error("Error extracting text from response: {}", e.getMessage());
            throw new RuntimeException("Failed to extract text from Gemini response: " + e.getMessage(), e);
        }
    }

    /**
     * Parse evaluation response from Gemini
     */
    private GeminiEvaluationResponse parseEvaluationResponse(String responseText, List<String> criteriaNames) {
        try {
            // Try to extract JSON from response (might have markdown code blocks)
            String jsonText = responseText;
            if (responseText.contains("```json")) {
                int start = responseText.indexOf("```json") + 7;
                int end = responseText.indexOf("```", start);
                jsonText = responseText.substring(start, end).trim();
            } else if (responseText.contains("```")) {
                int start = responseText.indexOf("```") + 3;
                int end = responseText.indexOf("```", start);
                jsonText = responseText.substring(start, end).trim();
            }

            // Parse JSON
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            GeminiEvaluationResponse result = new GeminiEvaluationResponse();

            // Parse scores
            JsonNode scoresNode = jsonNode.get("scores");
            Map<String, Double> scores = new HashMap<>();
            if (scoresNode != null && scoresNode.isObject()) {
                scoresNode.fields().forEachRemaining(entry -> {
                    String key = entry.getKey();
                    JsonNode value = entry.getValue();
                    if (value.isNumber()) {
                        scores.put(key, value.asDouble());
                    }
                });
            }
            result.setScores(scores);

            // Parse total score
            JsonNode totalScoreNode = jsonNode.get("totalScore");
            if (totalScoreNode != null && totalScoreNode.isNumber()) {
                result.setTotalScore(totalScoreNode.asDouble());
            } else {
                // Calculate from scores if totalScore not provided
                double avg = scores.values().stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
                result.setTotalScore(avg * 10); // Scale 0-10 to 0-100
            }

            // Parse feedback
            JsonNode feedbackNode = jsonNode.get("feedback");
            if (feedbackNode != null && feedbackNode.isTextual()) {
                result.setFeedback(feedbackNode.asText());
            } else {
                result.setFeedback("Không có nhận xét chi tiết.");
            }

            return result;

        } catch (JsonProcessingException e) {
            log.error("Error parsing evaluation response: {}", e.getMessage());
            // Fallback: create a basic response
            GeminiEvaluationResponse result = new GeminiEvaluationResponse();
            result.setScores(new HashMap<>());
            result.setTotalScore(0.0);
            result.setFeedback("Lỗi khi phân tích phản hồi từ AI. Vui lòng thử lại.");
            return result;
        }
    }
}
