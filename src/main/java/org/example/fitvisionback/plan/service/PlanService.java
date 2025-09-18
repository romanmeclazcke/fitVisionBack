package org.example.fitvisionback.plan.service;

import org.example.fitvisionback.plan.dto.PlanDto;
import org.example.fitvisionback.plan.entity.Plan;

import java.util.List;
import java.util.UUID;

public interface PlanService {
    Plan getPlanById(UUID planId);
    List<PlanDto> getPlans();
}
