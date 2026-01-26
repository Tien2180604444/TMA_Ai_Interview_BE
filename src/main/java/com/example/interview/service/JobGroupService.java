package com.example.interview.service;

import com.example.interview.constaint.CacheName;
import com.example.interview.entity.caching.JobGroup;
import com.example.interview.repository.CacheRepository.JobGroupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class JobGroupService {
    private  final JobGroupRepository jobGroupRepository;

    @Cacheable(value = CacheName.JOB_GROUPS)
    public List<JobGroup> getAllJobGroups(){
        log.info("Fetching job groups from database");
        return jobGroupRepository.findAll();
    }
   @Cacheable(value = CacheName.JOB_GROUPS,key = "#id")
    public JobGroup getJobGroupById(UUID id){
        log.info("Fetching job group by id from database");
        return jobGroupRepository.findById(id).orElse(null);
    }
    @CacheEvict(value = CacheName.JOB_GROUPS)
    public void evictJobGroupsCache() {
        log.info("Evicting job groups cache");
    }
}
