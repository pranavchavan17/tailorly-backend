# 🚀 Tailorly Backend

> AI-Powered Resume Tailoring Platform built with Spring Boot, OpenAI, MongoDB, JWT Authentication, ATS Scoring, PDF/DOCX Generation, and Razorpay Subscription Integration.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green)
![JWT](https://img.shields.io/badge/JWT-Authentication-blue)
![OpenAI](https://img.shields.io/badge/OpenAI-GPT-black)
![Razorpay](https://img.shields.io/badge/Razorpay-Payment-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)

---

# 📌 Project Status

✅ Backend Completed

Implemented Features:

- JWT Authentication
- User Profile Management
- AI Resume Tailoring
- ATS Score Analysis
- Resume PDF Generation
- Resume DOCX Generation
- Razorpay Subscription Integration
- Free & Premium Usage Management
- Swagger / OpenAPI Documentation

---

# 📖 Overview

Tailorly Backend powers an AI-powered resume tailoring platform that helps job seekers optimize their resumes for specific job descriptions.

The backend securely manages authentication, AI resume generation, ATS analysis, payment processing, subscription management, and document generation through REST APIs consumed by the React frontend.

---

# ✨ Features

## 🔐 Authentication

- User Registration
- Secure Login
- JWT Authentication
- BCrypt Password Encryption
- Stateless Security
- Protected APIs

---

## 👤 User Profile

- View Profile
- Update Profile
- Change Password

---

## 🤖 AI Resume Tailoring

- Upload Resume (PDF/DOCX)
- Resume Text Extraction
- AI-powered Resume Tailoring
- Job Description Optimization
- Custom Prompt Support
- Structured Resume Response

---

## 📄 Resume Export

- PDF Resume Generation
- DOCX Resume Generation
- Resume Preview Support

---

## 📊 ATS Score Analysis

- Overall ATS Score
- Keyword Score
- Skills Score
- Experience Score
- Education Score
- Grammar Score
- Summary Score
- Formatting Score
- Missing Keywords Detection
- Improvement Recommendations

---

## 💳 Subscription & Payments

- Razorpay Monthly Subscription
- Free Tier (3 Resume Generations)
- Premium Unlimited Resume Tailoring
- Premium ATS Access
- Usage Tracking
- Secure Payment Verification

---

## 📚 API Documentation

- Swagger UI
- OpenAPI JSON

---

# 🏗 System Architecture

```
                 React Frontend
                       │
                 REST API (JWT)
                       │
                       ▼
            Spring Boot Backend
                       │
      ┌────────────────┼────────────────┐
      │                │                │
      ▼                ▼                ▼
  OpenAI API      Razorpay API     MongoDB Atlas
      │
      ▼
AI Resume Tailoring
ATS Score Generation
```

---

# 🛠 Tech Stack

## Backend

- Java 21
- Spring Boot 3.5
- Spring Security
- Spring Validation
- Spring Data MongoDB
- MongoDB Atlas
- JWT Authentication
- Maven

## AI

- OpenAI Java SDK

## Payment

- Razorpay

## Document Generation

- Apache PDFBox
- Apache POI
- OpenHTMLToPDF
- Thymeleaf

---

# 📂 Project Structure

```
src
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── renderer
├── security
├── service
├── util
└── resources
```

---

# 🔄 Application Flow

## AI Resume Tailoring

```
Upload Resume
      │
      ▼
Resume Parsing
      │
      ▼
OpenAI Resume Tailoring
      │
      ▼
Structured Resume
      │
 ┌────┴─────┐
 ▼          ▼
PDF       DOCX
```

---

## ATS Score Analysis

```
Upload Resume
      │
      ▼
Resume Parsing
      │
      ▼
OpenAI ATS Analysis
      │
      ▼
ATS Report
      │
      ├── Overall Score
      ├── Missing Keywords
      ├── Recommendations
      └── Section-wise Scores
```

---

## Subscription Workflow

```
New User
     │
     ▼
3 Free Resume Generations
     │
     ▼
Free Limit Reached
     │
     ▼
Purchase Subscription
     │
     ▼
Razorpay Payment Verification
     │
     ▼
Premium Activated
     │
     ├── Unlimited Resume Tailoring
     └── Unlimited ATS Analysis
```

---

# 🔐 Environment Variables

Configure the following environment variables before running the application.

```properties
MONGODB_URI=

JWT_SECRET=
JWT_EXPIRATION=

OPENAI_API_KEY=

RAZORPAY_KEY_ID=
RAZORPAY_KEY_SECRET=

CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
```

---

# ▶ Running Locally

## Clone Repository

```bash
git clone https://github.com/pranavchavan17/tailorly-backend.git
```

## Navigate

```bash
cd tailorly-backend
```

## Install Dependencies

```bash
./mvnw clean install
```

or

```bash
mvn clean install
```

## Run

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

---

# 📖 API Documentation

### Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON

```
http://localhost:8080/v3/api-docs
```

---

# 🔗 Related Repository

Frontend Repository

https://github.com/pranavchavan17/tailorly-frontend

---

# 🚀 Planned Enhancements

- Multiple Resume Templates
- Resume Version History
- AI Interview Preparation

---

# 👨‍💻 Author

**Pranav Chavan**

GitHub

https://github.com/pranavchavan17

---

## ⭐ Support

If you found this project helpful, consider giving the repository a ⭐ on GitHub.
