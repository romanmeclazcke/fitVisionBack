package org.example.fitvisionback.creditsConsumed.service;

import org.example.fitvisionback.creditsConsumed.dto.CreditsConsumedChartPointDto;
import org.example.fitvisionback.creditsConsumed.dto.CreditsConsumedChartType;
import org.example.fitvisionback.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CreditsConsumedService {
    void saveCreditConsumed(User userConected);

    List<CreditsConsumedChartPointDto> getCreditsConsumedChart(User user,
                                                               CreditsConsumedChartType chartType,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate);
}
