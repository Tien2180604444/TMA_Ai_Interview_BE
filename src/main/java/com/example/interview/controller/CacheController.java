package com.example.interview.controller;

import com.example.interview.entity.caching.*;
import com.example.interview.service.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cache")
@AllArgsConstructor
public class CacheController {
    private final JobGroupService jobGroupService;
    private final DifficultLevelService difficultLevelService;
    private final QuestionTypeService questionTypeService;
    private final EvaluationCriteriaService evaluationCriteriaService;
    private final ProfessionalPositionService professionalPositionService;

    // Get cached data
    @GetMapping("/job-groups")
    public List<JobGroup> getALLJobGroups() {
        return jobGroupService.getAllJobGroups();
    }

    @GetMapping("/job-groups/{id}")
    public JobGroup getJobGroupById(@PathVariable UUID id) {
        return jobGroupService.getJobGroupById(id);
    }

    @GetMapping("/difficult-levels")
    public List<com.example.interview.entity.caching.DifficultLevel> getAllDifficultLevels() {
        return difficultLevelService.getAllDifficultLevels();
    }

    @GetMapping("/difficult-levels/{id}")
    public DifficultLevel getDifficultLevelById(@PathVariable  UUID id) {
        return difficultLevelService.getDifficultLevelById(id);
    }

    @GetMapping("/question-types")
    public List<QuestionType> getAllQuestionTypes() {
        return questionTypeService.getAllQuestionTypes();
    }

    @GetMapping("/question-types/{id}")
    public QuestionType getQuestionTypeById(@PathVariable  UUID id) {
        return questionTypeService.getQuestionTypeById(id);
    }

    @GetMapping("/professional-positions")
    public List<ProfessionalPosition> getAllProfessionalPositions() {
        return professionalPositionService.getAllProfessionalPositions();
    }

    @GetMapping("/professional-positions/{id}")
    public ProfessionalPosition getProfessionalPositionById(@PathVariable  UUID id) {
        return professionalPositionService.getProfessionalPositionById(id);
    }

    @GetMapping("/evaluation-criterias")
    public List<EvaluationCiteria> getAllEvaluationCriterias() {
        return evaluationCriteriaService.getAllEvaluationCriterias();
    }

    @GetMapping("/evaluation-criterias/{id}")
    public EvaluationCiteria getEvaluationCriteriaById(@PathVariable  UUID id) {
        return evaluationCriteriaService.getEvaluationCriteriaById(id);
    }

    //Delete cached data
    @PostMapping("/difficult-levels/evict")
    public void evictDifficultLevelsCache() {
        difficultLevelService.evictDifficultLevel();
    }

    @PostMapping("/job-groups/evict")
    public void evictJobGroupsCache() {
        jobGroupService.evictJobGroupsCache();
    }

    @PostMapping("/question-types/evict")
    public void evictQuestionTypesCache() {
        questionTypeService.evictQuestionTypesCache();
    }

    @PostMapping("/evaluation-criterias/evict")
    public void evictEvaluationCriteriaCache() {
        evaluationCriteriaService.evictEvaluationCriteriasCache();
    }

    @PostMapping("/professional-positions/evict")
    public void evictProfessionalPositionsCache() {
        professionalPositionService.evictProfessionalPositionsCache();
    }

}
