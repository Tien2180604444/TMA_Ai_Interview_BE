package com.example.interview.service;

import com.example.interview.constaint.CacheName;
import com.example.interview.entity.caching.QuestionType;
import com.example.interview.repository.CacheRepository.QuestionTypeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class QuestionTypeService {
    private final QuestionTypeRepository questionTypeRepository;

    @Cacheable(value = CacheName.QUESTION_TYPES)
    public List<QuestionType> getAllQuestionTypes() {
        log.info("Fetching question types from database");
        return questionTypeRepository.findAll();
    }
    @Cacheable(value = CacheName.QUESTION_TYPES,key = "#id")
    public QuestionType getQuestionTypeById(java.util.UUID id) {
        log.info("Fetching question type by id from database");
        return questionTypeRepository.findById(id).orElse(null);
    }
    @CacheEvict(value = CacheName.QUESTION_TYPES)
    public void evictQuestionTypesCache() {
        log.info("Evicting question types cache");
    }
}
