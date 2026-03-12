package org.example.fitvisionback.creditsConsumed.service;

import org.example.fitvisionback.creditsConsumed.dto.CreditsConsumedChartPointDto;
import org.example.fitvisionback.creditsConsumed.dto.CreditsConsumedChartType;
import org.example.fitvisionback.creditsConsumed.model.CreditsConsumed;
import org.example.fitvisionback.creditsConsumed.repository.CreditsConsumedRepository;
import org.example.fitvisionback.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreditsConsumedServiceImpl implements CreditsConsumedService{

    @Autowired
    private CreditsConsumedRepository creditsConsumedRepository;

    public CreditsConsumedServiceImpl(CreditsConsumedRepository creditsConsumedRepository) {
        this.creditsConsumedRepository = creditsConsumedRepository;
    }

    @Override
    public void saveCreditConsumed(User userConected) {
        CreditsConsumed creditsConsumed = new CreditsConsumed();
        creditsConsumed.setUser(userConected);
        creditsConsumedRepository.save(creditsConsumed);
    }

    @Override
    public List<CreditsConsumedChartPointDto> getCreditsConsumedChart(User user,
                                                                      CreditsConsumedChartType chartType,
                                                                      LocalDateTime startDate,
                                                                      LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("The start date must be before the end date");
        }

        List<CreditsConsumed> consumptions = creditsConsumedRepository
                .findAllByUserIdAndConsumedAtBetweenOrderByConsumedAt(user.getId(), startDate, endDate);

        return switch (chartType) {
            case DAILY -> buildDaily(consumptions, startDate);
            case WEEKLY -> buildWeekly(consumptions, startDate, endDate);
            case MONTHLY -> buildMonthly(consumptions, startDate, endDate);
            case ANNUAL -> buildAnnual(consumptions, startDate, endDate);
        };
    }

    private List<CreditsConsumedChartPointDto> buildDaily(List<CreditsConsumed> consumptions,
                                                          LocalDateTime startDate) {
        Map<String, Long> buckets = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00");

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime hourMark = startDate.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            buckets.put(formatter.format(hourMark), 0L);
        }

        for (CreditsConsumed consumption : consumptions) {
            String label = formatter.format(consumption.getConsumedAt());
            buckets.computeIfPresent(label, (key, current) -> current + 1);
        }

        return toDtoList(buckets);
    }

    private List<CreditsConsumedChartPointDto> buildWeekly(List<CreditsConsumed> consumptions,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate) {
        Map<String, Long> buckets = new LinkedHashMap<>();

        for (LocalDate cursor = startDate.toLocalDate(); !cursor.isAfter(endDate.toLocalDate()); cursor = cursor.plusDays(1)) {
            buckets.put(cursor.toString(), 0L);
        }

        for (CreditsConsumed consumption : consumptions) {
            LocalDate day = consumption.getConsumedAt().toLocalDate();
            String label = day.toString();
            buckets.computeIfPresent(label, (key, current) -> current + 1);
        }

        return toDtoList(buckets);
    }

    private List<CreditsConsumedChartPointDto> buildMonthly(List<CreditsConsumed> consumptions,
                                                            LocalDateTime startDate,
                                                            LocalDateTime endDate) {
        Map<String, Long> buckets = new LinkedHashMap<>();

        LocalDate firstWeekStart = startDate.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastDay = endDate.toLocalDate();

        for (LocalDate weekStart = firstWeekStart; !weekStart.isAfter(lastDay); weekStart = weekStart.plusWeeks(1)) {
            buckets.put(weekStart.toString(), 0L);
        }

        for (CreditsConsumed consumption : consumptions) {
            LocalDate weekStart = consumption.getConsumedAt().toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            String label = weekStart.toString();
            buckets.computeIfPresent(label, (key, current) -> current + 1);
        }

        return toDtoList(buckets);
    }

    private List<CreditsConsumedChartPointDto> buildAnnual(List<CreditsConsumed> consumptions,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate) {
        Map<String, Long> buckets = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        for (YearMonth cursor = startMonth; !cursor.isAfter(endMonth); cursor = cursor.plusMonths(1)) {
            buckets.put(formatter.format(cursor.atDay(1)), 0L);
        }

        for (CreditsConsumed consumption : consumptions) {
            YearMonth month = YearMonth.from(consumption.getConsumedAt());
            String label = formatter.format(month.atDay(1));
            buckets.computeIfPresent(label, (key, current) -> current + 1);
        }

        return toDtoList(buckets);
    }

    private List<CreditsConsumedChartPointDto> toDtoList(Map<String, Long> buckets) {
        List<CreditsConsumedChartPointDto> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : buckets.entrySet()) {
            result.add(new CreditsConsumedChartPointDto(entry.getKey(), entry.getValue()));
        }
        return result;
    }
}
