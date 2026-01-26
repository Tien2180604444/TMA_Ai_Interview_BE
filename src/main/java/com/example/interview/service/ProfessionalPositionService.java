package com.example.interview.service;

import com.example.interview.constaint.CacheName;
import com.example.interview.entity.caching.ProfessionalPosition;
import com.example.interview.repository.CacheRepository.ProfessionalPositionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProfessionalPositionService {
    private final ProfessionalPositionRepository professionalPositionRepository;

    @Cacheable(value = CacheName.PROFESSIONAL_POSITIONS)
    public List<ProfessionalPosition> getAllProfessionalPositions() {
        log.info("Fetching professional positions from database");
        return professionalPositionRepository.findAll();
    }

    @Cacheable(value = CacheName.PROFESSIONAL_POSITIONS, key = "#id")
    public ProfessionalPosition getProfessionalPositionById(java.util.UUID id) {
        log.info("Fetching professional position by id from database");
        return professionalPositionRepository.findById(id).orElse(null);
    }
    @CacheEvict(value = CacheName.PROFESSIONAL_POSITIONS)
    public void evictProfessionalPositionsCache() {
        log.info("Evicting professional positions cache");
    }
}
