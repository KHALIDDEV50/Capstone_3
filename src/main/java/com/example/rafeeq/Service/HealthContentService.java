package com.example.rafeeq.Service;

import com.example.rafeeq.DTO.HealthContentResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HealthContentService {

    public HealthContentResponseDTO getHealthContent() {

        List<HealthContentResponseDTO.HealthArticleDTO> articles =
                new ArrayList<>();

        articles.add(new HealthContentResponseDTO.HealthArticleDTO(
                "The Benefits of Regular Physical Activity",
                "PHYSICAL_ACTIVITY",
                "Regular physical activity can support cardiovascular health, physical fitness, and overall quality of life.",
                "World Health Organization",
                LocalDate.now(),
                "https://www.who.int/health-topics/physical-activity"
        ));

        articles.add(new HealthContentResponseDTO.HealthArticleDTO(
                "Healthy Eating for Better Heart Health",
                "NUTRITION",
                "A balanced eating pattern that includes vegetables, fruits, whole foods, and fiber can support overall heart health.",
                "American Heart Association",
                LocalDate.now(),
                "https://www.heart.org/en/healthy-living/healthy-eating"
        ));

        articles.add(new HealthContentResponseDTO.HealthArticleDTO(
                "Understanding Blood Pressure",
                "HEART_HEALTH",
                "Understanding blood pressure and regularly monitoring it can help people become more aware of their cardiovascular health.",
                "American Heart Association",
                LocalDate.now(),
                "https://www.heart.org/en/health-topics/high-blood-pressure"
        ));

        articles.add(new HealthContentResponseDTO.HealthArticleDTO(
                "Healthy Sleep Habits",
                "SLEEP",
                "Healthy sleep habits can support physical health, mental wellbeing, and daily performance.",
                "Centers for Disease Control and Prevention",
                LocalDate.now(),
                "https://www.cdc.gov/sleep/"
        ));

        articles.add(new HealthContentResponseDTO.HealthArticleDTO(
                "Diabetes and Healthy Living",
                "DIABETES",
                "Healthy lifestyle habits and regular health monitoring can support people in managing their metabolic health.",
                "Centers for Disease Control and Prevention",
                LocalDate.now(),
                "https://www.cdc.gov/diabetes/"
        ));

        return new HealthContentResponseDTO(articles);
    }
}