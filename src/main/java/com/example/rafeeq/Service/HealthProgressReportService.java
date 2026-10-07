package com.example.rafeeq.Service;

import com.example.rafeeq.DTO.AIHealthProgressDTO;
import com.example.rafeeq.Model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthProgressReportService {


    // =========================================================
    // Build AI Health Progress HTML Report
    // =========================================================

    public String buildHealthProgressHtml(
            User user,
            AIHealthProgressDTO progressReport) {


        // =====================================================
        // Gender Based Greeting
        // =====================================================

        String greeting;

        if ("FEMALE".equalsIgnoreCase(user.getGender())) {

            greeting = "مرحبًا بكِ";

        } else {

            greeting = "مرحبًا بك";

        }


        // =====================================================
        // Progress Score
        // =====================================================

        Integer progressScore =
                progressReport.getProgressScore();

        if (progressScore == null) {
            progressScore = 0;
        }


        // Make sure score stays between 0 and 100
        progressScore =
                Math.max(
                        0,
                        Math.min(
                                progressScore,
                                100
                        )
                );


        // =====================================================
        // Build Health Snapshot Cards
        // =====================================================

        String improvements =
                buildSnapshotCards(
                        progressReport.getImprovements(),
                        "improvement",
                        "📈",
                        "نقاط التحسن"
                );


        String stableIndicators =
                buildSnapshotCards(
                        progressReport.getStableIndicators(),
                        "stable",
                        "🟢",
                        "مؤشرات مستقرة"
                );


        String followUp =
                buildSnapshotCards(
                        progressReport.getAreasToFollowUp(),
                        "follow-up",
                        "🟠",
                        "تحتاج متابعة"
                );


        // =====================================================
        // Recommendations
        // =====================================================

        String recommendations =
                buildListHtml(
                        progressReport.getRecommendations(),
                        "recommendation"
                );


        // =====================================================
        // HTML
        // =====================================================

        String html = """
                <!DOCTYPE html>

                <html lang="ar" dir="rtl">

                <head>

                    <meta charset="UTF-8">

                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>
                        رفيق | تقرير التطور الصحي
                    </title>


                    <style>

                        * {
                            box-sizing: border-box;
                        }


                        body {

                            margin: 0;

                            padding: 0;

                            background: #f4f6f8;

                            font-family:
                                Arial,
                                "Tahoma",
                                sans-serif;

                            color: #263238;

                        }


                        .wrapper {

                            width: 100%;

                            padding: 30px 15px;

                        }


                        .container {

                            max-width: 900px;

                            margin: auto;

                            background: #ffffff;

                            border-radius: 24px;

                            overflow: hidden;

                            box-shadow:
                                0 10px 35px
                                rgba(0, 0, 0, 0.08);

                        }


                        /* =================================================
                           HEADER
                           ================================================= */

                        .header {

                            background:
                                linear-gradient(
                                    135deg,
                                    #16324f,
                                    #24577a
                                );

                            color: white;

                            padding: 32px 35px;

                        }


                        .header-top {

                            display: flex;

                            justify-content: space-between;

                            align-items: center;

                            gap: 20px;

                        }


                        .brand {

                            font-size: 25px;

                            font-weight: bold;

                            letter-spacing: 1px;

                        }


                        .brand-icon {

                            display: inline-block;

                            margin-left: 8px;

                        }


                        .report-label {

                            font-size: 13px;

                            background:
                                rgba(
                                    255,
                                    255,
                                    255,
                                    0.15
                                );

                            padding:
                                8px 14px;

                            border-radius: 20px;

                        }


                        .header-content {

                            margin-top: 30px;

                        }


                        .header-content h1 {

                            margin: 0;

                            font-size: 30px;

                        }


                        .header-content p {

                            margin-top: 10px;

                            margin-bottom: 0;

                            opacity: 0.9;

                            font-size: 15px;

                            line-height: 1.8;

                        }


                        /* =================================================
                           CONTENT
                           ================================================= */

                        .content {

                            padding: 30px;

                        }


                        .welcome {

                            margin-bottom: 25px;

                            font-size: 19px;

                        }


                        .welcome strong {

                            color: #16324f;

                        }


                        /* =================================================
                           TOP DASHBOARD
                           ================================================= */

                        .dashboard-grid {

                            display: grid;

                            grid-template-columns:
                                0.9fr
                                1.5fr;

                            gap: 20px;

                            margin-bottom: 30px;

                        }


                        .card {

                            background: #ffffff;

                            border:
                                1px solid #e8ecef;

                            border-radius: 18px;

                            padding: 25px;

                        }


                        .card-title {

                            font-size: 18px;

                            font-weight: bold;

                            margin-bottom: 8px;

                        }


                        .card-description {

                            color: #78909c;

                            font-size: 13px;

                            line-height: 1.7;

                        }


                        /* =================================================
                           SCORE CARD
                           ================================================= */

                        .score-card {

                            text-align: center;

                            background:
                                linear-gradient(
                                    180deg,
                                    #ffffff,
                                    #f8fbfa
                                );

                        }


                        .score-title {

                            font-size: 17px;

                            font-weight: bold;

                        }


                        .score-description {

                            color: #78909c;

                            font-size: 12px;

                            margin-top: 8px;

                            line-height: 1.6;

                        }


                        .score-ring {

                            width: 170px;

                            height: 170px;

                            margin:
                                25px auto 15px;

                            border-radius: 50%;

                            background:
                                conic-gradient(
                                    #f5b700
                                    0deg,
                                    #f5b700
                                    calc(
                                        var(--score)
                                        * 3.6deg
                                    ),
                                    #edf0f1
                                    calc(
                                        var(--score)
                                        * 3.6deg
                                    ),
                                    #edf0f1
                                    360deg
                                );

                            display: flex;

                            align-items: center;

                            justify-content: center;

                        }


                        .score-inner {

                            width: 130px;

                            height: 130px;

                            border-radius: 50%;

                            background: #ffffff;

                            display: flex;

                            flex-direction: column;

                            align-items: center;

                            justify-content: center;

                        }


                        .score-number {

                            font-size: 40px;

                            font-weight: bold;

                            color: #e0a800;

                        }


                        .score-percent {

                            font-size: 13px;

                            color: #78909c;

                        }


                        .score-status {

                            font-size: 18px;

                            font-weight: bold;

                            color: #263238;

                            margin-top: 10px;

                        }


                        /* =================================================
                           PROGRESS CARD
                           ================================================= */

                        .progress-card {

                            background:
                                #fafbfc;

                        }


                        .progress-header {

                            display: flex;

                            justify-content:
                                space-between;

                            align-items: center;

                            margin-bottom: 20px;

                        }


                        .progress-value {

                            font-size: 26px;

                            font-weight: bold;

                            color: #16324f;

                        }


                        .progress-bar {

                            width: 100%;

                            height: 13px;

                            background: #e8ecef;

                            border-radius: 20px;

                            overflow: hidden;

                        }


                        .progress-fill {

                            height: 100%;

                            background:
                                linear-gradient(
                                    90deg,
                                    #2f80ed,
                                    #f5b700,
                                    #e76f51
                                );

                            border-radius: 20px;

                        }


                        .progress-scale {

                            display: flex;

                            justify-content:
                                space-between;

                            margin-top: 9px;

                            font-size: 11px;

                            color: #90a4ae;

                        }


                        .ai-summary {

                            margin-top: 25px;

                            background:
                                #f5f8fa;

                            border-radius: 14px;

                            padding: 18px;

                            line-height: 1.9;

                        }


                        .ai-summary-title {

                            font-weight: bold;

                            margin-bottom: 8px;

                        }


                        /* =================================================
                           HEALTH SNAPSHOT
                           ================================================= */

                        .section-title-main {

                            font-size: 23px;

                            font-weight: bold;

                            margin-top: 10px;

                            margin-bottom: 18px;

                        }


                        .snapshot-grid {

                            display: grid;

                            grid-template-columns:
                                repeat(
                                    3,
                                    1fr
                                );

                            gap: 15px;

                        }


                        .snapshot-card {

                            border-radius: 17px;

                            padding: 20px;

                            min-height: 150px;

                            position: relative;

                            overflow: hidden;

                        }


                        .snapshot-card.improvement {

                            background:
                                #f0f8f4;

                            border:
                                1px solid #d9eee4;

                        }


                        .snapshot-card.stable {

                            background:
                                #f3f7fb;

                            border:
                                1px solid #e1eaf2;

                        }


                        .snapshot-card.follow-up {

                            background:
                                #fff8ed;

                            border:
                                1px solid #f5e2bd;

                        }


                        .snapshot-number {

                            display: inline-block;

                            background:
                                #2f80ed;

                            color: white;

                            border-radius: 20px;

                            padding:
                                5px 10px;

                            font-size: 11px;

                            margin-bottom: 15px;

                        }


                        .snapshot-icon {

                            position: absolute;

                            left: 18px;

                            top: 18px;

                            font-size: 27px;

                        }


                        .snapshot-title {

                            font-size: 17px;

                            font-weight: bold;

                            margin-bottom: 10px;

                        }


                        .snapshot-item {

                            font-size: 13px;

                            line-height: 1.7;

                            color: #546e7a;

                        }


                        /* =================================================
                           FULL WIDTH SECTIONS
                           ================================================= */

                        .full-section {

                            margin-top: 30px;

                        }


                        .section-box {

                            border-radius: 17px;

                            padding: 20px;

                        }


                        .recommendations-box {

                            background:
                                #f1f8f5;

                            border:
                                1px solid #d9eee4;

                        }


                        .recommendation-item {

                            background:
                                #ffffff;

                            border-radius: 12px;

                            padding: 14px;

                            margin-bottom: 9px;

                            line-height: 1.7;

                        }


                        .recommendation-item:last-child {

                            margin-bottom: 0;

                        }


                        /* =================================================
                           FOOTER
                           ================================================= */

                        .footer {

                            background:
                                #f5f7f9;

                            padding: 25px;

                            text-align: center;

                            color: #78909c;

                            font-size: 12px;

                            line-height: 1.9;

                        }


                        /* =================================================
                           MOBILE
                           ================================================= */

                        @media only screen
                        and (max-width: 700px) {

                            .dashboard-grid {

                                grid-template-columns: 1fr;

                            }


                            .snapshot-grid {

                                grid-template-columns: 1fr;

                            }


                            .header-top {

                                display: block;

                            }


                            .report-label {

                                display: inline-block;

                                margin-top: 12px;

                            }


                            .content {

                                padding: 20px;

                            }

                        }

                    </style>

                </head>


                <body>

                    <div class="wrapper">

                        <div class="container">


                            <!-- =================================================
                                 HEADER
                                 ================================================= -->

                            <div class="header">

                                <div class="header-top">

                                    <div class="brand">

                                        <span class="brand-icon">
                                            🩺
                                        </span>

                                        RAFEEQ

                                    </div>


                                    <div class="report-label">

                                        AI Health Progress Report

                                    </div>

                                </div>


                                <div class="header-content">

                                    <h1>
                                        تقرير التطور الصحي
                                    </h1>

                                    <p>

                                        متابعة رحلتك الصحية
                                        وتحليل التغيرات المسجلة
                                        عبر التقييمات السابقة.

                                    </p>

                                </div>

                            </div>


                            <!-- =================================================
                                 CONTENT
                                 ================================================= -->

                            <div class="content">


                                <!-- Welcome -->

                                <div class="welcome">

                                    {{GREETING}}

                                    <strong>
                                        {{FULL_NAME}}
                                    </strong>

                                    👋

                                </div>


                                <!-- =================================================
                                     TOP DASHBOARD
                                     ================================================= -->

                                <div class="dashboard-grid">


                                    <!-- Overall Score -->

                                    <div class="card score-card">

                                        <div class="score-title">

                                            Overall Progress

                                        </div>

                                        <div class="score-description">

                                            مؤشر تحليلي عام
                                            للتطور الصحي

                                        </div>


                                        <div
                                            class="score-ring"
                                            style="--score: {{PROGRESS_SCORE}};"
                                        >

                                            <div class="score-inner">

                                                <div class="score-number">

                                                    {{PROGRESS_SCORE}}

                                                </div>

                                                <div class="score-percent">

                                                    من 100

                                                </div>

                                            </div>

                                        </div>


                                        <div class="score-status">

                                            {{OVERALL_PROGRESS}}

                                        </div>

                                    </div>


                                    <!-- Progress Analysis -->

                                    <div class="card progress-card">

                                        <div class="progress-header">

                                            <div>

                                                <div class="card-title">

                                                    مستوى التقدم

                                                </div>

                                                <div
                                                    class="card-description"
                                                >

                                                    تحليل مبني على
                                                    التقارير الصحية المسجلة

                                                </div>

                                            </div>


                                            <div class="progress-value">

                                                {{PROGRESS_SCORE}}%

                                            </div>

                                        </div>


                                        <div class="progress-bar">

                                            <div
                                                class="progress-fill"
                                                style="
                                                    width:
                                                    {{PROGRESS_SCORE}}%;
                                                "
                                            >
                                            </div>

                                        </div>


                                        <div class="progress-scale">

                                            <span>
                                                بداية المتابعة
                                            </span>

                                            <span>
                                                تطور
                                            </span>

                                            <span>
                                                100
                                            </span>

                                        </div>


                                        <!-- AI Summary -->

                                        <div class="ai-summary">

                                            <div
                                                class="ai-summary-title"
                                            >

                                                🤖 ملخص رفيق الذكي

                                            </div>

                                            <div>

                                                {{SUMMARY}}

                                            </div>

                                        </div>

                                    </div>

                                </div>


                                <!-- =================================================
                                     HEALTH SNAPSHOT
                                     ================================================= -->

                                <div class="section-title-main">

                                    Health Snapshot

                                </div>


                                <div class="snapshot-grid">


                                    <!-- Improvements -->

                                    <div
                                        class="
                                            snapshot-card
                                            improvement
                                        "
                                    >

                                        <div
                                            class="snapshot-number"
                                        >

                                            1 • تحسن

                                        </div>


                                        <div
                                            class="snapshot-icon"
                                        >

                                            📈

                                        </div>


                                        <div
                                            class="snapshot-title"
                                        >

                                            نقاط التحسن

                                        </div>


                                        {{IMPROVEMENTS}}

                                    </div>


                                    <!-- Stable -->

                                    <div
                                        class="
                                            snapshot-card
                                            stable
                                        "
                                    >

                                        <div
                                            class="snapshot-number"
                                        >

                                            2 • استقرار

                                        </div>


                                        <div
                                            class="snapshot-icon"
                                        >

                                            🟢

                                        </div>


                                        <div
                                            class="snapshot-title"
                                        >

                                            مؤشرات مستقرة

                                        </div>


                                        {{STABLE_INDICATORS}}

                                    </div>


                                    <!-- Follow Up -->

                                    <div
                                        class="
                                            snapshot-card
                                            follow-up
                                        "
                                    >

                                        <div
                                            class="snapshot-number"
                                        >

                                            3 • متابعة

                                        </div>


                                        <div
                                            class="snapshot-icon"
                                        >

                                            🟠

                                        </div>


                                        <div
                                            class="snapshot-title"
                                        >

                                            تحتاج متابعة

                                        </div>


                                        {{FOLLOW_UP}}

                                    </div>


                                </div>


                                <!-- =================================================
                                     RECOMMENDATIONS
                                     ================================================= -->

                                <div class="full-section">

                                    <div class="section-title-main">

                                        💡 توصيات رفيق

                                    </div>


                                    <div
                                        class="
                                            section-box
                                            recommendations-box
                                        "
                                    >

                                        {{RECOMMENDATIONS}}

                                    </div>

                                </div>


                            </div>


                            <!-- =================================================
                                 FOOTER
                                 ================================================= -->

                            <div class="footer">

                                <strong>
                                    🩺 رفيق | RAFEEQ
                                </strong>

                                <br>

                                هذا التقرير تم إنشاؤه
                                بالاعتماد على البيانات الصحية
                                المسجلة في النظام.

                                <br>

                                مؤشر التقدم هو مؤشر تحليلي
                                وليس تشخيصًا طبيًا.

                                <br>

                                لا تقم بتغيير أو إيقاف أي دواء
                                دون استشارة الطبيب.

                            </div>


                        </div>

                    </div>

                </body>

                </html>
                """;


        // =====================================================
        // Replace placeholders
        // =====================================================

        html = html.replace(
                "{{GREETING}}",
                greeting
        );

        html = html.replace(
                "{{FULL_NAME}}",
                user.getFullName()
        );

        html = html.replace(
                "{{PROGRESS_SCORE}}",
                String.valueOf(progressScore)
        );

        html = html.replace(
                "{{OVERALL_PROGRESS}}",
                progressReport.getOverallProgress()
        );

        html = html.replace(
                "{{SUMMARY}}",
                progressReport.getSummary()
        );

        html = html.replace(
                "{{IMPROVEMENTS}}",
                improvements
        );

        html = html.replace(
                "{{STABLE_INDICATORS}}",
                stableIndicators
        );

        html = html.replace(
                "{{FOLLOW_UP}}",
                followUp
        );

        html = html.replace(
                "{{RECOMMENDATIONS}}",
                recommendations
        );


        // Return HTML
        return html;
    }


    // =========================================================
    // Build Snapshot Cards
    // =========================================================

    private String buildSnapshotCards(
            List<String> items,
            String cssClass,
            String icon,
            String title) {


        // No data
        if (items == null
                || items.isEmpty()) {

            return """
                    <div class="snapshot-item">

                        لا توجد معلومات مسجلة حاليًا.

                    </div>
                    """;
        }


        StringBuilder html =
                new StringBuilder();


        // Display maximum 3 items
        int maxItems =
                Math.min(
                        items.size(),
                        3
                );


        for (int i = 0; i < maxItems; i++) {

            String item =
                    items.get(i);


            html.append(

                    """
                    <div class="snapshot-item">

                        %s

                        %s

                    </div>
                    """.formatted(
                            icon,
                            item
                    )
            );
        }


        // If there are more items
        if (items.size() > 3) {

            html.append(

                    """
                    <div
                        class="snapshot-item"
                        style="
                            margin-top: 8px;
                            font-weight: bold;
                        "
                    >

                        + %s نقاط أخرى

                    </div>
                    """.formatted(
                            items.size() - 3
                    )
            );
        }


        return html.toString();
    }


    // =========================================================
    // Build Recommendations
    // =========================================================

    private String buildListHtml(
            List<String> items,
            String cssClass) {


        if (items == null
                || items.isEmpty()) {

            return """
                    <div class="recommendation-item">

                        لا توجد توصيات متاحة حاليًا.

                    </div>
                    """;
        }


        StringBuilder html =
                new StringBuilder();


        for (String item : items) {

            html.append(

                    """
                    <div class="recommendation-item">

                        💡 %s

                    </div>
                    """.formatted(item)

            );
        }


        return html.toString();
    }
}