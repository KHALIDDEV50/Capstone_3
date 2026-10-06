package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.HealthInsightResponseDTO;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HealthInsightsService {

    private final VitalSignRepository vitalSignRepository;
    private final UserRepository userRepository;

    public HealthInsightResponseDTO getHealthInsights(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new ApiException("User not found");
        }

        List<HealthInsightResponseDTO.VitalInsightDTO> insights = new ArrayList<>();

        List<String> vitalTypes = List.of(
                "BLOOD_PRESSURE",
                "GLUCOSE",
                "WEIGHT",
                "WAIST",
                "HEART_RATE"
        );

        for (String type : vitalTypes) {

            List<VitalSign> vitalSigns =
                    vitalSignRepository.findTop2ByUserIdAndTypeOrderByMeasuredAtDesc(userId, type);

            if (!vitalSigns.isEmpty()) {
                insights.add(buildInsight(vitalSigns));
            }
        }

        if (insights.isEmpty()) {
            throw new ApiException("No vital signs found for this user");
        }

        return new HealthInsightResponseDTO(userId, insights);
    }

    private HealthInsightResponseDTO.VitalInsightDTO buildInsight(List<VitalSign> vitalSigns) {

        VitalSign latest = vitalSigns.get(0);

        if (latest.getType().equals("BLOOD_PRESSURE")) {
            return buildBloodPressureInsight(vitalSigns);
        }

        VitalSign previous = vitalSigns.size() > 1 ? vitalSigns.get(1) : null;

        BigDecimal change = null;
        BigDecimal changePercentage = null;
        String trend = "INSUFFICIENT_DATA";

        if (previous != null && latest.getValue() != null && previous.getValue() != null) {

            change = latest.getValue()
                    .subtract(previous.getValue())
                    .setScale(2, RoundingMode.HALF_UP);

            if (previous.getValue().compareTo(BigDecimal.ZERO) != 0) {
                changePercentage = change
                        .divide(previous.getValue(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            trend = determineTrend(change);
        }

        return new HealthInsightResponseDTO.VitalInsightDTO(
                latest.getType(),
                latest.getUnit(),
                latest.getValue(),
                previous != null ? previous.getValue() : null,
                change,
                changePercentage,
                null,
                null,
                null,
                null,
                null,
                null,
                trend,
                latest.getFlag(),
                latest.getMeasuredAt(),
                previous != null ? previous.getMeasuredAt() : null
        );
    }

    private HealthInsightResponseDTO.VitalInsightDTO buildBloodPressureInsight(
            List<VitalSign> vitalSigns) {

        VitalSign latest = vitalSigns.get(0);
        VitalSign previous = vitalSigns.size() > 1 ? vitalSigns.get(1) : null;

        BigDecimal systolicChange = null;
        BigDecimal diastolicChange = null;
        String trend = "INSUFFICIENT_DATA";

        if (previous != null) {

            if (latest.getSystolic() != null && previous.getSystolic() != null) {
                systolicChange = latest.getSystolic()
                        .subtract(previous.getSystolic())
                        .setScale(2, RoundingMode.HALF_UP);
            }

            if (latest.getDiastolic() != null && previous.getDiastolic() != null) {
                diastolicChange = latest.getDiastolic()
                        .subtract(previous.getDiastolic())
                        .setScale(2, RoundingMode.HALF_UP);
            }

            trend = determineBloodPressureTrend(
                    systolicChange,
                    diastolicChange
            );
        }

        return new HealthInsightResponseDTO.VitalInsightDTO(
                latest.getType(),
                latest.getUnit(),
                null,
                null,
                null,
                null,
                latest.getSystolic(),
                latest.getDiastolic(),
                previous != null ? previous.getSystolic() : null,
                previous != null ? previous.getDiastolic() : null,
                systolicChange,
                diastolicChange,
                trend,
                latest.getFlag(),
                latest.getMeasuredAt(),
                previous != null ? previous.getMeasuredAt() : null
        );
    }

    private String determineTrend(BigDecimal change) {

        if (change.compareTo(BigDecimal.ZERO) > 0) {
            return "INCREASED";
        }

        if (change.compareTo(BigDecimal.ZERO) < 0) {
            return "DECREASED";
        }

        return "UNCHANGED";
    }

    private String determineBloodPressureTrend(
            BigDecimal systolicChange,
            BigDecimal diastolicChange) {

        if (systolicChange == null || diastolicChange == null) {
            return "INSUFFICIENT_DATA";
        }

        boolean systolicIncreased =
                systolicChange.compareTo(BigDecimal.ZERO) > 0;

        boolean diastolicIncreased =
                diastolicChange.compareTo(BigDecimal.ZERO) > 0;

        boolean systolicDecreased =
                systolicChange.compareTo(BigDecimal.ZERO) < 0;

        boolean diastolicDecreased =
                diastolicChange.compareTo(BigDecimal.ZERO) < 0;

        if (systolicIncreased && diastolicIncreased) {
            return "INCREASED";
        }

        if (systolicDecreased && diastolicDecreased) {
            return "DECREASED";
        }

        if (systolicChange.compareTo(BigDecimal.ZERO) == 0
                && diastolicChange.compareTo(BigDecimal.ZERO) == 0) {
            return "UNCHANGED";
        }

        return "MIXED";
    }
}