# Media manage Using Jwt 
A robust, enterprise-grade media management platform built with Spring Boot, featuring JWT authentication, secure streaming capabilities, and comprehensive access control.

#Overview

This application provides a complete solution for managing digital media assets with enterprise-level security. It implements time-limited access URLs, comprehensive audit logging, and role-based authentication to ensure secure media distribution.

#Technology Stack
<div align="center">
  <table>
    <tr>
      <td align="center" width="96">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="48" height="48" alt="Java" />
        <br />Java 17
      </td>
      <td align="center" width="96">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="48" height="48" alt="Spring Boot" />
        <br />Spring Boot
      </td>
      <td align="center" width="96">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mongodb/mongodb-original.svg" width="48" height="48" alt="MongoDB" />
        <br />MongoDB
      </td>
      <td align="center" width="96">
        <img src="https://jwt.io/img/pic_logo.svg" width="48" height="48" alt="JWT" />
        <br />JWT
      </td>
      <td align="center" width="96">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/maven/maven-original.svg" width="48" height="48" alt="Maven" />
        <br />Maven
      </td>
    </tr>
  </table>
</div>

#Core Features

**Security & Authentication**

*JWT-based Authentication: Stateless token-based security

*BCrypt Password Encryption: Industry-standard password hashing

*Role-based Access Control: Administrative user management

*Time-limited URLs: Configurable expiration for media access

**Enterprise Capabilities**

*RESTful API Design: Standard HTTP methods and status codes

*Database Abstraction: MongoDB integration with Spring Data

*Configuration Management: Environment-based configuration

*Comprehensive Error Handling: Detailed error responses

**Prerequisites**

*Java Development Kit: Version 17 or higher

*Apache Maven: Version 3.6 or higher

*MongoDB: Version 4.0 or higher (running on localhost:27017)

Installation & Setup

1. Clone Repository
```
bash  git clone <repository-url>cd MediaApp
```
2. Configure Application
Create or update src/main/resources/application.properties:

 **Database Configuration**

```
spring.data.mongodb.host=localhost

spring.data.mongodb.port=27017

spring.data.mongodb.database=MediaAppDb

spring.data.mongodb.auto-index-creation=true
```

# JWT Configuration
```
    jwt.secret=mySecretKey123456789012345678901234567890123456789012345678901234567890
    jwt.expiration=86400000
```

# Server Configuration
server.port=8080

3. Build Application
```bash mvn clean compile```

```mvn clean install```

5. Run Application
```bash mvn spring-boot:run```

The application will be available at http://localhost:8080


**Authentication Endpoints**

**Admin Registration**

```http POST /auth/signup```
Content-Type: application/json
```json
{
    "email": "admin@example.com",
    "password": "securePassword123"
}
```
Response:

json "Admin user registered successfully!"

**Admin Login**

```http POST /auth/login ```

Content-Type: application/json

```json
{
    "email": "admin@example.com",
    "password": "securePassword123"
}
```
Response:
```json{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer"
}
```
**Media Management Endpoints (Protected)**

**Add Media Asset**

```http POST /media```

Authorization: Bearer <jwt-token>

Content-Type: application/json

```json
{
    "title": "Sample Video Content",
    "type": "video",
    "fileUrl": "https://example.com/media/video.mp4"
}
```

Response:

json "Media added successfully with ID: 65f8a1b2c3d4e5f6789abcde"

**Generate Secure Stream URL**

```http GET /media/{mediaId}/stream-url```

Authorization: Bearer <jwt-token>

Response:
```json{
    "streamUrl": "https://example.com/media/video.mp4?token=eyJhbGciOiJIUz...&expires=1694372815000",
    "expiresInMinutes": 10
}
```
**Testing & Validation Endpoints**

Stream Access Validation

```http GET /media/stream/{mediaId}?token={jwt-token}&expires={timestamp}```

Success Response:

json "Stream access granted for media: {mediaId}"

Expired Response (401):

json "Stream URL has expired"

Invalid Token Response (401):

json "Token validation failed: JWT signature does not match"

Administrative Endpoints (Protected)

List All Admin Users

```http GET /api/users```

Authorization: Bearer <jwt-token>

Response:

```json[
{
"id": "65f8a1b2c3d4e5f6789abcde",
"email": "admin@example.com",
"hashedPassword": null,
"createdAt": "2024-01-15T10:30:00"
}
]
```
Testing Guide

Complete Testing Workflow


# Start application
```mvn spring-boot:run```
1. Authentication Flow
http# Register admin user

```POST http://localhost:8080/auth/signup```
```json
{
    "email": "test@admin.com",
    "password": "testPassword123"
}
```

# Authenticate and get token
```POST http://localhost:8080/auth/login```

```json
{
    "email": "test@admin.com",
    "password": "testPassword123"
}
```
3. Media Management Testing
http# Add media asset

```POST http://localhost:8080/media```

Authorization: Bearer <your-jwt-token>

```json
{
    "title": "Test Media",
    "type": "video",
    "fileUrl": "https://sample-videos.com/test.mp4"
}
```
# Generate stream URL
```GET http://localhost:8080/media/{media-id}/stream-url```

Authorization: Bearer <your-jwt-token>

4. Time-Limited Access Testing
http# Test immediate access (should succeed)

```GET http://localhost:8080/media/stream/{media-id}?token={stream-token}&expires={timestamp}```

# Wait for expiration period (default: 10 minutes)
# Test expired access (should fail with 401)
```GET http://localhost:8080/media/stream/{media-id}?token={stream-token}&expires={timestamp}```


Quick Expiration Testing

For  testing, modify MediaService.java:

Reduce expiration to 10 seconds for testing

```.setExpiration(new Date(System.currentTimeMillis() + 10000))```
**_Database Schema**


**_admin_users**

```{
    "_id": ObjectId,
    "email": String (unique),
    "hashedPassword": String,
    "createdAt": ISODate
}
```
**_media_assets**
```{
    "_id": ObjectId,
    "title": String,
    "type": String, // "video" or "audio"
    "fileUrl": String,
    "createdAt": ISODate
}
```
**_media_view_logs**
```{
    "_id": ObjectId,
    "mediaId": String,
    "viewedByIp": String,
    "timestamp": ISODate
}
```


**Security Features**

*Password Security*: BCrypt hashing with salt

*Token Security*: HMAC-SHA256 signed JWTs

*Time-based Security*: Configurable token expiration

*Access Control*: Role-based endpoint protection




**Project Structure**

The application follows standard Spring Boot conventions with clear separation of concerns:

*Controllers: Handle HTTP requests and responses

*Services: Implement business logic

*Repositories: Data access layer

*Models: Entity definitions

*DTOs: Data transfer objects

*Configuration: Security and application configuration

**Extension Points**

*Custom authentication providers

*Additional media formats

*Advanced access control rules

Integration with cloud storage services

Real-time streaming capabilities
