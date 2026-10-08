# 🩺 Rafeeq – AI Healthcare Platform

**Rafeeq** is an AI-powered healthcare platform built with **Java & Spring Boot**.

The platform helps users manage their **Health Assessments** and **Medication Schedules**, with AI-powered analysis and smart health reports using **OpenAI**.

---

## 🚀 Technologies

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Jakarta Validation
- Lombok
- OpenAI API
- JavaMailSender
- REST API
- HTML / CSS
- Postman

----
Health Assessment – CRUD
GET/health-assessment/get

GET/health-assessment/get/{id}

GET/health-assessment/user/{userId}

GET/health-assessment/user/{userId}/current

POST/health-assessment/add

PUT/health-assessment/update/{id}

DELETE/health-assessment/delete/{id}

---
Medication Schedule – CRUD

GET/medication-schedule/get

GET/medication-schedule/get/{id}

GET/medication-schedule/user/{userId}

GET/medication-schedule/user/{userId}/active

POST/medication-schedule/add

PUT/medication-schedule/update/{id}

DELETE/medication-schedule/delete/{id}


----
Extra Points endpoints

1 - AI Summary : AI-powered health assessment analysis and smart summary of key findings, follow-ups, and recommendations.

/health-assessment/ai-summary/{assessmentId}

2 - Health Assessment Email : Generate and email an HTML health assessment report to the user.

/health-assessment/report/email/{assessmentId}

3 - AI Report + Email : Generate the AI health report and email it directly to the user.

/health-assessment/report/email/{assessmentId}

4 - AI Health Trend : Compare current and previous assessments to analyze Health Trends, improvements, and changes.

/health-assessment/health-trend/{assessmentId}

5 - Health Trend Email : Generate an AI Health Trend analysis, create an HTML report, and email it to the user.

/health-assessment/health-trend/email/{assessmentId}

6 - AI Medication Analysis : Analyze the medication schedule using AI, including dosages, usage times, and meal relationships.

/medication-schedule/ai-analysis/{userId}

7 - Medication Analysis Email : Generate an AI medication analysis, create an HTML report, and email it to the user.

/medication-schedule/ai-analysis/email/{userId}

8 - AI Medication Impact : Analyze the temporal relationship between Medication Schedules and Health Assessments to identify health changes.

/medication-schedule/ai-medication-impact/{userId}

9 - Medication Impact HTML : Generate an AI Medication Impact Report and convert it into a formatted, presentation-ready HTML report.

/medication-schedule/ai-medication-impact/html/{userId}

10 - AI Health Progress : Analyze all Health Assessments over time to identify health progress, improvements, and stable indicators.

/health-assessment/health-progress/{userId}

11 -Health Progress HTML : Convert the AI Health Progress analysis into a professional, customized HTML Health Dashboard.

/health-assessment/health-progress/html/{userId}


# 🔐 AI Safety.
AI Safety Guidelines:
- Uses only recorded user data.
- Does not provide medical diagnoses.
- Does not recommend stopping or changing medications.
- Does not suggest new dosages.
- Does not invent missing information.
- Distinguishes temporal changes from medical causation.
- Provides general and safe recommendations.




