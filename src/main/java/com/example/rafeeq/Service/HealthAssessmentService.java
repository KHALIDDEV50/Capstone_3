package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.AIHealthProgressDTO;
import com.example.rafeeq.DTO.AIHealthSummaryDTO;
import com.example.rafeeq.DTO.AIHealthTrendDTO;
import com.example.rafeeq.DTO.HealthAssessmentDTO;
import com.example.rafeeq.Model.HealthAssessment;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Repository.HealthAssessmentRepository;
import com.example.rafeeq.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthAssessmentService {

    private final HealthAssessmentRepository healthAssessmentRepository;
    private final UserRepository userRepository;

    private final HealthTrendReportService healthTrendReportService;

    // Service responsible for communicating with OpenAI
    private final OpenAIService openAIService;

    // Service responsible for creating and sending HTML reports
    private final HealthReportService healthReportService;

    // Service responsible for building the AI health progress HTML report
    private final HealthProgressReportService healthProgressReportService;


    // Get all Health Assessments
    public List<HealthAssessment> getAllHealthAssessments() {

        return healthAssessmentRepository.findAll();
    }


    // Get Health Assessment By ID
    public HealthAssessment getHealthAssessmentById(Integer id) {

        HealthAssessment healthAssessment =
                healthAssessmentRepository.findById(id).orElse(null);

        if (healthAssessment == null) {
            throw new ApiException("Health Assessment not found");
        }

        return healthAssessment;
    }


    // Get Health Assessments By User ID
    public List<HealthAssessment> getHealthAssessmentsByUserId(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return healthAssessmentRepository.findByUser_Id(userId);
    }


    // Get Current Health Assessment By User ID
    public List<HealthAssessment> getCurrentHealthAssessmentsByUserId(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return healthAssessmentRepository.findByUser_IdAndIsCurrentTrue(userId);
    }


    // Add Health Assessment
    public void addHealthAssessment(
            HealthAssessmentDTO healthAssessmentDTO) {

        // Find User
        User user = userRepository
                .findById(healthAssessmentDTO.getUserId())
                .orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        // Create Health Assessment
        HealthAssessment healthAssessment = new HealthAssessment();

        // Set User
        healthAssessment.setUser(user);

        // Set Previous Assessment
        if (healthAssessmentDTO.getPreviousAssessmentId() != null) {

            HealthAssessment previousAssessment =
                    healthAssessmentRepository
                            .findById(healthAssessmentDTO.getPreviousAssessmentId())
                            .orElse(null);

            if (previousAssessment == null) {
                throw new ApiException(
                        "Previous Health Assessment not found"
                );
            }

            healthAssessment.setPreviousAssessment(previousAssessment);
        }

        healthAssessment.setAttachments(
                healthAssessmentDTO.getAttachments()
        );

        healthAssessment.setUserNotes(
                healthAssessmentDTO.getUserNotes()
        );

        healthAssessment.setProfileSnapshot(
                healthAssessmentDTO.getProfileSnapshot()
        );

        healthAssessment.setExtractedValues(
                healthAssessmentDTO.getExtractedValues()
        );

        healthAssessment.setAiConclusion(
                healthAssessmentDTO.getAiConclusion()
        );

        healthAssessment.setTrend(
                healthAssessmentDTO.getTrend()
        );

        healthAssessment.setIsCurrent(
                healthAssessmentDTO.getIsCurrent()
        );

        healthAssessment.setAssessmentDate(
                healthAssessmentDTO.getAssessmentDate()
        );

        healthAssessment.setNextDueDate(
                healthAssessmentDTO.getNextDueDate()
        );

        healthAssessmentRepository.save(healthAssessment);
    }


    // Update Health Assessment
    public void updateHealthAssessment(
            Integer id,
            HealthAssessmentDTO healthAssessmentDTO) {

        HealthAssessment oldHealthAssessment =
                healthAssessmentRepository.findById(id).orElse(null);

        if (oldHealthAssessment == null) {
            throw new ApiException("Health Assessment not found");
        }

        // Find User
        User user = userRepository
                .findById(healthAssessmentDTO.getUserId())
                .orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        // Set User
        oldHealthAssessment.setUser(user);

        // Set Previous Assessment
        if (healthAssessmentDTO.getPreviousAssessmentId() != null) {

            HealthAssessment previousAssessment =
                    healthAssessmentRepository
                            .findById(
                                    healthAssessmentDTO.getPreviousAssessmentId()
                            )
                            .orElse(null);

            if (previousAssessment == null) {
                throw new ApiException(
                        "Previous Health Assessment not found"
                );
            }

            oldHealthAssessment.setPreviousAssessment(
                    previousAssessment
            );

        } else {

            oldHealthAssessment.setPreviousAssessment(null);
        }

        oldHealthAssessment.setAttachments(
                healthAssessmentDTO.getAttachments()
        );

        oldHealthAssessment.setUserNotes(
                healthAssessmentDTO.getUserNotes()
        );

        oldHealthAssessment.setProfileSnapshot(
                healthAssessmentDTO.getProfileSnapshot()
        );

        oldHealthAssessment.setExtractedValues(
                healthAssessmentDTO.getExtractedValues()
        );

        oldHealthAssessment.setAiConclusion(
                healthAssessmentDTO.getAiConclusion()
        );

        oldHealthAssessment.setTrend(
                healthAssessmentDTO.getTrend()
        );

        oldHealthAssessment.setIsCurrent(
                healthAssessmentDTO.getIsCurrent()
        );

        oldHealthAssessment.setAssessmentDate(
                healthAssessmentDTO.getAssessmentDate()
        );

        oldHealthAssessment.setNextDueDate(
                healthAssessmentDTO.getNextDueDate()
        );

        healthAssessmentRepository.save(oldHealthAssessment);
    }


    // Delete Health Assessment
    public void deleteHealthAssessment(Integer id) {

        HealthAssessment healthAssessment =
                healthAssessmentRepository.findById(id).orElse(null);

        if (healthAssessment == null) {
            throw new ApiException("Health Assessment not found");
        }

        healthAssessmentRepository.delete(healthAssessment);
    }


    // ==========================================================
    // ===================== Extra Point =========================
    // ==========================================================


    // Generate structured AI health summary
    public AIHealthSummaryDTO generateAIHealthSummary(
            Integer assessmentId) {

        // Find the health assessment
        HealthAssessment healthAssessment =
                healthAssessmentRepository
                        .findById(assessmentId)
                        .orElse(null);

        // Check if the assessment exists
        if (healthAssessment == null) {
            throw new ApiException(
                    "Health Assessment not found"
            );
        }

        // Get the user related to the assessment
        User user = healthAssessment.getUser();

        // Build the data that will be sent to OpenAI
        String healthData = """
                اسم المستخدم: %s

                تاريخ التقييم: %s

                ملاحظات المستخدم:
                %s

                معلومات الملف الصحي:
                %s

                القيم الصحية المستخرجة:
                %s

                الاتجاه الصحي:
                %s

                هل التقييم الحالي:
                %s

                تاريخ المتابعة القادمة:
                %s
                """.formatted(
                user.getFullName(),
                healthAssessment.getAssessmentDate(),
                healthAssessment.getUserNotes(),
                healthAssessment.getProfileSnapshot(),
                healthAssessment.getExtractedValues(),
                healthAssessment.getTrend(),
                healthAssessment.getIsCurrent(),
                healthAssessment.getNextDueDate()
        );

        // Send the health data to OpenAI
        // OpenAI returns a structured AIHealthSummaryDTO
        return openAIService.generateHealthSummary(healthData);
    }


    // Generate the AI summary and send the HTML report to the user's email
    public void generateAndSendAIHealthReport(
            Integer assessmentId) {

        // Find the health assessment
        HealthAssessment healthAssessment =
                healthAssessmentRepository
                        .findById(assessmentId)
                        .orElse(null);

        // Check if the assessment exists
        if (healthAssessment == null) {
            throw new ApiException(
                    "Health Assessment not found"
            );
        }

        // Generate the structured Arabic AI health summary
        // The result is AIHealthSummaryDTO, not String
        AIHealthSummaryDTO aiSummary =
                generateAIHealthSummary(assessmentId);

        // Send the HTML report to the user's email
        // together with the structured AI summary
        healthReportService.sendHealthReport(
                healthAssessment, aiSummary
        );
    }



    //....

    // Generate AI health trend by comparing the current assessment with the previous assessment
    public AIHealthTrendDTO generateAIHealthTrend(Integer assessmentId) {

        // Find the current assessment
        HealthAssessment currentAssessment =
                healthAssessmentRepository.findById(assessmentId).orElse(null);

        if (currentAssessment == null) {
            throw new ApiException("Health Assessment not found");
        }

        // Get the previous assessment
        HealthAssessment previousAssessment = currentAssessment.getPreviousAssessment();

        // Make sure a previous assessment exists
        if (previousAssessment == null) {
            throw new ApiException("Previous Health Assessment not found");
        }

        // Prepare the current assessment data
        String currentData = """
            Assessment Date: %s
            User Notes: %s
            Health Profile: %s
            Extracted Values: %s
            Trend: %s
            Is Current: %s
            Next Due Date: %s
            """.formatted(
                currentAssessment.getAssessmentDate(),
                currentAssessment.getUserNotes(),
                currentAssessment.getProfileSnapshot(),
                currentAssessment.getExtractedValues(),
                currentAssessment.getTrend(),
                currentAssessment.getIsCurrent(),
                currentAssessment.getNextDueDate()
        );

        // Prepare the previous assessment data
        String previousData = """
            Assessment Date: %s
            User Notes: %s
            Health Profile: %s
            Extracted Values: %s
            Trend: %s
            Is Current: %s
            Next Due Date: %s
            """.formatted(
                previousAssessment.getAssessmentDate(),
                previousAssessment.getUserNotes(),
                previousAssessment.getProfileSnapshot(),
                previousAssessment.getExtractedValues(),
                previousAssessment.getTrend(),
                previousAssessment.getIsCurrent(),
                previousAssessment.getNextDueDate()
        );

        // Send both assessments to OpenAI for comparison
        return openAIService.generateHealthTrend(
                currentData,
                previousData
        );
    }

    public void generateAndSendAIHealthTrendReport(
            Integer assessmentId) {

        HealthAssessment currentAssessment =
                healthAssessmentRepository
                        .findById(assessmentId)
                        .orElse(null);

        if (currentAssessment == null) {
            throw new ApiException(
                    "Health Assessment not found"
            );
        }

        HealthAssessment previousAssessment =
                currentAssessment.getPreviousAssessment();

        if (previousAssessment == null) {
            throw new ApiException(
                    "Previous Health Assessment not found"
            );
        }

        // Generate AI trend
        AIHealthTrendDTO aiTrend =
                generateAIHealthTrend(assessmentId);

        // Send the trend report by email
        healthTrendReportService.sendHealthTrendReport(
                currentAssessment,
                previousAssessment,
                aiTrend
        );
    }
    // ..

    // Generate AI Health Progress Report
    public AIHealthProgressDTO generateHealthProgressReport(
            Integer userId) {

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ApiException("User not found")
                );


        // Get all health assessments for the user
        List<HealthAssessment> assessments =
                healthAssessmentRepository.findByUser_Id(userId);


        // Check if the user has health assessments
        if (assessments.isEmpty()) {

            throw new ApiException(
                    "No health assessments found for this user"
            );
        }


        // Sort assessments by assessment date
        assessments.sort(Comparator.comparing(HealthAssessment::getAssessmentDate, Comparator.nullsLast(Comparator.naturalOrder())));


        // Build health assessment data
        String healthAssessmentData =
                assessments.stream()
                        .map(assessment ->
                                "Assessment Date: "
                                        + assessment.getAssessmentDate()

                                        + ", AI Conclusion: "
                                        + assessment.getAiConclusion()

                                        + ", Trend: "
                                        + assessment.getTrend()

                                        + ", User Notes: "
                                        + assessment.getUserNotes()

                                        + ", Extracted Values: "
                                        + assessment.getExtractedValues()
                        )
                        .collect(Collectors.joining("\n"));


        // Generate AI Health Progress Report
        return openAIService.generateHealthProgressReport(
                healthAssessmentData
        );
    }

    //..
    // Generate AI Health Progress HTML Report
    public String generateHealthProgressHtmlReport(
            Integer userId) {

        System.out.println("===== START HEALTH PROGRESS HTML =====");

        // Get user
        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ApiException("User not found")
                        );

        System.out.println("USER FOUND: " + user.getFullName());


        // Get assessments
        List<HealthAssessment> assessments =
                healthAssessmentRepository.findByUser_Id(userId);

        System.out.println(
                "ASSESSMENTS COUNT: " + assessments.size()
        );


        if (assessments.isEmpty()) {

            throw new ApiException(
                    "No health assessments found for this user"
            );
        }


        // Sort assessments by date
        assessments.sort(
                Comparator.comparing(
                        HealthAssessment::getAssessmentDate,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                )
        );

        System.out.println("ASSESSMENTS SORTED");


        // Build AI data
        String healthAssessmentData =
                assessments.stream()
                        .map(assessment ->
                                "Assessment Date: "
                                        + assessment.getAssessmentDate()
                                        + ", AI Conclusion: "
                                        + assessment.getAiConclusion()
                                        + ", Trend: "
                                        + assessment.getTrend()
                                        + ", User Notes: "
                                        + assessment.getUserNotes()
                                        + ", Extracted Values: "
                                        + assessment.getExtractedValues()
                        )
                        .collect(Collectors.joining("\n"));

        System.out.println("AI DATA CREATED");


        // Generate AI report
        AIHealthProgressDTO progressReport =
                openAIService.generateHealthProgressReport(
                        healthAssessmentData
                );

        System.out.println("AI REPORT CREATED");


        // Generate HTML
        String html =
                healthProgressReportService
                        .buildHealthProgressHtml(
                                user,
                                progressReport
                        );

        System.out.println("HTML CREATED");
        System.out.println(
                "HTML LENGTH: " + html.length()
        );

        System.out.println("===== END HEALTH PROGRESS HTML =====");


        return html;
    }
}