package org.example.fitvisionback.plan.service;

import org.example.fitvisionback.plan.entity.Plan;

import java.util.UUID;

public interface PlanService {
    Plan getPlanById(UUID planId);
}
