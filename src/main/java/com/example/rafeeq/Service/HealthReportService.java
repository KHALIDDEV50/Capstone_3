package com.example.rafeeq.Service;

import com.example.rafeeq.DTO.AIHealthSummaryDTO;
import com.example.rafeeq.Model.HealthAssessment;
import com.example.rafeeq.Model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HealthReportService {

    private final JavaMailSender mailSender;


    // Send the health report to the user's email
    public void sendHealthReport(
            HealthAssessment healthAssessment,
            AIHealthSummaryDTO aiSummary) {

        // Get the user who owns this health assessment
        User user = healthAssessment.getUser();

        // Create a new email message
        MimeMessage message = mailSender.createMimeMessage();

        try {

            // Helper is used to create an HTML email
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            // Email sender
            helper.setFrom("YOUR_EMAIL@gmail.com");

            // Email receiver
            helper.setTo(user.getEmail());

            // Email subject
            helper.setSubject("رفيق | التقرير الصحي الذكي");

            // Create the HTML content
            String htmlReport = buildHealthReportHtml(
                    healthAssessment,
                    user,
                    aiSummary
            );

            // true means the email content is HTML
            helper.setText(htmlReport, true);

            // Send the email
            mailSender.send(message);

        } catch (MessagingException e) {

            // Throw an error if the email cannot be created or sent
            throw new RuntimeException(
                    "Failed to send health report email"
            );
        }
    }


    // Build the Arabic HTML health report
    private String buildHealthReportHtml(
            HealthAssessment assessment,
            User user,
            AIHealthSummaryDTO aiSummary) {

        // Convert AI lists into HTML lists
        String keyFindingsHtml =
                buildListHtml(aiSummary.getKeyFindings());

        String followUpHtml =
                buildListHtml(aiSummary.getFollowUp());

        String recommendationsHtml =
                buildListHtml(aiSummary.getRecommendations());


        return """
                <!DOCTYPE html>

                <html lang="ar" dir="rtl">

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>التقرير الصحي - رفيق</title>


                    <style>

                        body {
                            margin: 0;
                            padding: 0;
                            background-color: #f4f7fb;
                            font-family: Arial, Tahoma, sans-serif;
                            direction: rtl;
                            color: #263238;
                        }


                        .container {
                            max-width: 720px;
                            margin: 30px auto;
                            background: #ffffff;
                            border-radius: 18px;
                            overflow: hidden;
                            box-shadow: 0 5px 25px rgba(0,0,0,0.08);
                        }


                        /* Header */
                        .header {
                            background: linear-gradient(
                                135deg,
                                #0f766e,
                                #0d9488
                            );

                            padding: 35px 30px;

                            text-align: center;

                            color: white;
                        }


                        .logo {
                            font-size: 34px;
                            font-weight: bold;
                            margin-bottom: 10px;
                        }


                        .header-title {
                            font-size: 23px;
                            margin: 0;
                        }


                        .header-subtitle {
                            font-size: 14px;
                            opacity: 0.9;
                            margin-top: 8px;
                        }


                        /* Main content */
                        .content {
                            padding: 30px;
                        }


                        .welcome {
                            font-size: 19px;
                            font-weight: bold;
                            margin-bottom: 25px;
                        }


                        /* Sections */
                        .section {
                            margin-bottom: 28px;
                        }


                        .section-title {
                            font-size: 18px;
                            font-weight: bold;
                            color: #0f766e;

                            border-right: 4px solid #0f766e;

                            padding-right: 10px;

                            margin-bottom: 15px;
                        }


                        /* Information Card */
                        .info-card {
                            background: #f8fafc;

                            border-radius: 12px;

                            padding: 18px;

                            border: 1px solid #e5e7eb;
                        }


                        .info-row {
                            padding: 10px 0;

                            border-bottom: 1px solid #e5e7eb;
                        }


                        .info-row:last-child {
                            border-bottom: none;
                        }


                        .label {
                            font-weight: bold;
                            color: #475569;
                        }


                        .value {
                            color: #1e293b;
                        }


                        /* AI Summary */
                        .summary-box {
                            background: #ecfdf5;

                            border: 1px solid #a7f3d0;

                            border-radius: 14px;

                            padding: 20px;

                            line-height: 1.9;

                            font-size: 15px;
                        }


                        /* Findings */
                        .finding-box {
                            background: #f8fafc;

                            border: 1px solid #e5e7eb;

                            border-radius: 14px;

                            padding: 18px;
                        }


                        .finding-item {
                            padding: 10px 0;

                            border-bottom: 1px solid #e5e7eb;

                            line-height: 1.7;
                        }


                        .finding-item:last-child {
                            border-bottom: none;
                        }


                        /* Follow Up */
                        .followup-box {
                            background: #fff7ed;

                            border: 1px solid #fed7aa;

                            border-radius: 14px;

                            padding: 18px;
                        }


                        /* Recommendations */
                        .recommendation-box {
                            background: #eff6ff;

                            border: 1px solid #bfdbfe;

                            border-radius: 14px;

                            padding: 18px;
                        }


                        /* Date */
                        .date-box {
                            background: #eff6ff;

                            border: 1px solid #bfdbfe;

                            border-radius: 14px;

                            padding: 20px;

                            text-align: center;
                        }


                        .date-label {
                            font-size: 14px;

                            color: #475569;
                        }


                        .date-value {
                            font-size: 22px;

                            font-weight: bold;

                            color: #2563eb;

                            margin-top: 8px;
                        }


                        /* Medical Notice */
                        .notice {
                            background: #fff7ed;

                            border: 1px solid #fed7aa;

                            border-radius: 12px;

                            padding: 15px;

                            color: #9a3412;

                            font-size: 13px;

                            line-height: 1.8;
                        }


                        /* Footer */
                        .footer {
                            background: #f8fafc;

                            padding: 25px;

                            text-align: center;

                            color: #64748b;

                            font-size: 13px;
                        }


                        .footer-logo {
                            font-size: 20px;

                            font-weight: bold;

                            color: #0f766e;

                            margin-bottom: 8px;
                        }


                        /* Mobile */
                        @media screen and (max-width: 600px) {

                            .container {
                                margin: 10px;

                                border-radius: 12px;
                            }


                            .content {
                                padding: 20px;
                            }


                            .header {
                                padding: 28px 20px;
                            }


                            .logo {
                                font-size: 28px;
                            }


                            .header-title {
                                font-size: 19px;
                            }

                        }

                    </style>

                </head>


                <body>

                    <div class="container">


                        <!-- Header -->

                        <div class="header">

                            <div class="logo">
                                رفيق
                            </div>


                            <h1 class="header-title">
                                التقرير الصحي الذكي
                            </h1>


                            <div class="header-subtitle">
                                تقرير تم إنشاؤه باستخدام الذكاء الاصطناعي
                            </div>

                        </div>



                        <div class="content">


                            <!-- Welcome -->

                            <div class="welcome">

                                مرحبًا %s 👋

                            </div>



                            <!-- Assessment Information -->

                            <div class="section">

                                <div class="section-title">
                                    بيانات التقييم
                                </div>


                                <div class="info-card">


                                    <div class="info-row">

                                        <span class="label">
                                            تاريخ التقييم:
                                        </span>

                                        <span class="value">
                                            %s
                                        </span>

                                    </div>


                                    <div class="info-row">

                                        <span class="label">
                                            حالة التقييم:
                                        </span>

                                        <span class="value">
                                            %s
                                        </span>

                                    </div>


                                    <div class="info-row">

                                        <span class="label">
                                            الاتجاه الصحي:
                                        </span>

                                        <span class="value">
                                            %s
                                        </span>

                                    </div>


                                </div>

                            </div>



                            <!-- AI Summary -->

                            <div class="section">

                                <div class="section-title">
                                    الملخص الصحي
                                </div>


                                <div class="summary-box">

                                    %s

                                </div>

                            </div>



                            <!-- Key Findings -->

                            <div class="section">

                                <div class="section-title">
                                    أهم المؤشرات والنتائج
                                </div>


                                <div class="finding-box">

                                    %s

                                </div>

                            </div>



                            <!-- Follow Up -->

                            <div class="section">

                                <div class="section-title">
                                    نقاط تحتاج إلى متابعة
                                </div>


                                <div class="followup-box">

                                    %s

                                </div>

                            </div>



                            <!-- Recommendations -->

                            <div class="section">

                                <div class="section-title">
                                    التوصيات العامة
                                </div>


                                <div class="recommendation-box">

                                    %s

                                </div>

                            </div>



                            <!-- Next Follow Up -->

                            <div class="section">

                                <div class="section-title">
                                    موعد المتابعة القادمة
                                </div>


                                <div class="date-box">

                                    <div class="date-label">
                                        الموعد المحدد في النظام
                                    </div>


                                    <div class="date-value">

                                        %s

                                    </div>

                                </div>

                            </div>



                            <!-- Medical Notice -->

                            <div class="section">

                                <div class="notice">

                                    <strong>
                                        تنبيه مهم:
                                    </strong>

                                    <br><br>

                                    هذا التقرير يقدم معلومات وملخصات عامة
                                    بناءً على البيانات المدخلة في النظام،
                                    ولا يُعد تشخيصًا طبيًا أو بديلًا عن
                                    استشارة الطبيب أو مقدم الرعاية الصحية.

                                </div>

                            </div>


                        </div>



                        <!-- Footer -->

                        <div class="footer">

                            <div class="footer-logo">
                                رفيق | Rafeeq
                            </div>


                            <div>
                                رفيقك الذكي لمتابعة صحتك
                            </div>


                            <div style="margin-top: 10px;">
                                © 2026 Rafeeq
                            </div>

                        </div>


                    </div>

                </body>

                </html>

                """.formatted(
                user.getFullName(),
                assessment.getAssessmentDate(),
                assessment.getIsCurrent() ? "حالي" : "سابق",
                assessment.getTrend(),

                aiSummary.getSummary(),

                keyFindingsHtml,

                followUpHtml,

                recommendationsHtml,

                assessment.getNextDueDate()
        );
    }


    // Convert a list of AI results into HTML list items
    private String buildListHtml(List<String> items) {

        if (items == null || items.isEmpty()) {

            return """
                    <div class="finding-item">
                        لا توجد معلومات متاحة حاليًا.
                    </div>
                    """;
        }

        StringBuilder html = new StringBuilder();

        for (String item : items) {

            html.append("""
                    <div class="finding-item">
                        • %s
                    </div>
                    """.formatted(item));
        }

        return html.toString();
    }
}