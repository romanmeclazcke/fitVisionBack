package org.example.fitvisionback.creditsConsumed.controller;

import org.example.fitvisionback.creditsConsumed.dto.CreditsConsumedChartPointDto;
import org.example.fitvisionback.creditsConsumed.dto.CreditsConsumedChartType;
import org.example.fitvisionback.creditsConsumed.service.CreditsConsumedService;
import org.example.fitvisionback.user.entity.User;
import org.example.fitvisionback.utils.GetUserConected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/credits-consumed")
public class CreditsConsumedController {

    @Autowired
    private CreditsConsumedService creditsConsumedService;

    @Autowired
    private GetUserConected getUserConected;

    @GetMapping("/chart")
    public ResponseEntity<List<CreditsConsumedChartPointDto>> getCreditsChart(
            @RequestParam CreditsConsumedChartType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        User user = getUserConected.getUserConected();
        return ResponseEntity.ok(creditsConsumedService.getCreditsConsumedChart(user, type, startDate, endDate));
    }
}
