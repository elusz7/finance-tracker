# Personal Finance Tracker

A full-stack personal finance application for tracking income and expenses, managing transactions, and reviewing spending trends. The project uses a React and Vite frontend, a Java Spring Boot REST API, and a PostgreSQL database.

> **Project status:** In progress. This repository currently supports transaction CRUD, backend-powered transaction filtering, a year-to-date dashboard, and date-range reports.

## Features

### Transaction management
- Create, view, edit, and delete income and expense transactions.
- Store an optional description, positive monetary amount, transaction type, classification, and date.
- Persist transaction data in PostgreSQL.
- Display transactions sorted by date, with the ability to filter the transaction list.

### Backend-powered filtering
The transaction list supports optional query parameters for:
- Transaction type (`INCOME` or `EXPENSE`)
- Expense category
- Income source
- Inclusive start and end dates
- Minimum and maximum amount

Filters are edited separately from the currently applied filters. Selecting **Apply Filters** sends the selected criteria to the backend; **Reset Filters** clears the criteria.

### Dashboard
- Year-to-date income.
- Year-to-date expenses.
- Year-to-date net balance.
- Monthly income-versus-expenses bar chart.
- Dashboard data refreshes after transaction create, update, and delete operations.

### Reports
Reports can be generated for a selected inclusive date range:
- **Summary:** total income, total expenses, net cash flow, and savings rate.
- **Monthly:** income, expenses, and net cash flow by calendar month. Months without transactions are included with zero totals.
- **Spending by category:** expense totals and each category's percentage of total expenses, displayed as a donut chart.

The Reports page initially loads the current month's first day through today. Changing the dates does not run a new report until **Generate Reports** is selected.

## Technology Stack

| Area | Technologies |
|---|---|
| Frontend | React 19, JavaScript, Vite 8, CSS |
| Charts | Recharts 3 |
| Backend | Java 21, Spring Boot 4, Spring MVC |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation |
| Backend testing | JUnit 5, Mockito, Spring MVC test support |
| Build tooling | Maven Wrapper, npm |
| Frontend checks | ESLint, Vite production build |

## Application Architecture

The application uses a layered client/server architecture:

```text
React + Vite
    |
    | HTTP / JSON
    v
Spring Boot REST controllers
    |
    v
Service layer
    |
    +-----------------------------+
    |                             |
    v                             v
TransactionRepository       ReportRepository
Spring Data JPA             NamedParameterJdbcTemplate
    |                             |
    +--------------+--------------+
                   |
                   v
              PostgreSQL
```

- **Controllers** expose the transaction and reporting REST endpoints.
- **Services** contain transaction operations, validation of filter ranges, and report calculations.
- **TransactionRepository** uses Spring Data JPA and JPQL for transaction persistence and filtering.
- **ReportRepository** loads SQL files from the classpath and executes aggregate reporting queries with `NamedParameterJdbcTemplate`.
- **DTOs** define request and report response shapes.

## Prerequisites

Install:
- JDK 21
- Node.js and npm (a current LTS release)
- PostgreSQL
- Git

The backend includes Maven Wrapper scripts, so a separate Maven installation is not required.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/elusz7/finance-tracker.git
cd finance-tracker
```

### 2. Create the PostgreSQL database

Start PostgreSQL and create a database named `finance_tracker`:

```sql
CREATE DATABASE finance_tracker;
```

The default connection is configured for:
- Host: `localhost`
- Port: `5432`
- Database: `finance_tracker`
- Username: `postgres`
- Password: supplied through the `DB_PASSWORD` environment variable

If your local PostgreSQL settings differ, update `backend/src/main/resources/application.properties`.

### 3. Configure the database password

Set `DB_PASSWORD` in the environment used to launch the backend.

**Windows PowerShell**
```powershell
$env:DB_PASSWORD = "your-local-postgres-password"
```

**macOS / Linux**
```bash
export DB_PASSWORD="your-local-postgres-password"
```

You can also configure the variable in your IDE's run configuration. Do not commit real credentials.

### 4. Start the backend

From the `backend` directory:

**Windows PowerShell**
```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux**
```bash
./mvnw spring-boot:run
```

The API runs at `http://localhost:8080` by default.

### 5. Start the frontend

Open a second terminal in the `frontend` directory:

```bash
npm install
npm run dev
```

Open `http://localhost:5173` (or the URL printed by Vite). Keep PostgreSQL and both application processes running while using the app.

## Using the Application

### Dashboard
The dashboard displays year-to-date totals from January 1 of the current year through the current date, plus a monthly income/expenses chart.

### Transactions
1. Open **Transactions**.
2. Use the form to create an income or expense. Amounts must be greater than zero.
3. Select a transaction type and its corresponding classification.
4. Optionally enter a description and select the transaction date.
5. Use the filters to narrow the list by type, category/source, date range, and amount range.
6. Select **Apply Filters** to request filtered data from the backend. Select **Reset Filters** to clear the filters.
7. Use the transaction actions to edit or delete an existing record.

### Reports
1. Open **Reports**.
2. Select a start and end date.
3. Select **Generate Reports**.
4. Review the summary cards, monthly trend, and spending-by-category donut chart.
5. Select **Reset Filters** to return to the current month's date range.

## Transaction Data Model

Transactions are stored in the `transactions` table.

| Field | Meaning | Requirement |
|---|---|---|
| `id` | Generated transaction identifier | Generated by the database |
| `description` | Optional transaction notes | Optional |
| `amount` | Monetary amount (`BigDecimal`) | Required; greater than zero |
| `type` | `INCOME` or `EXPENSE` | Required |
| `expenseCategory` | Expense classification | Required for expenses; null for income |
| `incomeSource` | Income classification | Required for income; null for expenses |
| `date` | Transaction date (`LocalDate`) | Required |

A class-level validation constraint ensures the classification matches the transaction type: expenses require an expense category and no income source; income requires an income source and no expense category.

### Expense categories

```text
HOUSING
UTILITIES
PHONE_INTERNET
SUBSCRIPTION
GROCERY
DINING
ENTERTAINMENT
TRANSPORTATION
GAS_FUEL
AUTO_MAINTENANCE
HEALTHCARE
INSURANCE
TRAVEL
SHOPPING
PERSONAL_CARE
EDUCATION
PETS
GIFTS_DONATIONS
TAXES
DEBT_PAYMENT
CHILDCARE
FITNESS
HOME_MAINTENANCE
FEES
WORK_EXPENSES
OTHER
```

### Income sources

```text
SALARY
FREELANCE
INVESTMENT
INTEREST
GIFT
REFUND
OTHER
```

Enum values in API requests must match the values above exactly.

## REST API Reference

Base URL: `http://localhost:8080`

### Transaction endpoints

| Method | Endpoint | Description | Success status |
|---|---|---|---|
| `GET` | `/api/transactions` | Retrieve transactions; optional filters supported | `200 OK` |
| `GET` | `/api/transactions/{id}` | Retrieve one transaction | `200 OK` |
| `POST` | `/api/transactions` | Create a transaction | `201 Created` |
| `PUT` | `/api/transactions/{id}` | Update a transaction | `200 OK` |
| `DELETE` | `/api/transactions/{id}` | Delete a transaction | `204 No Content` |

A request for a transaction ID that does not exist is handled as a not-found error.

### Transaction filters

All filter parameters are optional and can be combined:

| Parameter | Type | Description |
|---|---|---|
| `type` | `INCOME` or `EXPENSE` | Transaction type |
| `expenseCategory` | Expense category enum | Expense category |
| `incomeSource` | Income source enum | Income source |
| `startDate` | `YYYY-MM-DD` | Inclusive lower date bound |
| `endDate` | `YYYY-MM-DD` | Inclusive upper date bound |
| `minAmount` | Decimal | Inclusive minimum amount |
| `maxAmount` | Decimal | Inclusive maximum amount |

Example:

```http
GET /api/transactions?type=EXPENSE&expenseCategory=GROCERY&startDate=2026-10-01&endDate=2026-10-31&minAmount=20&maxAmount=150
```

The backend rejects a start date later than the end date and a minimum amount greater than the maximum amount. Results are ordered by transaction date descending.

### Transaction request examples

**Create an expense**
```json
{
  "description": "Weekly groceries",
  "amount": 52.75,
  "type": "EXPENSE",
  "expenseCategory": "GROCERY",
  "incomeSource": null,
  "date": "2026-10-27"
}
```

**Create income**
```json
{
  "description": "Paycheck",
  "amount": 1800.00,
  "type": "INCOME",
  "expenseCategory": null,
  "incomeSource": "SALARY",
  "date": "2026-10-27"
}
```

The description is optional. Amount, type, and date are required. Amounts must be greater than zero, and dates use ISO format (`YYYY-MM-DD`).

### Reporting endpoints

All three date-range report endpoints require `startDate` and `endDate` in `YYYY-MM-DD` format. The date range is inclusive.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/transactions/reports/dashboard` | Current-year-to-date dashboard totals |
| `GET` | `/api/transactions/reports/summary?startDate={date}&endDate={date}` | Date-range totals and savings rate |
| `GET` | `/api/transactions/reports/monthly?startDate={date}&endDate={date}` | Monthly income, expenses, and net cash flow |
| `GET` | `/api/transactions/reports/category?startDate={date}&endDate={date}` | Expense totals and percentages by category |

#### Dashboard response

The dashboard endpoint returns:
- `ytdIncome`: income from January 1 through the current date.
- `ytdExpenses`: expenses from January 1 through the current date.
- `ytdBalance`: year-to-date income minus expenses.

#### Summary response

The summary endpoint returns:
- `totalIncome`
- `totalExpenses`
- `netCashFlow` (income minus expenses)
- `savingsRate` (net cash flow divided by income, multiplied by 100; zero when income is zero)

#### Monthly response

The monthly endpoint returns a list containing:
- `yearMonth` (`YYYY-MM`)
- `totalIncome`
- `totalExpenses`
- `netCashFlow`

The SQL query generates each calendar month in the requested period, including months with no matching transactions. The first and last months are still limited to the exact requested dates.

#### Category response

The category endpoint returns expense categories with:
- `expenseCategory`
- `expenseAmount`
- `spendingPercentage` (category expense amount divided by total expenses, multiplied by 100)

Categories with no expenses are omitted. If there are no expenses in the selected range, the endpoint returns an empty list.

## Validation and Error Handling

- Required transaction fields are validated with Jakarta Bean Validation.
- Amounts must be greater than zero.
- Transaction type and classification must be consistent.
- Missing transaction IDs are handled with a not-found exception.
- Transaction filter ranges are validated for date and amount ordering.
- Report date ranges are validated by the report service.
- The frontend displays loading and error states for dashboard and report data and displays success feedback after transaction operations.

## Testing and Quality Checks

### Backend tests

From `backend`:

**Windows PowerShell**
```powershell
.\mvnw.cmd clean test
```

**macOS / Linux**
```bash
./mvnw clean test
```

Tests are located under `backend/src/test/java/com/example/financetracker/` and include transaction/report controller and service tests.

### Frontend checks

From `frontend`:

```bash
npm run lint
npm run build
```

To preview the production build:

```bash
npm run preview
```

## Project Structure

```text
finance-tracker/
├── backend/
│   ├── .mvn/wrapper/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/financetracker/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── entity/
│       │   │   ├── exception/
│       │   │   ├── repository/
│       │   │   ├── service/
│       │   │   └── validation/
│       │   └── resources/
│       │       └── queries/
│       └── test/java/com/example/financetracker/
└── frontend/
    └── src/
        ├── components/
        │   └── Reports/
        ├── App.jsx
        └── App.css
```

## Configuration Notes

The backend configuration is in `backend/src/main/resources/application.properties`. The current local setup uses:

```properties
spring.application.name=finance-tracker-backend
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_tracker
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

The frontend currently uses `http://localhost:8080` directly in its fetch calls, and the backend allows cross-origin requests from `http://localhost:5173`. Before deployment, use environment-specific API configuration and restrict CORS to the intended frontend origin.

## Known Limitations and Future Improvements

The application is an in-progress personal project intended for local development. It is not a production-ready financial service. It does not connect to banks, import live account balances, or provide financial advice.

Potential improvements, roughly prioritized:

### Core functionality and correctness
- Resolve the `OTHER` classification ambiguity when the transaction type filter is set to `ALL`. `OTHER` exists in both the expense-category and income-source enums, while the current frontend mapping selects one parameter.
- Add pagination and, if useful, server-side sorting for larger transaction histories.
- Improve filter validation and surface backend validation errors in a consistent, user-friendly way.
- Add tests for combined filters, open-ended date/amount ranges, invalid ranges, and the shared `OTHER` value.
- Review update semantics for partial versus full updates and make the API contract explicit.

### Frontend quality
- Add automated component and integration tests for transaction forms, filters, reports, and loading/error states.
- Extract repeated API calls into a shared API client and move the API base URL into environment configuration.
- Improve accessibility with keyboard-friendly controls, semantic labels, and chart alternatives or summaries.
- Add clearer empty states and consistent error handling across dashboard, transactions, and reports.
- Remove development-only logging before release.

### Data and reporting
- Add CSV export for transactions and reports.
- Add configurable budgets, spending limits, and financial goals.
- Add account support and transfers between accounts.
- Add recurring transactions.
- Support configurable currency and locale formatting.
- Consider additional reporting such as year-over-year comparisons and trends.

### Security and deployment
- Add authentication and authorization before supporting multiple users; ensure each user can only access their own records.
- Use HTTPS and secure secret management.
- Replace `ddl-auto=update` with database migrations (for example, Flyway or Liquibase).
- Restrict CORS to trusted origins.
- Add database backup/recovery procedures, production logging, and monitoring.
- Add a CI workflow to run backend tests and frontend lint/build checks on each push or pull request.

## License

No license is currently specified. Add a `LICENSE` file if you intend to distribute the project as open source.
