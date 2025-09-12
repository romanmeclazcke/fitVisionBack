package org.example.fitvisionback.plan.mapper;

import org.example.fitvisionback.plan.dto.PlanDto;
import org.example.fitvisionback.plan.entity.Plan;
import org.mapstruct.Mapper;

@Mapper
public interface PlanMapper {
    Plan toEntity(PlanDto planDto);
    PlanDto toDto(Plan plan);
}
