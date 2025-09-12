package org.example.fitvisionback.plan.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.fitvisionback.plan.entity.Plan;
import org.example.fitvisionback.plan.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlanServiceImpl implements PlanService{


    private PlanRepository planRepository;

    @Autowired
    public PlanServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Plan getPlanById(UUID planId) {
        return this.planRepository.findById(planId).orElseThrow(() -> new EntityNotFoundException("Plan not found"));
    }
}
