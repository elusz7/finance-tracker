# Personal Finance Tracker

A full-stack personal finance application for recording income and
expenses, managing transactions, and understanding financial activity
over a selected date range. The application combines a React and Vite
frontend, a Java Spring Boot REST API, and PostgreSQL.

## Table of Contents

-   [Features](#features)
-   [Technology Stack](#technology-stack)
-   [Application Architecture](#application-architecture)
-   [Prerequisites](#prerequisites)
-   [Getting Started](#getting-started)
    -   [1. Clone the repository](#1-clone-the-repository)
    -   [2. Create the database](#2-create-the-database)
    -   [3. Configure database
        credentials](#3-configure-database-credentials)
    -   [4. Run the backend](#4-run-the-backend)
    -   [5. Run the frontend](#5-run-the-frontend)
-   [Using the Application](#using-the-application)
-   [Transaction Data Model](#transaction-data-model)
-   [REST API Reference](#rest-api-reference)
-   [Reports API](#reports-api)
-   [Validation and Error Handling](#validation-and-error-handling)
-   [Testing and Quality Checks](#testing-and-quality-checks)
-   [Project Structure](#project-structure)
-   [Configuration](#configuration)
-   [Troubleshooting](#troubleshooting)
-   [Security and Production
    Considerations](#security-and-production-considerations)
-   [Future Enhancements](#future-enhancements)
-   [License](#license)

## Features

### Transaction management

-   Create income and expense transactions.
-   View the transaction history.
-   Edit existing transactions.
-   Delete transactions.
-   Record a transaction description, amount, classification, and date.
-   Store data in PostgreSQL so transactions persist between sessions.
-   Validate required fields and require transaction amounts greater
    than zero.
-   Use separate classifications for expense categories and income
    sources.

### Dashboard

-   View year-to-date income, expenses, and net balance.
-   Review recent transactions.
-   Filter transactions by type, category or income source, date range,
    and amount range.
-   See success feedback after transaction changes.

### Financial reports

Reports can be generated for a selected start and end date:

-   **Summary report:** total income, total expenses, net cash flow, and
    savings rate.
-   **Monthly report:** monthly income, expenses, and net cash flow
    displayed as a three-line trend chart.
-   **Spending by category:** expense totals and percentages visualized
    in an interactive donut chart with hover details.

The report endpoints accept inclusive `startDate` and `endDate` query
parameters in `YYYY-MM-DD` format.

## Technology Stack

  Area              Technologies
  ----------------- -----------------------------------------
  Frontend          React 19, JavaScript, Vite 8, CSS
  Charts            Recharts 3
  Backend           Java 21, Spring Boot 4, Spring MVC
  Persistence       Spring Data JPA, Hibernate
  Database          PostgreSQL
  Validation        Jakarta Bean Validation
  Build tooling     Maven Wrapper, npm
  Backend tests     JUnit, Mockito, Spring MVC test support
  Frontend checks   ESLint, Vite production build

## Application Architecture

The application uses a layered frontend/backend architecture:

``` text
React + Vite browser application
          |
          | HTTP / JSON
          v
Spring Boot REST controllers
          |
          v
Service layer
          |
          +----------------------+
          |                      |
          v                      v
Transaction repository     Report repository
(Spring Data JPA)           (SQL queries)
          |                      |
          +----------+-----------+
                     |
                     v
                PostgreSQL
```

The transaction API uses Spring Data JPA for transaction CRUD
operations. Reporting queries use SQL through
`NamedParameterJdbcTemplate` to calculate summary, monthly, and category
aggregates.

### Default local URLs

-   Frontend (Vite): `http://localhost:5173`
-   Backend API: `http://localhost:8080`
-   PostgreSQL: `localhost:5432`

The frontend currently calls the backend using `http://localhost:8080`.
The backend controllers allow cross-origin requests from
`http://localhost:5173`.

## Prerequisites

Install the following tools:

-   **JDK 21**
-   **Node.js** (a current LTS release) and npm
-   **PostgreSQL**
-   **Git**

The backend includes Maven Wrapper scripts, so installing Maven
separately is not required.

## Getting Started

### 1. Clone the repository

``` bash
git clone https://github.com/elusz7/finance-tracker.git
cd finance-tracker
```

If you downloaded the repository as a ZIP file, extract it and open a
terminal in the extracted project directory.

### 2. Create the database

Start PostgreSQL and create a database named `finance_tracker`:

``` sql
CREATE DATABASE finance_tracker;
```

The default connection settings are:

  Setting    Default
  ---------- -------------------------
  Database   `finance_tracker`
  Host       `localhost`
  Port       `5432`
  Username   `postgres`
  Password   Read from `DB_PASSWORD`

If your local PostgreSQL username, host, port, or database name differs,
update `backend/src/main/resources/application.properties`.

### 3. Configure database credentials

The backend reads its password from the `DB_PASSWORD` environment
variable. Set it in the same terminal or IDE run configuration used to
start the backend.

**Windows PowerShell:**

``` powershell
$env:DB_PASSWORD = "your-local-postgres-password"
```

**macOS / Linux:**

``` bash
export DB_PASSWORD="your-local-postgres-password"
```

For IntelliJ IDEA, add `DB_PASSWORD` to the environment variables in the
run configuration for `FinanceTrackerBackendApplication`.

Do not commit real database credentials to source control.

### 4. Run the backend

Open a terminal in the `backend` directory.

**Windows PowerShell:**

``` powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**

``` bash
./mvnw spring-boot:run
```

The API should start at `http://localhost:8080`.

The current configuration uses:

``` properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

Hibernate updates the local database schema based on the JPA entities at
startup. This is convenient for development; use a managed migration
approach for production.

### 5. Run the frontend

Open a second terminal in the `frontend` directory:

``` bash
npm install
npm run dev
```

Open the URL printed by Vite. The default is:

``` text
http://localhost:5173
```

Keep both the backend and frontend running while using the application.

## Using the Application

### Dashboard

The Dashboard provides a year-to-date overview and transaction activity.
The year-to-date totals are calculated from transaction dates between
January 1 of the current year and the current date.

### Transactions

Use the transaction form to create an income or expense record.

-   Enter an amount greater than zero.
-   Choose `INCOME` or `EXPENSE`.
-   For an expense, select an expense category.
-   For income, select an income source.
-   Choose a transaction date.
-   Add an optional description.

Use the transaction list controls to filter, edit, or delete records.

### Reports

Open the Reports page to review financial activity over a date range.

1.  Choose a start date and end date.
2.  Select **Generate Reports** to load the reports for that range.
3.  Use **Reset Filters** to restore the current month's date range.
4.  Review the summary, monthly trend chart, and category donut chart.

The Reports page initially loads using the current month's first day
through today. Changing the date inputs does not itself generate a new
report; select **Generate Reports** to apply the selected range.

## Transaction Data Model

Transactions are stored in the `transactions` table.

  -----------------------------------------------------------------------
  Field                   Type / meaning          Required
  ----------------------- ----------------------- -----------------------
  `id`                    Generated transaction   Generated by database
                          identifier              

  `description`           Optional notes about    No
                          the transaction         

  `amount`                Monetary amount         Yes; greater than zero
                          represented as          
                          `BigDecimal`            

  `type`                  `INCOME` or `EXPENSE`   Yes

  `expenseCategory`       Expense classification  Required for expenses;
                                                  otherwise null

  `incomeSource`          Income classification   Required for income;
                                                  otherwise null

  `date`                  Transaction date        Yes
                          (`LocalDate`)           
  -----------------------------------------------------------------------

The application validates the classification so that an expense uses an
expense category and an income transaction uses an income source.

### Transaction types

-   `INCOME`
-   `EXPENSE`

### Expense categories

The backend currently defines these expense categories:

``` text
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

``` text
SALARY
FREELANCE
INVESTMENT
INTEREST
GIFT
REFUND
OTHER
```

Enum values in API requests must match these values exactly.

## REST API Reference

Base URL:

``` text
http://localhost:8080
```

### Transaction endpoints

  ---------------------------------------------------------------------------------
  Method            Endpoint                   Description       Success status
  ----------------- -------------------------- ----------------- ------------------
  `GET`             `/api/transactions`        Retrieve all      `200 OK`
                                               transactions      

  `GET`             `/api/transactions/{id}`   Retrieve one      `200 OK`
                                               transaction       

  `POST`            `/api/transactions`        Create a          `201 Created`
                                               transaction       

  `PUT`             `/api/transactions/{id}`   Update a          `200 OK`
                                               transaction       

  `DELETE`          `/api/transactions/{id}`   Delete a          `204 No Content`
                                               transaction       
  ---------------------------------------------------------------------------------

A request for a transaction ID that does not exist returns
`404 Not Found`.

### Transaction request example

The API uses `expenseCategory` and `incomeSource` as separate fields.
For an expense, provide `expenseCategory` and set `incomeSource` to
`null` or omit it. For income, provide `incomeSource` and set
`expenseCategory` to `null` or omit it.

**Create an expense:**

``` json
{
  "description": "Weekly groceries",
  "amount": 52.75,
  "type": "EXPENSE",
  "expenseCategory": "GROCERY",
  "incomeSource": null,
  "date": "2026-10-27"
}
```

**Create income:**

``` json
{
  "description": "Paycheck",
  "amount": 1800.00,
  "type": "INCOME",
  "expenseCategory": null,
  "incomeSource": "SALARY",
  "date": "2026-10-27"
}
```

The description is optional. Amount, type, and date are required.
Amounts must be greater than zero, and dates use ISO format
`YYYY-MM-DD`.

### Example requests

Retrieve all transactions:

``` bash
curl http://localhost:8080/api/transactions
```

Create an expense:

``` bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Weekly groceries",
    "amount": 52.75,
    "type": "EXPENSE",
    "expenseCategory": "GROCERY",
    "incomeSource": null,
    "date": "2026-10-27"
  }'
```

Retrieve transaction `1`:

``` bash
curl http://localhost:8080/api/transactions/1
```

Update transaction `1`:

``` bash
curl -X PUT http://localhost:8080/api/transactions/1 \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Updated grocery purchase",
    "amount": 60.00,
    "type": "EXPENSE",
    "expenseCategory": "GROCERY",
    "incomeSource": null,
    "date": "2026-10-27"
  }'
```

Delete transaction `1`:

``` bash
curl -X DELETE http://localhost:8080/api/transactions/1
```

On Windows PowerShell, `curl` may be an alias. Use `curl.exe` if
necessary.

## Reports API

All report endpoints require both `startDate` and `endDate` query
parameters. Dates use ISO format (`YYYY-MM-DD`), and the requested date
range is inclusive.

Example date range:

``` text
startDate=2026-10-01&endDate=2026-10-31
```

### Summary report

``` http
GET /api/transactions/reports/summary?startDate=2026-10-01&endDate=2026-10-31
```

Returns one object containing:

  Field             Description
  ----------------- ---------------------------------------------------
  `totalIncome`     Sum of income transactions in the selected range
  `totalExpenses`   Sum of expense transactions in the selected range
  `netCashFlow`     Income minus expenses
  `savingsRate`     Savings rate calculated by the reporting service

Example response shape:

``` json
{
  "totalIncome": 3000.00,
  "totalExpenses": 1850.00,
  "netCashFlow": 1150.00,
  "savingsRate": 38.33
}
```

### Monthly report

``` http
GET /api/transactions/reports/monthly?startDate=2026-08-15&endDate=2026-10-20
```

Returns a list of monthly aggregates:

  -----------------------------------------------------------------------
  Field                               Description
  ----------------------------------- -----------------------------------
  `yearMonth`                         Calendar month in `YYYY-MM` form

  `totalIncome`                       Income total for the month within
                                      the selected date range

  `totalExpenses`                     Expense total for the month within
                                      the selected date range

  `netCashFlow`                       Monthly income minus expenses
  -----------------------------------------------------------------------

The monthly query generates calendar months across the requested period,
so months with no matching transactions can still appear with zero
totals. The first and last months are limited to the exact requested
start and end dates.

Example response shape:

``` json
[
  {
    "yearMonth": "2026-08",
    "totalIncome": 1500.00,
    "totalExpenses": 900.00,
    "netCashFlow": 600.00
  },
  {
    "yearMonth": "2026-09",
    "totalIncome": 3000.00,
    "totalExpenses": 2100.00,
    "netCashFlow": 900.00
  }
]
```

### Category report

``` http
GET /api/transactions/reports/category?startDate=2026-10-01&endDate=2026-10-31
```

Returns expense totals grouped by category:

  Field                  Description
  ---------------------- ----------------------------------------
  `expenseCategory`      Expense category enum
  `expenseAmount`        Total expense amount for that category
  `spendingPercentage`   Category share of total expenses

Example response shape:

``` json
[
  {
    "expenseCategory": "GROCERY",
    "expenseAmount": 425.50,
    "spendingPercentage": 35.46
  },
  {
    "expenseCategory": "HOUSING",
    "expenseAmount": 774.50,
    "spendingPercentage": 64.54
  }
]
```

Categories without expenses in the selected range are not returned. If
there are no expenses, the endpoint returns an empty list.

### Invalid date ranges

The start date must not be after the end date. Invalid ranges are
rejected by the backend service. Both query parameters are required.

## Validation and Error Handling

-   Transaction request validation requires an amount, type, and date.
-   Transaction amounts must be greater than zero.
-   Classification validation enforces the appropriate category/source
    for the selected transaction type.
-   A missing transaction produces a not-found response.
-   Report requests require a valid date range.
-   The frontend displays an error message when report requests fail and
    clears report data for a failed report load.

## Testing and Quality Checks

### Backend tests

From the `backend` directory, run:

**Windows PowerShell:**

``` powershell
.\mvnw.cmd clean test
```

**macOS / Linux:**

``` bash
./mvnw clean test
```

The test suite includes controller and service tests for transactions
and reports. Relevant test classes are under:

``` text
backend/src/test/java/com/example/financetracker/
├── controller/
│   ├── ReportControllerTest.java
│   └── TransactionControllerTest.java
└── service/
    ├── ReportServiceImplTest.java
    └── TransactionServiceImplTest.java
```

### Frontend lint and production build

From the `frontend` directory:

``` bash
npm run lint
npm run build
```

To preview the production build locally:

``` bash
npm run preview
```

## Project Structure

``` text
finance-tracker/
├── backend/
│   ├── .mvn/wrapper/                 # Maven Wrapper configuration
│   ├── mvnw                          # Maven Wrapper (macOS/Linux)
│   ├── mvnw.cmd                      # Maven Wrapper (Windows)
│   ├── pom.xml                       # Backend dependencies and build
│   └── src/
│       ├── main/
│       │   ├── java/com/example/financetracker/
│       │   │   ├── controller/       # Transaction and report REST controllers
│       │   │   ├── dto/              # API request and response DTOs
│       │   │   ├── entity/           # Transaction entity and enums
│       │   │   ├── exception/        # Application exceptions and handlers
│       │   │   ├── repository/       # JPA and reporting data access
│       │   │   ├── service/          # Business logic
│       │   │   └── validation/       # Transaction classification validation
│       │   └── resources/
│       │       ├── queries/           # SQL report queries
│       │       └── application.properties
│       └── test/java/com/example/financetracker/
│           ├── controller/           # REST controller tests
│           └── service/              # Service tests
└── frontend/
    ├── public/                       # Static assets
    ├── src/
    │   ├── components/               # Reusable UI components
    │   │   └── Reports/              # Summary, monthly, and category reports
    │   ├── App.jsx                   # Main application and page navigation
    │   ├── App.css
    │   └── index.css
    ├── package.json
    └── vite.config.js
```

## Configuration

The primary backend configuration is located at:

``` text
backend/src/main/resources/application.properties
```

Current local defaults:

``` properties
spring.application.name=finance-tracker-backend
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_tracker
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

The frontend currently uses the backend URL `http://localhost:8080`
directly in its fetch calls. If you change the backend host or port,
update those frontend API URLs as well. For a deployed environment, move
the API base URL into environment-specific frontend configuration rather
than hard-coding it.

## Troubleshooting

### Backend cannot connect to PostgreSQL

-   Confirm the PostgreSQL service is running.
-   Confirm the `finance_tracker` database exists.
-   Check the host, port, and username in `application.properties`.
-   Confirm `DB_PASSWORD` is available to the backend process.
-   Review the backend console for the underlying connection error.

### Frontend requests fail

-   Confirm the backend is running on port `8080`.
-   Confirm the frontend is running on port `5173`.
-   Inspect the browser developer tools' Console and Network tabs.
-   Check that the request URL matches the backend endpoint.
-   The controllers currently allow CORS requests from
    `http://localhost:5173`. If Vite uses another port, update the
    `@CrossOrigin` configuration.

### A report request returns an error

-   Confirm both `startDate` and `endDate` are present.
-   Use `YYYY-MM-DD` date formatting.
-   Confirm the start date is not later than the end date.
-   Inspect the backend console for SQL or service exceptions.
-   Inspect the Network tab to see the failing report endpoint and
    response.

### Frontend dependencies or build fail

From the `frontend` directory, reinstall dependencies:

``` bash
npm install
```

Then run:

``` bash
npm run lint
npm run build
```

## Security and Production Considerations

This project is intended as a local development application and is not a
production-hardened financial service.

Before deploying it publicly:

-   Add authentication and authorization so users can access only their
    own financial data.
-   Use HTTPS.
-   Store credentials in environment variables or a secrets manager.
-   Restrict CORS to trusted origins.
-   Replace automatic Hibernate schema updates with a controlled
    database migration strategy.
-   Establish database backup and recovery procedures.
-   Review logging and avoid logging sensitive financial information.
-   Configure production-specific API URLs and environment settings.
-   Add monitoring and production-grade operational error handling.

The application stores transactions entered by the user. It does not
connect to banks, retrieve live account balances, or provide financial
advice.

## Future Enhancements

Potential future improvements include:

-   User registration, authentication, and per-user transaction data.
-   Allow multiple accounts per user & account types
-   Set financial goals
-   Configurable currency and locale support.
-   More detailed report filters and export options.
-   CSV or spreadsheet export.
-   Pagination for large transaction histories.
-   Automated frontend tests.
-   Integration tests against a dedicated PostgreSQL test database.
-   Continuous integration to run backend tests and frontend lint/build
    checks.

## License

No license is currently specified. Add a `LICENSE` file before
distributing or accepting contributions under an open-source license.
