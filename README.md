# SecondGo

A user-friendly and mobile-responsive e-commerce marketplace inspired by **finn.no**, where users can list items for sale, browse or search for products, favorite listings, communicate with sellers, and make purchases.

This project is part of the course **Full-stack Applikasjonsutvikling**. Developed by group 8, which consists of Johan M. Arntsen, Sverre G. Halvorsen, Anders Lundemo og Emil Ruud. The apllication includes a backend built with Spring Boot and a frontend built with Vue.js. 


### Table of Contents

- [Technologies](#technologies)
- [Features](#features)
- [Installation](#installation)
- [Running the Project](#running-the-project)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Database Migration](#database-migration)
- [Contributors](#contributors)

---

## Technologies

### Backend
- **Language**: Java
- **Framework**: Spring Boot
- **Database**: MySQL
- **Dependencies**:
  - Spring Data JPA
  - Spring Security
  - Spring Web
  - Lombok
  - Mockito (for testing)

### Frontend
- **Language**: JavaScript
- **Framework**: Vue.js
- **Build Tool**: Vite
- **State Management**: Pinia
- **CSS**: Custom styling with CSS variables

---

## Features

### Backend
- **User Management**:
  - Create, update, and delete users.
  - Authentication and authorization using JWT.
- **Messaging System**:
  - Send and receive messages between users.
  - Mark messages as read.
  - Retrieve conversations and inbox summaries.
- **Favorites System**:
  - Add and remove favorite listings.
  - Retrieve all favorites for a user.
- **Listings**:
  - Create, update, and delete listings.
  - Retrieve listings based on filtering criteria.

### Frontend
- **User Interface**:
  - User registration and login.
  - Display listings and details.
  - Messaging system with real-time updates via WebSocket.
  - Favorites system to save listings.

---

### Prerequisites
- **Backend**:
  - Java 17+
  - Maven

### Runnning the Project

1. Navigate to root folder:
    ```bash
    docker compose up --build 
    ```
    

## Project Structure
**Backend**
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── ntnu.idatt2105.project.backend/
│   │   │   │   ├── controller/   # REST API controllers
│   │   │   │   ├── model/        # Data models
│   │   │   │   ├── repository/   # Database operations
│   │   │   │   ├── service/      # Business logic
│   │   │   │   ├── security/     # Security configuration
│   │   ├── resources/
│   │   │   ├── db/
│   │   │   │   ├── migration/    # Flyway migrations
│   │   │   ├── application.properties
├── pom.xml

**Frontend**
frontend/
├── src/
│   ├── components/    # Reusable Vue components
│   ├── views/         # Pages
│   ├── router/        # Routing
│   ├── stores/        # State management (Pinia)
│   ├── services/      # API calls
│   ├── assets/        # Images and icons
│   ├── style.css      # Global styling
├── package.json

## Testing 

### Backend
1. run tests with maven
```bash
    mvn test 
``` 
2. Test coverage includes:
- Models: 
- Controllers: Testing API endpoints
- Services: Testing business logic
- Repositories: Testing database operations

### Frontend
1. run tests with vitest
```bash
    npm run test
```
2. Tests cover:
- Components
- Routing
- API integrations

## Contributors
- Group 8:
    - Johan M. Arntsen
    - Sverre G. Halvorsen
    - Anders Lundemo
    - Emil Ruud


    