package com.example.rafeeq.Service;

import com.example.rafeeq.DTO.AIMedicationImpactDTO;
import com.example.rafeeq.Model.HealthAssessment;
import com.example.rafeeq.Model.MedicationSchedule;
import com.example.rafeeq.Model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicationImpactReportService {

    public String buildMedicationImpactHtml(
            User user,
            List<MedicationSchedule> medications,
            List<HealthAssessment> assessments,
            AIMedicationImpactDTO aiReport) {

        String medicationCards =
                buildMedicationCards(aiReport);

        String healthChanges =
                buildListHtml(
                        aiReport.getHealthChanges(),
                        "health-change"
                );

        String followUp =
                buildListHtml(
                        aiReport.getFollowUp(),
                        "follow-up"
                );

        String recommendations =
                buildListHtml(
                        aiReport.getRecommendations(),
                        "recommendation"
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
                            max-width: 750px;
                            margin: 30px auto;
                            background: white;
                            border-radius: 18px;
                            overflow: hidden;
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
                        }

                        .content {
                            padding: 25px;
                        }

                        .summary {
                            background: #eef3f8;
                            border-radius: 14px;
                            padding: 20px;
                            margin: 20px 0;
                            line-height: 1.8;
                        }

                        .section {
                            margin-top: 28px;
                        }

                        .section-title {
                            font-size: 20px;
                            font-weight: bold;
                            margin-bottom: 15px;
                        }

                        .medication-card {
                            background: #f8fafc;
                            border-radius: 14px;
                            padding: 18px;
                            margin-bottom: 15px;
                        }

                        .medication-name {
                            font-size: 19px;
                            font-weight: bold;
                            margin-bottom: 12px;
                        }

                        .row {
                            padding: 6px 0;
                        }

                        .observation {
                            background: #fff8e1;
                            border-radius: 10px;
                            padding: 14px;
                            margin-top: 12px;
                            line-height: 1.7;
                        }

                        .ai-note {
                            background: #eef3f8;
                            border-radius: 10px;
                            padding: 14px;
                            margin-top: 10px;
                            line-height: 1.7;
                        }

                        .health-change {
                            background: #f1f8e9;
                            border-radius: 10px;
                            padding: 13px;
                            margin-bottom: 8px;
                        }

                        .follow-up {
                            background: #fff3e0;
                            border-radius: 10px;
                            padding: 13px;
                            margin-bottom: 8px;
                        }

                        .recommendation {
                            background: #e8f5e9;
                            border-radius: 10px;
                            padding: 13px;
                            margin-bottom: 8px;
                        }

                        .footer {
                            text-align: center;
                            background: #f1f4f7;
                            padding: 20px;
                            color: #78909c;
                            font-size: 12px;
                            line-height: 1.8;
                        }

                    </style>
                </head>

                <body>

                    <div class="container">

                        <div class="header">
                            <h1>💊 رفيق | تحليل تأثير الأدوية</h1>

                            <p>
                                تحليل العلاقة الزمنية بين الأدوية
                                والتقييمات الصحية
                            </p>
                        </div>

                        <div class="content">

                            <p>
                                مرحبًا
                                <strong>%s</strong>
                            </p>

                            <div class="summary">

                                <strong>
                                    🤖 الملخص الذكي
                                </strong>

                                <p>
                                    %s
                                </p>

                            </div>

                            <div class="section">

                                <div class="section-title">
                                    💊 تحليل الأدوية
                                </div>

                                %s

                            </div>

                            <div class="section">

                                <div class="section-title">
                                    📈 التغيرات الصحية
                                </div>

                                %s

                            </div>

                            <div class="section">

                                <div class="section-title">
                                    🟠 نقاط تحتاج متابعة
                                </div>

                                %s

                            </div>

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

                            التقرير يعتمد على البيانات الصحية
                            المسجلة في النظام.

                            <br>

                            العلاقة الزمنية لا تعني بالضرورة
                            وجود علاقة سببية بين الدواء والتغير الصحي.

                            <br><br>

                            لا تقم بتغيير أو إيقاف أي دواء
                            دون استشارة الطبيب.

                        </div>

                    </div>

                </body>

                </html>
                """.formatted(
                user.getFullName(),
                aiReport.getSummary(),
                medicationCards,
                healthChanges,
                followUp,
                recommendations
        );
    }


    private String buildMedicationCards(
            AIMedicationImpactDTO aiReport) {

        if (aiReport.getMedications() == null
                || aiReport.getMedications().isEmpty()) {

            return """
                    <div class="medication-card">
                        لا توجد معلومات متاحة حاليًا.
                    </div>
                    """;
        }

        StringBuilder html =
                new StringBuilder();

        for (AIMedicationImpactDTO.MedicationImpact medication
                : aiReport.getMedications()) {

            html.append(
                    """
                    <div class="medication-card">

                        <div class="medication-name">
                            💊 %s
                        </div>

                        <div class="row">
                            <strong>تاريخ البداية:</strong>
                            %s
                        </div>

                        <div class="row">
                            <strong>تاريخ النهاية:</strong>
                            %s
                        </div>

                        <div class="observation">

                            <strong>
                                📊 الملاحظة الصحية:
                            </strong>

                            <br>

                            %s

                        </div>

                        <div class="ai-note">

                            <strong>
                                🤖 ملاحظة AI:
                            </strong>

                            <br>

                            %s

                        </div>

                    </div>
                    """.formatted(
                            medication.getName(),
                            medication.getStartDate(),
                            medication.getEndDate(),
                            medication.getHealthObservation(),
                            medication.getGeneralNote()
                    )
            );
        }

        return html.toString();
    }


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

            html.append(
                    """
                    <div class="%s">
                        • %s
                    </div>
                    """.formatted(
                            cssClass,
                            item
                    )
            );
        }

        return html.toString();
    }
}