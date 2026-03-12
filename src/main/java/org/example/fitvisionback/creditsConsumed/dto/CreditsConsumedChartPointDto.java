package org.example.fitvisionback.creditsConsumed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditsConsumedChartPointDto {
    private String label;
    private Long credits;
}
