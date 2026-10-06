package com.example.rafeeq.Config;

import com.example.rafeeq.Model.EvidenceReference;
import com.example.rafeeq.Repository.EvidenceReferenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EvidenceDataInitializer implements CommandLineRunner {

    private final EvidenceReferenceRepository evidenceReferenceRepository;

    @Override
    public void run(String... args) {

        saveIfNotExists(
                "Standards of Care in Diabetes—2026: Facilitating Positive Health Behaviors and Well-being",
                "American Diabetes Association",
                "DIABETES_NUTRITION",
                "Nutrition plans for people with diabetes should be individualized and should consider nutrient quality, total calories, metabolic goals, preferences, and nutritional needs. Eating patterns should emphasize nonstarchy vegetables, whole fruits, legumes, lean proteins, whole grains, nuts and seeds, and low-fat dairy or nondairy alternatives while minimizing sugar-sweetened beverages, sweets, refined grains, processed foods, and ultraprocessed foods.",
                "American Diabetes Association Professional Practice Committee",
                "10.2337/dc26-S005",
                "https://diabetesjournals.org/care/article/49/Supplement_1/S89/163932/"
        );

        saveIfNotExists(
                "Standards of Care in Diabetes—2026: Facilitating Positive Health Behaviors and Well-being",
                "American Diabetes Association",
                "DIABETES_EXERCISE",
                "Most adults with type 1 or type 2 diabetes should engage in at least 150 minutes of moderate- to vigorous-intensity aerobic activity per week, spread over at least 3 days, with resistance exercise 2–3 times per week on nonconsecutive days, unless contraindicated.",
                "American Diabetes Association Professional Practice Committee",
                "10.2337/dc26-S005",
                "https://diabetesjournals.org/care/article/49/Supplement_1/S89/163932/"
        );

        saveIfNotExists(
                "2025 High Blood Pressure Guideline",
                "American Heart Association / American College of Cardiology",
                "HYPERTENSION",
                "Lifestyle interventions for elevated blood pressure and hypertension include a heart-healthy eating pattern such as DASH, sodium reduction, appropriate dietary potassium, moderate physical activity, healthy weight management, and stress management.",
                "AHA/ACC Joint Committee on Clinical Practice Guidelines",
                "10.1161/CIR.0000000000001356",
                "https://professional.heart.org/en/science-news/2025-high-blood-pressure-guideline/top-things-to-know"
        );

        saveIfNotExists(
                "2025 High Blood Pressure Guideline",
                "American Heart Association / American College of Cardiology",
                "HYPERTENSION_NUTRITION",
                "A heart-healthy eating pattern such as DASH and reduction of sodium intake are recommended lifestyle measures for elevated blood pressure and hypertension.",
                "AHA/ACC Joint Committee on Clinical Practice Guidelines",
                "10.1161/CIR.0000000000001356",
                "https://professional.heart.org/en/science-news/2025-high-blood-pressure-guideline/top-things-to-know"
        );

        saveIfNotExists(
                "Dietary Approaches to Stop Hypertension (DASH) Diet and Blood Pressure Reduction in Adults with and without Hypertension",
                "American Society for Nutrition / PubMed",
                "HYPERTENSION_NUTRITION",
                "A systematic review and meta-analysis of randomized controlled trials found that DASH reduced systolic and diastolic blood pressure compared with control diets.",
                "Filippou et al.",
                "10.1093/advances/nmaa041",
                "https://pubmed.ncbi.nlm.nih.gov/32330233/"
        );

        saveIfNotExists(
                "Effect of Dietary Carbohydrate Restriction on Glycemic Control in Adults With Diabetes",
                "PubMed",
                "DIABETES_NUTRITION",
                "A systematic review and meta-analysis found that carbohydrate-restricted diets may improve HbA1c over some time periods, while evidence does not establish one universally ideal macronutrient distribution. Recommendations should therefore remain individualized.",
                "Sainsbury et al.",
                "10.1016/j.diabres.2018.02.026",
                "https://pubmed.ncbi.nlm.nih.gov/29522789/"
        );

        saveIfNotExists(
                "Effects of Exercise Training and Physical Activity Advice on HbA1c in People With Type 2 Diabetes",
                "PubMed",
                "DIABETES_EXERCISE",
                "A network meta-analysis of randomized controlled trials found that aerobic, resistance, combined, and other exercise approaches were associated with improved HbA1c in people with type 2 diabetes.",
                "Schaan et al.",
                "10.1016/j.diabres.2025.112027",
                "https://pubmed.ncbi.nlm.nih.gov/39904457/"
        );

        saveIfNotExists(
                "Physical Activity",
                "World Health Organization",
                "GENERAL_EXERCISE",
                "Adults should perform regular physical activity, with a general target of at least 150 minutes of moderate-intensity aerobic activity per week or an equivalent combination, with muscle-strengthening activities on at least 2 days per week.",
                "World Health Organization",
                null,
                "https://www.who.int/initiatives/behealthy/physical-activity/"
        );

        saveIfNotExists(
                "Healthy Eating Pattern",
                "BasirhAI Evidence Base",
                "GENERAL_NUTRITION",
                "Nutrition recommendations should prioritize minimally processed foods, vegetables, appropriate fruit, whole grains and other high-fiber foods, lean protein sources, and appropriate portions while considering individual health conditions and preferences.",
                "BasirhAI",
                null,
                null
        );
    }

    private void saveIfNotExists(
            String title,
            String organization,
            String topic,
            String recommendation,
            String authors,
            String doi,
            String sourceUrl
    ) {

        if (!evidenceReferenceRepository.existsByTitleAndTopic(title, topic)) {

            EvidenceReference evidence = new EvidenceReference();

            evidence.setTitle(title);
            evidence.setOrganization(organization);
            evidence.setTopic(topic);
            evidence.setRecommendation(recommendation);
            evidence.setAuthors(authors);
            evidence.setDoi(doi);
            evidence.setSourceUrl(sourceUrl);

            evidenceReferenceRepository.save(evidence);
        }
    }
}