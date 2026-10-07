package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OpenAIService {

    private final OpenAIClient openAIClient;

    // ================ 1 =====================

    // ObjectMapper converts the JSON response from OpenAI
    // into AIHealthSummaryDTO.
    private final ObjectMapper objectMapper;

    // Generate a structured Arabic health summary using OpenAI
    public AIHealthSummaryDTO generateHealthSummary(String healthData) {

        String prompt = """
                أنت مساعد صحي ذكي في نظام Rafeeq.

                قم بتحليل البيانات الصحية التالية.

                أريد النتيجة بصيغة JSON فقط، بدون Markdown وبدون ```.

                يجب أن يكون JSON بهذا الشكل بالضبط:

                {
                  "summary": "ملخص عام للحالة",
                  "keyFindings": [
                    "أهم مؤشر أو نتيجة",
                    "أهم مؤشر أو نتيجة"
                  ],
                  "followUp": [
                    "النقطة التي تحتاج متابعة",
                    "النقطة التي تحتاج متابعة"
                  ],
                  "recommendations": [
                    "توصية عامة",
                    "توصية عامة"
                  ]
                }

                التعليمات:
                - استخدم اللغة العربية.
                - لا تقدم تشخيصًا طبيًا.
                - لا تطلب إيقاف أو تغيير أي دواء.
                - قدم معلومات وتوصيات عامة فقط.
                - لا تضف أي حقول أخرى.
                - أعد JSON صالحًا فقط.

                البيانات الصحية:
                """ + healthData;

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(prompt)
                .model(ChatModel.GPT_5_2)
                .build();

        // Send the request to OpenAI
        Response response = openAIClient.responses().create(params);

        // Extract the generated text
        String result = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .findFirst()
                .orElseThrow(() ->
                        new ApiException("OpenAI did not return a result"));

        try {

            // Convert OpenAI JSON response into our DTO
            return objectMapper.readValue(
                    result,
                    AIHealthSummaryDTO.class
            );

        } catch (Exception e) {

            // Handle invalid JSON returned by AI
            throw new ApiException(
                    "Failed to process OpenAI response"
            );
        }
    }


    // ================ 2 ====================
    // Generate health trend analysis by comparing two assessments
    public AIHealthTrendDTO generateHealthTrend(String currentAssessment, String previousAssessment) {

        String prompt = """
            أنت مساعد صحي ذكي في نظام Rafeeq.

            قم بمقارنة التقييم الصحي الحالي مع التقييم الصحي السابق.

            أريد النتيجة بصيغة JSON فقط، بدون Markdown وبدون ```.

            يجب أن يكون JSON بهذا الشكل بالضبط:

            {
              "overallTrend": "الاتجاه الصحي العام",
              "improvements": [
                "مؤشر صحي تحسن"
              ],
              "concerns": [
                "مؤشر صحي يحتاج متابعة"
              ],
              "recommendations": [
                "توصية عامة"
              ]
            }

            التعليمات:
            - استخدم اللغة العربية.
            - قارن التقييم الحالي بالتقييم السابق.
            - حدد المؤشرات التي تحسنت.
            - حدد المؤشرات التي تراجعت أو تحتاج متابعة.
            - قدم توصيات عامة فقط.
            - لا تقدم تشخيصًا طبيًا.
            - لا تطلب إيقاف أو تغيير أي دواء.
            - لا تضف أي حقول أخرى.
            - أعد JSON صالحًا فقط.

            التقييم الصحي السابق:
            %s

            التقييم الصحي الحالي:
            %s
            """.formatted(previousAssessment, currentAssessment);

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(prompt)
                .model(ChatModel.GPT_5_2)
                .build();

        // Send the comparison request to OpenAI
        Response response = openAIClient.responses().create(params);

        // Extract the generated text from OpenAI
        String result = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .findFirst()
                .orElseThrow(() ->
                        new ApiException("OpenAI did not return a result"));

        try {

            // Convert OpenAI JSON response into AIHealthTrendDTO
            return objectMapper.readValue(
                    result,
                    AIHealthTrendDTO.class
            );

        } catch (Exception e) {

            throw new ApiException("Failed to process OpenAI health trend response");
        }
    }


    // ==================== 3============

    // Generate AI analysis for the user's medication schedule
    public AIMedicationAnalysisDTO generateMedicationAnalysis(
            String medicationData) {

        String prompt = """
            أنت مساعد صحي ذكي في نظام Rafeeq.

            قم بتحليل جدول الأدوية التالي للمستخدم.

            أريد النتيجة بصيغة JSON فقط، بدون Markdown وبدون ```.

            يجب أن يكون JSON بهذا الشكل بالضبط:

            {
              "summary": "ملخص عام لجدول الأدوية",
              "medications": [
                {
                  "name": "اسم الدواء",
                  "dosage": "الجرعة",
                  "mealRelation": "علاقة الدواء بالطعام",
                  "times": "أوقات الدواء",
                  "generalNote": "ملاحظة عامة"
                }
              ],
              "followUp": [
                "نقطة تحتاج متابعة"
              ],
              "recommendations": [
                "توصية عامة"
              ]
            }

            التعليمات:
            - استخدم اللغة العربية.
            - حلل تنظيم وجدول الأدوية فقط.
            - لا تقدم تشخيصًا طبيًا.
            - لا تطلب تغيير أو إيقاف أي دواء.
            - لا تقترح جرعات جديدة.
            - لا تفترض معلومات غير موجودة في البيانات.
            - إذا كانت معلومة غير موجودة، لا تخترعها.
            - قدم توصيات عامة وآمنة فقط.
            - لا تضف أي حقول أخرى.
            - أعد JSON صالحًا فقط.

            بيانات الأدوية:
            """ + medicationData;

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(prompt)
                .model(ChatModel.GPT_5_2)
                .build();

        // Send the medication data to OpenAI
        Response response =
                openAIClient.responses().create(params);

        // Extract the generated text from OpenAI
        String result = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .findFirst()
                .orElseThrow(() ->
                        new ApiException(
                                "OpenAI did not return a result"
                        ));

        try {

            // Convert OpenAI JSON response into our DTO
            return objectMapper.readValue(
                    result,
                    AIMedicationAnalysisDTO.class
            );

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to process OpenAI medication analysis"
            );
        }
    }

    // ========================= 4 =================
// Generate AI Medication Impact Report
    public AIMedicationImpactDTO generateMedicationImpactReport(String medicationData, String healthAssessmentData) {

        // Build prompt
        String prompt = """
            أنت مساعد صحي ذكي في نظام Rafeeq.

            قم بتحليل العلاقة الزمنية بين جدول الأدوية
            والتقييمات الصحية للمستخدم.

            أريد النتيجة بصيغة JSON فقط،
            بدون Markdown وبدون ```.

            يجب أن يكون JSON بهذا الشكل:

            {
              "summary": "ملخص عام",
              "medications": [
                {
                  "name": "اسم الدواء",
                  "startDate": "تاريخ البداية",
                  "endDate": "تاريخ النهاية",
                  "healthObservation": "الملاحظة الصحية",
                  "generalNote": "ملاحظة عامة"
                }
              ],
              "healthChanges": [
                "تغير صحي"
              ],
              "followUp": [
                "نقطة تحتاج متابعة"
              ],
              "recommendations": [
                "توصية عامة"
              ]
            }

            التعليمات:

            - استخدم اللغة العربية.
            - قارن التوقيت بين الأدوية والتقييمات الصحية.
            - اعتمد فقط على البيانات الموجودة.
            - لا تفترض معلومات غير موجودة.
            - لا تعتبر التحسن بعد تسجيل دواء دليلًا
              على أن الدواء تسبب في التحسن.
            - لا تعتبر التدهور بعد تسجيل دواء دليلًا
              على أن الدواء تسبب في التدهور.
            - استخدم عبارات مثل "لوحظ" و"ظهر تغير".
            - لا تقدم تشخيصًا طبيًا.
            - لا تطلب تغيير أو إيقاف أي دواء.
            - لا تقترح جرعات جديدة.
            - قدم توصيات عامة وآمنة فقط.
            - لا تضف أي حقول أخرى.
            - أعد JSON صالحًا فقط.

            بيانات الأدوية:

            %s

            بيانات التقييمات الصحية:

            %s
            """.formatted(
                medicationData,
                healthAssessmentData
        );

        // Create OpenAI request
        ResponseCreateParams params =
                ResponseCreateParams.builder()
                        .input(prompt)
                        .model(ChatModel.GPT_5_2)
                        .build();

        // Send request to OpenAI
        Response response =
                openAIClient.responses().create(params);

        // Get OpenAI response text
        String result = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .findFirst()
                .orElseThrow(() -> new ApiException("OpenAI did not return a result"));

        // Convert JSON response to DTO
        try {

            return objectMapper.readValue(result, AIMedicationImpactDTO.class);

        } catch (Exception e) {

            throw new ApiException("Failed to process OpenAI medication impact report");
        }
    }

    //..

    // Generate AI Health Progress Report
    public AIHealthProgressDTO generateHealthProgressReport(
            String healthAssessmentData) {

        // Build prompt
        String prompt = """
            أنت مساعد صحي ذكي في نظام Rafeeq.

            قم بتحليل تطور الحالة الصحية للمستخدم
            بالاعتماد فقط على التقييمات الصحية المسجلة
            والمرتبة زمنيًا.

            أريد النتيجة بصيغة JSON فقط،
            بدون Markdown وبدون ```.

            يجب أن يكون JSON بهذا الشكل:

            {
              "overallProgress": "وصف عام لمستوى التطور",
              "progressScore": 0,
              "summary": "ملخص عام للتطور الصحي",
              "improvements": [
                "نقطة تحسن"
              ],
              "stableIndicators": [
                "مؤشر مستقر"
              ],
              "areasToFollowUp": [
                "نقطة تحتاج متابعة"
              ],
              "recommendations": [
                "توصية عامة وآمنة"
              ]
            }

            التعليمات:

            - استخدم اللغة العربية.
            - اعتمد فقط على البيانات الموجودة.
            - قارن التقييمات الصحية السابقة والحالية.
            - رتب التحليل حسب التسلسل الزمني للتقييمات.
            - حدد التغيرات الإيجابية عندما تكون مدعومة بالبيانات.
            - حدد المؤشرات المستقرة عندما تكون البيانات تدعم ذلك.
            - حدد النقاط التي تحتاج متابعة.
            - progressScore يجب أن يكون رقمًا من 0 إلى 100.
            - progressScore هو مؤشر تحليلي مبني على البيانات
              وليس تقييمًا طبيًا أو تشخيصًا.
            - لا تعتبر أي تغير دليلًا مؤكدًا على سبب طبي.
            - لا تقدم تشخيصًا طبيًا.
            - لا تقترح تغيير أو إيقاف أي دواء.
            - لا تقترح جرعات جديدة.
            - لا تخترع أي بيانات غير موجودة.
            - إذا كانت البيانات غير كافية لتحديد التحسن،
              اذكر ذلك بوضوح.
            - استخدم عبارات مثل:
              "لوحظ"
              و"تشير البيانات المسجلة"
              و"ظهر تغير".
            - قدم توصيات عامة وآمنة فقط.
            - لا تضف أي حقول أخرى.
            - أعد JSON صالحًا فقط.

            التقييمات الصحية:

            %s
            """.formatted(
                healthAssessmentData
        );


        // OpenAI request
        ResponseCreateParams params =
                ResponseCreateParams.builder()
                        .input(prompt)
                        .model(ChatModel.GPT_5_2)
                        .build();


        // Send request to OpenAI
        Response response =
                openAIClient.responses().create(params);


        // Get AI response
        String result =
                response.output().stream()
                        .flatMap(item -> item.message().stream())
                        .flatMap(message -> message.content().stream())
                        .flatMap(content -> content.outputText().stream())
                        .map(outputText -> outputText.text())
                        .findFirst()
                        .orElseThrow(() ->
                                new ApiException(
                                        "OpenAI did not return a result"
                                )
                        );


        // Convert JSON to DTO
        try {

            return objectMapper.readValue(result, AIHealthProgressDTO.class);

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to process OpenAI health progress report"
            );
        }
    }
}