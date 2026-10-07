package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.DailyTimelineDTO;
import com.example.rafeeq.DTO.DailyTimelineItemDTO;
import com.example.rafeeq.Model.ExercisePlan;
import com.example.rafeeq.Model.MedicationSchedule;
import com.example.rafeeq.Model.NutritionPlan;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Repository.ExercisePlanRepository;
import com.example.rafeeq.Repository.MedicationScheduleRepository;
import com.example.rafeeq.Repository.NutritionPlanRepository;
import com.example.rafeeq.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyTimelineService {

    private final UserRepository userRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final ExercisePlanRepository exercisePlanRepository;


    public DailyTimelineDTO getDailyTimeline(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<DailyTimelineItemDTO> timeline = new ArrayList<>();


        // Medications
        List<MedicationSchedule> medications =
                medicationScheduleRepository.findByUser_IdAndIsActiveTrue(userId);

        for (MedicationSchedule medication : medications) {

            if (medication.getTimes() == null) {
                continue;
            }

            String[] times = medication.getTimes().split(",");

            for (String time : times) {

                timeline.add(
                        new DailyTimelineItemDTO(
                                time.trim(),
                                "MEDICATION",
                                medication.getMedicationName(),
                                medication.getDosage()
                                        + " - "
                                        + medication.getMealRelation()
                        )
                );
            }
        }


        // Nutrition Plan
        NutritionPlan nutritionPlan =
                nutritionPlanRepository.findNutritionPlanByUserId(userId);

        if (nutritionPlan != null) {

            timeline.add(
                    new DailyTimelineItemDTO(
                            "حسب الوجبة",
                            "NUTRITION",
                            "خطة الوجبات",
                            nutritionPlan.getItems()
                    )
            );
        }


        // Exercise Plan
        ExercisePlan exercisePlan =
                exercisePlanRepository.findExercisePlanByUserId(userId);

        if (exercisePlan != null) {

            timeline.add(
                    new DailyTimelineItemDTO(
                            "أي وقت",
                            "EXERCISE",
                            exercisePlan.getGoal(),
                            exercisePlan.getExercises()
                    )
            );
        }


        timeline.sort(
                Comparator.comparing(
                        DailyTimelineItemDTO::getTime,
                        (time1, time2) -> {

                            boolean time1IsMedication =
                                    time1.matches("\\d{2}:\\d{2}");

                            boolean time2IsMedication =
                                    time2.matches("\\d{2}:\\d{2}");

                            if (time1IsMedication && time2IsMedication) {
                                return time1.compareTo(time2);
                            }

                            if (time1IsMedication) {
                                return -1;
                            }

                            if (time2IsMedication) {
                                return 1;
                            }

                            return 0;
                        }
                )
        );


        return new DailyTimelineDTO(
                userId,
                timeline
        );
    }
}