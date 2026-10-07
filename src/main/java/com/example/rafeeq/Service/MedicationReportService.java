package com.example.rafeeq.Service;

import com.example.rafeeq.DTO.AIMedicationAnalysisDTO;
import com.example.rafeeq.Model.MedicationSchedule;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Repository.HealthAssessmentRepository;
import com.example.rafeeq.Repository.MedicationScheduleRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MedicationReportService {

    private final JavaMailSender mailSender;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final HealthAssessmentRepository healthAssessmentRepository;
    private final OpenAIService openAIService;
    private final MedicationImpactReportService medicationImpactReportService;

    // Send the AI medication report to the user's email
    public void sendMedicationReport(
            User user,
            List<MedicationSchedule> medications,
            AIMedicationAnalysisDTO aiAnalysis) {

        MimeMessage message =
                mailSender.createMimeMessage();

        try {

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            // Sender email
            helper.setFrom("cs.develop71@gmail.com");

            // User email
            helper.setTo(user.getEmail());

            // Email subject
            helper.setSubject(
                    "رفيق | تحليل جدول الأدوية الذكي"
            );

            // Build HTML report
            String htmlReport =
                    buildMedicationReportHtml(
                            user,
                            medications,
                            aiAnalysis
                    );

            // Send as HTML
            helper.setText(
                    htmlReport,
                    true
            );

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send medication report email"
            );
        }
    }

    // Build the HTML medication report
    private String buildMedicationReportHtml(
            User user,
            List<MedicationSchedule> medications,
            AIMedicationAnalysisDTO aiAnalysis) {

        return """
                <!DOCTYPE html>

                <html lang="ar" dir="rtl">

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <style>

                        body {
                            margin: 0;
                            padding: 0;
                            background: #f4f7fb;
                            font-family: Arial, sans-serif;
                            color: #263238;
                        }

                        .container {
                            max-width: 700px;
                            margin: 30px auto;
                            background: white;
                            border-radius: 18px;
                            overflow: hidden;
                            box-shadow: 0 5px 20px rgba(0,0,0,0.08);
                        }

                        .header {
                            background: #16324f;
                            color: white;
                            padding: 30px;
                            text-align: center;
                        }

                        .header h1 {
                            margin: 0;
                            font-size: 25px;
                        }

                        .header p {
                            margin-top: 10px;
                            opacity: 0.85;
                        }

                        .content {
                            padding: 25px;
                        }

                        .summary {
                            background: #eef7f0;
                            border-radius: 14px;
                            padding: 20px;
                            margin: 20px 0;
                            line-height: 1.8;
                        }

                        .section {
                            margin-top: 25px;
                        }

                        .section-title {
                            font-size: 19px;
                            font-weight: bold;
                            margin-bottom: 12px;
                        }

                        .medication-card {
                            background: #f8fafc;
                            border-radius: 14px;
                            padding: 18px;
                            margin-bottom: 12px;
                            border-right: 5px solid #16324f;
                        }

                        .medication-name {
                            font-size: 19px;
                            font-weight: bold;
                            margin-bottom: 10px;
                        }

                        .medication-row {
                            padding: 5px 0;
                            color: #546e7a;
                        }

                        .ai-note {
                            background: #eef3f8;
                            border-radius: 10px;
                            padding: 12px;
                            margin-top: 12px;
                            color: #455a64;
                        }

                        .follow-up {
                            background: #fff3e0;
                            border-radius: 10px;
                            padding: 12px;
                            margin-bottom: 8px;
                        }

                        .recommendation {
                            background: #e8f5e9;
                            border-radius: 10px;
                            padding: 12px;
                            margin-bottom: 8px;
                        }

                        .footer {
                            text-align: center;
                            background: #f1f4f7;
                            padding: 20px;
                            color: #78909c;
                            font-size: 12px;
                            line-height: 1.7;
                        }

                    </style>

                </head>

                <body>

                    <div class="container">

                        <div class="header">

                            <h1>
                                💊 رفيق | تحليل جدول الأدوية
                            </h1>

                            <p>
                                تحليل ذكي وتنظيم معلومات الأدوية
                            </p>

                        </div>

                        <div class="content">

                            <p>
                                مرحبًا
                                <strong>%s</strong>،
                            </p>

                            <!-- AI Summary -->

                            <div class="summary">

                                <strong>
                                    🤖 الملخص الذكي
                                </strong>

                                <p>
                                    %s
                                </p>

                            </div>

                            <!-- Medications -->

                            <div class="section">

                                <div class="section-title">
                                    💊 الأدوية الحالية
                                </div>

                                %s

                            </div>

                            <!-- Follow Up -->

                            <div class="section">

                                <div class="section-title">
                                    🟠 نقاط تحتاج متابعة
                                </div>

                                %s

                            </div>

                            <!-- Recommendations -->

                            <div class="section">

                                <div class="section-title">
                                    💡 التوصيات العامة
                                </div>

                                %s

                            </div>

                        </div>

                        <div class="footer">

                            هذا التقرير مقدم من منصة رفيق
                            <br>

                            المعلومات الواردة للتوعية والتنظيم العام
                            وليست تشخيصًا طبيًا.

                            <br><br>

                            لا تقم بتغيير أو إيقاف أي دواء
                            دون استشارة الطبيب.

                        </div>

                    </div>

                </body>

                </html>

                """.formatted(

                user.getFullName(),

                aiAnalysis.getSummary(),

                buildMedicationCards(
                        medications,
                        aiAnalysis
                ),

                buildListHtml(
                        aiAnalysis.getFollowUp(),
                        "follow-up"
                ),

                buildListHtml(
                        aiAnalysis.getRecommendations(),
                        "recommendation"
                )
        );
    }

    // Build medication cards
    private String buildMedicationCards(
            List<MedicationSchedule> medications,
            AIMedicationAnalysisDTO aiAnalysis) {

        StringBuilder html =
                new StringBuilder();

        for (MedicationSchedule medication : medications) {

            String aiNote =
                    findMedicationNote(
                            medication.getMedicationName(),
                            aiAnalysis
                    );

            html.append("""
                    <div class="medication-card">

                        <div class="medication-name">
                            💊 %s
                        </div>

                        <div class="medication-row">
                            <strong>الجرعة:</strong>
                            %s
                        </div>

                        <div class="medication-row">
                            <strong>علاقة الطعام:</strong>
                            %s
                        </div>

                        <div class="medication-row">
                            <strong>الأوقات:</strong>
                            %s
                        </div>

                        <div class="medication-row">
                            <strong>تاريخ البداية:</strong>
                            %s
                        </div>

                        <div class="medication-row">
                            <strong>تاريخ النهاية:</strong>
                            %s
                        </div>

                        <div class="ai-note">
                            <strong>🤖 ملاحظة AI:</strong>
                            %s
                        </div>

                    </div>
                    """.formatted(

                    medication.getMedicationName(),
                    medication.getDosage(),
                    medication.getMealRelation(),
                    medication.getTimes(),
                    medication.getStartDate(),
                    medication.getEndDate(),
                    aiNote
            ));
        }

        return html.toString();
    }

    // Find AI note for a specific medication
    private String findMedicationNote(
            String medicationName,
            AIMedicationAnalysisDTO aiAnalysis) {

        if (aiAnalysis.getMedications() == null) {
            return "لا توجد ملاحظة متاحة حاليًا.";
        }

        for (AIMedicationAnalysisDTO.MedicationAnalysis medication
                : aiAnalysis.getMedications()) {

            if (medication.getName() != null
                    && medication.getName()
                    .equalsIgnoreCase(medicationName)) {

                return medication.getGeneralNote();
            }
        }

        return "لا توجد ملاحظة متاحة حاليًا.";
    }

    // Build HTML lists
    private String buildListHtml(
            List<String> items,
            String cssClass) {

        if (items == null || items.isEmpty()) {

            return """
                    <div class="%s">
                        لا توجد معلومات متاحة حاليًا.
                    </div>
                    """.formatted(cssClass);
        }

        StringBuilder html =
                new StringBuilder();

        for (String item : items) {

            html.append("""
                    <div class="%s">
                        • %s
                    </div>
                    """.formatted(
                    cssClass,
                    item
            ));
        }

        return html.toString();
    }

    //.....

}