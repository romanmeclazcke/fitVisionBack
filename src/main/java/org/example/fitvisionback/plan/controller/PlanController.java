package org.example.fitvisionback.plan.controller;

import org.example.fitvisionback.plan.dto.PlanDto;
import org.example.fitvisionback.plan.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanService planService;



    @GetMapping("")
    public ResponseEntity<List<PlanDto>> getPlans(){
        return ResponseEntity.ok(planService.getPlans());
    }



}
