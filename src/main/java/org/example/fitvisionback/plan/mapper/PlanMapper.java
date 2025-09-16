package org.example.fitvisionback.plan.mapper;

import org.example.fitvisionback.plan.dto.PlanDto;
import org.example.fitvisionback.plan.entity.Plan;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanMapper {
    Plan toEntity(PlanDto planDto);
    PlanDto toDto(Plan plan);
    List<PlanDto> toDtoList(List<Plan> plans);
}
