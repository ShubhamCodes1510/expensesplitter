# Expense Splitter

A full-stack group expense management and settlement application built with Spring Boot and Angular.

## Tech Stack

**Backend**
- Java 17 + Spring Boot 3.2.1
- Spring Data JPA + MySQL 8
- Spring Security + JWT Authentication
- Springdoc OpenAPI (Swagger)
- Maven

**Frontend**
- Angular 17 (Standalone Components, Lazy Loading)
- Angular Material + Tailwind CSS
- Chart.js (ng2-charts)
- TypeScript

## Features

- **Authentication** - Register/Login with JWT tokens, role-based access
- **Dashboard** - Overview with spending charts, category breakdown, quick stats
- **Expenses** - Create, view, filter expenses with categories and split details
- **Settlements** - View balances, optimized settlement calculation, payment history
- **Groups** - Create and manage expense groups
- **Profile** - User profile management, settings, theme toggle (light/dark)
- **Reports** - Export expenses/settlements as CSV, view HTML summary reports
- **File Upload** - Receipt upload support
- **Notifications** - Email notifications via SMTP
- **Responsive** - Mobile-friendly sidebar with hamburger menu

## Project Structure

```
expensesplitter/
├── backend/
│   ├── src/main/java/com/expense/expensesplitter/
│   │   ├── config/          # Security, JWT, WebSocket, Validation
│   │   ├── controller/      # REST API controllers
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── exception/       # Global exception handling
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   └── service/         # Business logic
│   └── src/main/resources/
│       ├── application.properties
│       └── templates/       # Email templates
├── frontend/
│   └── src/app/
│       ├── core/            # Guards, Interceptors, Services, Models
│       ├── features/        # Auth, Dashboard, Expenses, Settlements, Profile
│       ├── shared/          # Reusable components, pipes, directives
│       ├── navbar/
│       └── sidebar/
└── .gitignore
```

## Prerequisites

- Java 17+
- Node.js 18+ & npm
- MySQL 8+
- Maven (or use the included `mvnw` wrapper)

## Getting Started

### 1. Database Setup

```sql
CREATE DATABASE expense_splitter;
```

Default credentials are `root`/`root`. Update `backend/src/main/resources/application.properties` if different.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

Frontend runs on `http://localhost:4200`.

### 4. Environment Variables (Optional)

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/expense_splitter` | MySQL connection URL |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `JWT_SECRET` | (built-in) | Secret key for JWT signing |
| `JWT_EXPIRATION` | `86400000` | Token expiry in ms (24h) |
| `EMAIL_USERNAME` | | SMTP email |
| `EMAIL_PASSWORD` | | SMTP password |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login and get JWT token |
| POST | `/api/auth/register` | Register new user |
| GET | `/api/expenses` | List all expenses |
| POST | `/api/expenses` | Create new expense |
| GET | `/api/expenses/{id}` | Get expense details |
| GET | `/api/settlements/balances` | Get user balances |
| GET | `/api/settlements/calculate` | Optimized settlements |
| GET | `/api/settlements` | Settlement history |
| POST | `/api/settlements/process-payment` | Process a payment |
| GET | `/api/groups` | List all groups |
| GET | `/api/reports/expenses/csv` | Export expenses CSV |
| GET | `/api/reports/settlements/csv` | Export settlements CSV |
| GET | `/api/reports/summary/html` | View summary report |

Swagger UI available at `http://localhost:8080/swagger-ui.html`.

## Default Users

| Email | Password |
|-------|----------|
| john@example.com | password |
| jane@example.com | password |

## License

MIT
