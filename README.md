# Tailorly – AI-Powered Resume Tailoring Platform

Tailorly is an AI-powered resume tailoring platform that helps job seekers create customized resumes for specific job descriptions. The application analyzes a user's resume and a target job description, then generates an optimized resume that is more relevant to the role while maintaining factual accuracy.

The backend is built with **Spring Boot** and follows a clean, scalable architecture with secure authentication, cloud storage integration, and AI-powered resume processing.

---

## Features

* User Registration & Login
* JWT-based Authentication & Authorization
* Secure Password Encryption using BCrypt
* User Profile Management
* Resume Upload & Storage
* AI-Powered Resume Tailoring
* Resume Version Management
* Subscription & Usage Tracking
* RESTful APIs
* MongoDB Atlas Integration
* Production-ready layered architecture

---

## Tech Stack

### Backend

* Java 21
* Spring Boot 3.x
* Spring Security
* Spring Data MongoDB
* JWT Authentication
* Maven

### Database

* MongoDB Atlas

### Cloud Services

* Cloudinary (Resume Storage)

### AI

* OpenAI API

---

## Project Structure

```text
src
├── controller
├── service
│   ├── impl
├── repository
├── entity
├── dto
├── security
├── config
├── exception
└── util
```

---

## Authentication Flow

1. User registers with email and password.
2. Password is encrypted using BCrypt.
3. User logs in.
4. Backend validates credentials.
5. JWT token is generated.
6. Protected APIs require a valid JWT in the Authorization header.

---

## Main API Endpoints

### Authentication

| Method | Endpoint                |
| ------ | ----------------------- |
| POST   | `/api/v1/auth/register` |
| POST   | `/api/v1/auth/login`    |

### User

| Method | Endpoint                        |
| ------ | ------------------------------- |
| GET    | `/api/v1/users/me`              |
| PUT    | `/api/v1/users/profile`         |
| PUT    | `/api/v1/users/change-password` |

### Resume

| Method | Endpoint                 |
| ------ | ------------------------ |
| POST   | `/api/v1/resumes/upload` |
| GET    | `/api/v1/resumes`        |

### Payment & Subscription

| Method | Endpoint                       |
| ------ | ------------------------------ |
| POST   | `/api/v1/payment/create-order` |
| POST   | `/api/v1/payment/verify`       |
| GET    | `/api/v1/subscription`         |

---

## Security

* JWT Authentication
* BCrypt Password Hashing
* Stateless Sessions
* Role-based Authorization
* Protected REST APIs

---

## Getting Started

### Prerequisites

* Java 21
* Maven
* MongoDB Atlas
* Git

### Clone the Repository

```bash
git clone https://github.com/pranavchavan17/tailorly-backend.git
cd tailorly-backend
```

### Configure Environment Variables

Create an `.env` file or configure `application.properties` with:

```properties
MONGODB_URI=
JWT_SECRET=
OPENAI_API_KEY=
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
```

### Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

The application will start at:

```text
http://localhost:8080
```

---

## Future Enhancements

* ATS Score Analysis
* Payment Integration Improvements
* Resume Analytics Dashboard
* AI Resume Suggestions
* Multi-language Resume Support

---

## Author

**Pranav Chavan**

Java Backend Developer | Spring Boot | MongoDB | REST APIs | JWT Authentication | AI Integration

---

## License

This project is intended for educational and portfolio purposes.
