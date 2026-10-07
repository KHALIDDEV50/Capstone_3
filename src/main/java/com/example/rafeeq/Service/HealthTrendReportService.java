package com.example.rafeeq.Service;

import com.example.rafeeq.DTO.AIHealthTrendDTO;
import com.example.rafeeq.Model.HealthAssessment;
import com.example.rafeeq.Model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@AllArgsConstructor
public class HealthTrendReportService {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    // Send the health trend report to the user's email
    public void sendHealthTrendReport(HealthAssessment currentAssessment, HealthAssessment previousAssessment, AIHealthTrendDTO aiTrend) {

        User user = currentAssessment.getUser();

        MimeMessage message = mailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Sender email
            helper.setFrom("cs.develop71@gmail.com");

            // User email
            helper.setTo(user.getEmail());

            // Email subject
            helper.setSubject("رفيق | تحليل تطور حالتك الصحية");

            // Build HTML report
            String htmlReport = buildHealthTrendHtml(currentAssessment, previousAssessment, user, aiTrend);

            // Send HTML email
            helper.setText(htmlReport, true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send health trend report email"
            );
        }
    }

    // Build the complete HTML email
    private String buildHealthTrendHtml(
            HealthAssessment current,
            HealthAssessment previous,
            User user,
            AIHealthTrendDTO aiTrend) {

        // Extract health values from both assessments
        JsonNode currentValues =
                readJson(current.getExtractedValues());

        JsonNode previousValues =
                readJson(previous.getExtractedValues());

        // Get individual metrics
        double currentGlucose =
                getDouble(currentValues, "glucose");

        double previousGlucose =
                getDouble(previousValues, "glucose");

        double currentWeight =
                getDouble(currentValues, "weight");

        double previousWeight =
                getDouble(previousValues, "weight");

        double currentHeartRate =
                getDouble(currentValues, "heartRate");

        double previousHeartRate =
                getDouble(previousValues, "heartRate");

        String currentBloodPressure =
                getText(currentValues, "bloodPressure");

        String previousBloodPressure =
                getText(previousValues, "bloodPressure");

        // Calculate percentage changes
        String glucoseChange =
                calculateChange(
                        previousGlucose,
                        currentGlucose
                );

        String weightChange =
                calculateChange(
                        previousWeight,
                        currentWeight
                );

        String heartRateChange =
                calculateChange(
                        previousHeartRate,
                        currentHeartRate
                );

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
                            font-size: 26px;
                        }

                        .header p {
                            margin-top: 10px;
                            opacity: 0.85;
                        }

                        .content {
                            padding: 25px;
                        }

                        .trend-box {
                            text-align: center;
                            background: #eef7f0;
                            border-radius: 14px;
                            padding: 20px;
                            margin-bottom: 25px;
                        }

                        .trend-title {
                            font-size: 14px;
                            color: #607d8b;
                        }

                        .trend-value {
                            font-size: 24px;
                            font-weight: bold;
                            margin-top: 8px;
                        }

                        .cards {
                            width: 100%%;
                            border-collapse: separate;
                            border-spacing: 10px;
                        }

                        .card {
                            background: #f8fafc;
                            border-radius: 12px;
                            padding: 18px;
                            text-align: center;
                            width: 33%%;
                        }

                        .metric-title {
                            font-size: 13px;
                            color: #607d8b;
                        }

                        .metric-value {
                            font-size: 21px;
                            font-weight: bold;
                            margin: 8px 0;
                        }

                        .change {
                            font-size: 13px;
                            font-weight: bold;
                        }

                        .section {
                            margin-top: 25px;
                        }

                        .section-title {
                            font-size: 19px;
                            font-weight: bold;
                            margin-bottom: 12px;
                        }

                        .finding {
                            background: #f8fafc;
                            border-radius: 10px;
                            padding: 12px;
                            margin-bottom: 8px;
                        }

                        .recommendation {
                            background: #fff8e1;
                            border-radius: 10px;
                            padding: 12px;
                            margin-bottom: 8px;
                        }

                        .comparison {
                            background: #f5f7fa;
                            border-radius: 14px;
                            padding: 18px;
                            margin-top: 25px;
                        }

                        .comparison-row {
                            padding: 10px 0;
                            border-bottom: 1px solid #e0e0e0;
                        }

                        .comparison-row:last-child {
                            border-bottom: none;
                        }

                        .footer {
                            text-align: center;
                            background: #f1f4f7;
                            padding: 20px;
                            color: #78909c;
                            font-size: 12px;
                        }

                    </style>

                </head>

                <body>

                    <div class="container">

                        <div class="header">

                            <h1>
                                رفيق | تحليل تطور الحالة الصحية
                            </h1>

                            <p>
                                مقارنة بين التقييم السابق والتقييم الحالي
                            </p>

                        </div>

                        <div class="content">

                            <p>
                                مرحبًا <strong>%s</strong>،
                            </p>

                            <div class="trend-box">

                                <div class="trend-title">
                                    الاتجاه الصحي العام
                                </div>

                                <div class="trend-value">
                                    %s
                                </div>

                            </div>

                            <!-- Health Metrics -->

                            <table class="cards">

                                <tr>

                                    <td class="card">

                                        <div class="metric-title">
                                            مستوى السكر
                                        </div>

                                        <div class="metric-value">
                                            %s
                                        </div>

                                        <div class="change">
                                            %s
                                        </div>

                                    </td>

                                    <td class="card">

                                        <div class="metric-title">
                                            الوزن
                                        </div>

                                        <div class="metric-value">
                                            %s
                                        </div>

                                        <div class="change">
                                            %s
                                        </div>

                                    </td>

                                    <td class="card">

                                        <div class="metric-title">
                                            معدل ضربات القلب
                                        </div>

                                        <div class="metric-value">
                                            %s
                                        </div>

                                        <div class="change">
                                            %s
                                        </div>

                                    </td>

                                </tr>

                            </table>

                            <!-- Blood Pressure -->

                            <div class="comparison">

                                <div class="section-title">
                                    ❤️ ضغط الدم
                                </div>

                                <div class="comparison-row">

                                    السابق:
                                    <strong>%s</strong>

                                </div>

                                <div class="comparison-row">

                                    الحالي:
                                    <strong>%s</strong>

                                </div>

                            </div>

                            <!-- Improvements -->

                            <div class="section">

                                <div class="section-title">
                                    🟢 مؤشرات التحسن
                                </div>

                                %s

                            </div>

                            <!-- Concerns -->

                            <div class="section">

                                <div class="section-title">
                                    🟠 مؤشرات تحتاج متابعة
                                </div>

                                %s

                            </div>

                            <!-- Recommendations -->

                            <div class="section">

                                <div class="section-title">
                                    💡 توصيات الذكاء الاصطناعي
                                </div>

                                %s

                            </div>

                            <div class="comparison">

                                <strong>
                                    تاريخ التقييم السابق:
                                </strong>

                                %s

                                <br><br>

                                <strong>
                                    تاريخ التقييم الحالي:
                                </strong>

                                %s

                            </div>

                        </div>

                        <div class="footer">

                            هذا التقرير مقدم من منصة رفيق
                            <br>
                            المعلومات الواردة للتوعية والمتابعة العامة
                            وليست تشخيصًا طبيًا.

                        </div>

                    </div>

                </body>

                </html>
                """.formatted(

                user.getFullName(),

                aiTrend.getOverallTrend(),

                formatMetric(
                        currentGlucose
                ),

                glucoseChange,

                formatMetric(
                        currentWeight
                ),

                weightChange,

                formatMetric(
                        currentHeartRate
                ),

                heartRateChange,

                previousBloodPressure,

                currentBloodPressure,

                buildListHtml(
                        aiTrend.getImprovements(),
                        "finding"
                ),

                buildListHtml(
                        aiTrend.getConcerns(),
                        "finding"
                ),

                buildListHtml(
                        aiTrend.getRecommendations(),
                        "recommendation"
                ),

                previous.getAssessmentDate(),

                current.getAssessmentDate()
        );
    }

    // Convert JSON string into JsonNode
    private JsonNode readJson(String json) {

        try {

            if (json == null || json.isBlank()) {
                return objectMapper.createObjectNode();
            }

            return objectMapper.readTree(json);

        } catch (Exception e) {

            return objectMapper.createObjectNode();
        }
    }

    // Get a numeric value from JSON
    private double getDouble(
            JsonNode node,
            String field) {

        if (node.has(field)
                && node.get(field).isNumber()) {

            return node.get(field).asDouble();
        }

        return 0;
    }

    // Get a text value from JSON
    private String getText(
            JsonNode node,
            String field) {

        if (node.has(field)) {
            return node.get(field).asText();
        }

        return "-";
    }

    // Calculate percentage change
    private String calculateChange(
            double previous,
            double current) {

        if (previous == 0) {
            return "-";
        }

        double percentage =
                ((current - previous) / previous) * 100;

        String arrow =
                percentage > 0 ? "↑" :
                        percentage < 0 ? "↓" : "→";

        return String.format(
                Locale.US,
                "%s %.1f%%",
                arrow,
                Math.abs(percentage)
        );
    }

    // Format metric values
    private String formatMetric(double value) {

        if (value == 0) {
            return "-";
        }

        return String.format(
                Locale.US,
                "%.1f",
                value
        );
    }

    // Build HTML list
    private String buildListHtml(
            java.util.List<String> items,
            String cssClass) {

        if (items == null || items.isEmpty()) {

            return """
                    <div class="finding">
                        لا توجد معلومات متاحة حاليًا.
                    </div>
                    """;
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
}