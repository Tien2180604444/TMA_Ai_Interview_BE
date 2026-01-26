package com.example.interview.service;

import com.example.interview.constaint.CacheName;
import com.example.interview.entity.caching.EvaluationCiteria;
import com.example.interview.entity.caching.QEvaluationCiteria;
import com.example.interview.repository.CacheRepository.EvaluationCriteriaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EvaluationCriteriaService {
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;

    @Cacheable(value = CacheName.EVALUATION_CRITERIA)
    public List<EvaluationCiteria> getAllEvaluationCriterias() {
        log.info("Fetching evaluation criterias from database");
        return evaluationCriteriaRepository.findAll();
    }

    @Cacheable(value = CacheName.EVALUATION_CRITERIA, key = "#id")
    public EvaluationCiteria getEvaluationCriteriaById(java.util.UUID id) {
        log.info("Fetching evaluation criteria by id from database");
        return evaluationCriteriaRepository.findById(id).orElse(null);
    }
    @CacheEvict(value = CacheName.EVALUATION_CRITERIA)
    public void evictEvaluationCriteriasCache() {
        log.info("Evicting evaluation criterias cache");
    }

}
