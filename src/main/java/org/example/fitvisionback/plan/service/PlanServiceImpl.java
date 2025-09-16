package org.example.fitvisionback.plan.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.fitvisionback.plan.dto.PlanDto;
import org.example.fitvisionback.plan.entity.Plan;
import org.example.fitvisionback.plan.mapper.PlanMapper;
import org.example.fitvisionback.plan.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlanServiceImpl implements PlanService{


    private PlanRepository planRepository;
    private PlanMapper planMapper;

    @Autowired
    public PlanServiceImpl(PlanRepository planRepository, PlanMapper planMapper) {
        this.planRepository = planRepository;
        this.planMapper = planMapper;
    }

    @Override
    public Plan getPlanById(UUID planId) {
        return this.planRepository.findById(planId).orElseThrow(() -> new EntityNotFoundException("Plan not found"));
    }

    @Override
    public List<PlanDto> getPlans() {
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        return this.planMapper.toDtoList(this.planRepository.findAll(sort));
    }
}
