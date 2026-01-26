package com.example.interview.service;

import com.example.interview.constaint.CacheName;
import com.example.interview.entity.caching.DifficultLevel;
import com.example.interview.repository.CacheRepository.DifficultLevelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DifficultLevelService {
    private final DifficultLevelRepository difficultLevelRepository;
    @Cacheable(value = CacheName.DIFFICULT_LEVELS)
    public List<DifficultLevel> getAllDifficultLevels() {
        log.info("Fetching difficult levels from database");
        return difficultLevelRepository.findAll();
    }
    @Cacheable(value = CacheName.DIFFICULT_LEVELS, key = "#id")
    public DifficultLevel getDifficultLevelById(java.util.UUID id) {
        log.info("Fetching difficult level by id from database");
        return difficultLevelRepository.findById(id).orElse(null);
    }
    @CacheEvict(value = CacheName.DIFFICULT_LEVELS)
    public void evictDifficultLevel() {
        log.info("Evicting evaluation criterias cache");
    }
}
