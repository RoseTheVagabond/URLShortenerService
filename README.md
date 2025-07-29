# URL Shortener Service

A production-ready URL shortening service built with Spring Boot, featuring comprehensive internationalization, advanced validation, and dual API/Web interfaces.

## Technical Stack

- **Framework**: Spring Boot 3.2.5 with Java 21
- **Persistence**: JPA/Hibernate with H2 database
- **Web Layer**: Spring MVC + Thymeleaf templating
- **Validation**: Hibernate Validator with custom constraints
- **Database**: H2 with file-based persistence
- **Build Tool**: Gradle with dependency management

## Key Features

**Core Functionality**: Cryptographically secure URL shortening with optional password protection, real-time visit analytics, and complete CRUD operations via RESTful API.

**Multi-Language Support**: Custom locale resolver with dynamic language detection (URL parameters, HTTP headers, session persistence) supporting English, Polish, and German with localized validation messages.

**Enterprise Validation**: Multi-layer architecture using Bean Validation, custom regex patterns, and complex password policies (10+ characters, specific character requirements) with granular error messaging.

**Clean Architecture**: Dual interface design with RESTful API and MVC web interface, centralized exception handling, and atomic transaction management for visit counting.

## API Endpoints

### Link Management
```http
POST   /api/links           # Create new short link
GET    /api/links/{id}      # Retrieve link information
PATCH  /api/links/{id}      # Update link (password-protected)
DELETE /api/links/{id}      # Delete link (password-protected)
```

### Redirection
```http
GET    /red/{id}            # Redirect to target URL (increments counter)
```

### Standard Response Format (GET, POST)
```json
{
  "id": "abCdEFGHiJ",
  "name": "MyLink",
  "targetUrl": "https://example.com",
  "redirectUrl": "http://localhost:8080/red/abCdEFGHiJ",
  "visits": 42
}
```

Note: PATCH and DELETE operations return `204 No Content` on success.

## Web Interface Features

- **Bootstrap-Responsive Design**: Mobile-first, professional UI
- **Interactive Forms**: Real-time validation with localized error messages
- **Language Switching**: Seamless i18n with session persistence
- **Flash Messages**: Success/error notifications with proper styling
- **Search Functionality**: Link discovery by name with password authentication

## Configuration Highlights

### Custom Internationalization Setup
```java
@Configuration
public class InternationalizationConfig implements WebMvcConfigurer, LocaleResolver {
    // Multi-source locale resolution with intelligent fallback
    // Session-based persistence with header detection
}
```

### Advanced Validation Patterns
```java
@Pattern.List({
    @Pattern(regexp = "^$|^.{10,}$", message = "{password.length.min}"),
    @Pattern(regexp = "^$|^.*[a-z].*$", message = "{password.lowercase.required}"),
    @Pattern(regexp = "^$|^(.*[A-Z]){2,}.*$", message = "{password.uppercase.min}")
    // ... additional complexity rules
})
```

## Quick Start

1. **Clone and Build**:
   ```bash
   ./gradlew bootRun
   ```

2. **Access Points**:
   - Web Interface: `http://localhost:8080`
   - API Base: `http://localhost:8080/api/links`
   - H2 Console: `http://localhost:8080/h2-console`

3. **Language Selection**:
   - Add `?lang=pl` or `?lang=de` to any URL
   - Or send `Accept-Language` header in API requests

## Implementation Highlights

- **Security-First Design**: Password hashing and validation at multiple layers
- **Performance Optimized**: Efficient ID generation with collision detection
- **Error Resilience**: Comprehensive exception handling with user-friendly messaging
- **Scalable Architecture**: Stateless design ready for horizontal scaling
- **Developer Experience**: Clean code structure with comprehensive validation feedback

## Database Schema

- **Optimized Storage**: Efficient indexing on unique constraints
- **Data Integrity**: Foreign key relationships and cascade operations
- **Visit Tracking**: Atomic increment operations for accurate analytics

---

*Built with modern Spring Boot practices, emphasizing clean architecture, internationalization, and enterprise-grade validation patterns.*
