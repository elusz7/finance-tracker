# Personal Finance Tracker

A full-stack personal finance tracker for recording income and expenses,
viewing a running financial summary, and reviewing recent transactions
by category. The project uses a React frontend, a Java Spring Boot REST
API, and PostgreSQL for persistent storage.

## Features

-   Add income and expense transactions.
-   Assign each transaction a category and date.
-   View total income, total expenses, and calculated balance.
-   Review the seven most recent transactions, ordered by date.
-   Filter recent transactions by category.
-   Persist transactions in PostgreSQL so they remain available after a
    page refresh.
-   Validate required transaction fields and reject amounts that are
    zero or negative.
-   Expose a REST API for creating, reading, updating, and deleting
    transactions.
-   Automated backend tests for service logic and controller behavior.

## Tech Stack

### Frontend

-   React 19
-   JavaScript
-   Vite
-   CSS

### Backend

-   Java 21
-   Spring Boot 4
-   Spring MVC / REST
-   Spring Data JPA
-   Jakarta Bean Validation
-   Maven Wrapper
-   JUnit, Mockito, and Spring MVC testing support

### Database

-   PostgreSQL

## Architecture

``` text
React + Vite frontend
        |
        | HTTP requests
        v
Spring Boot REST API
        |
        v
Service layer
        |
        v
Spring Data JPA repository
        |
        v
PostgreSQL database
```

The frontend runs by default at `http://localhost:5173`. The backend API
runs at `http://localhost:8080`, and the PostgreSQL configuration
defaults to `localhost:5432`.

## Prerequisites

Install or configure the following before running the project:

-   Java Development Kit (JDK) 21
-   Node.js and npm
-   PostgreSQL
-   Git

The backend includes the Maven Wrapper, so a separate Maven installation
is not required.

## Getting Started

### 1. Clone the repository

``` bash
git clone https://github.com/elusz7/finance-tracker.git
cd finance-tracker
```

If you downloaded the source as a ZIP file, extract it and open the
extracted project directory.

### 2. Create the PostgreSQL database

Start PostgreSQL and create a database named `finance_tracker`.

For example, when connected as a PostgreSQL administrator:

``` sql
CREATE DATABASE finance_tracker;
```

The current backend configuration uses:

-   Database: `finance_tracker`
-   Username: `postgres`
-   Host: `localhost`
-   Port: `5432`

If your PostgreSQL username, host, port, or database name differs,
update `backend/src/main/resources/application.properties` to match your
local setup.

### 3. Configure the database password

The backend reads the database password from the `DB_PASSWORD`
environment variable. Do not commit a real database password to source
control.

#### Windows PowerShell

Set the variable in the terminal you will use to launch the backend:

``` powershell
$env:DB_PASSWORD = "your-local-postgres-password"
```

If you launch the backend from IntelliJ IDEA, add `DB_PASSWORD` to the
environment variables in the run configuration for
`FinanceTrackerBackendApplication`.

#### macOS / Linux

``` bash
export DB_PASSWORD="your-local-postgres-password"
```

The environment variable must be available to the process running the
backend.

### 4. Start the backend

Open a terminal in the `backend` directory.

#### Windows PowerShell

``` powershell
.\mvnw.cmd spring-boot:run
```

#### macOS / Linux

``` bash
./mvnw spring-boot:run
```

The backend uses `http://localhost:8080` by default. Hibernate is
configured with `spring.jpa.hibernate.ddl-auto=update`, which updates
the database schema based on the JPA entities during startup. This
setting is convenient for local development; for a production
deployment, use a deliberate schema migration strategy.

### 5. Start the frontend

Open a second terminal in the `frontend` directory:

``` bash
npm install
npm run dev
```

Vite prints the local development URL; by default it is:

``` text
http://localhost:5173
```

Keep both the backend and frontend running while using the application.
The frontend currently calls the API at
`http://localhost:8080/api/transactions`.

## Using the Application

1.  Open the frontend in your browser.
2.  Add a transaction by entering an amount, selecting its type and
    category, and choosing a date. Description is optional.
3.  Check the income, expenses, and balance summary.
4.  Use the category filter to narrow the recent transaction list.
5.  Refresh the page to verify that saved transactions are loaded from
    the backend and database.

The displayed balance is calculated from the transactions stored in the
application (`total income - total expenses`). It is not a live
connection to a bank account.

## Transaction Types and Categories

### Transaction types

-   `INCOME`
-   `EXPENSE`

### Categories

-   `HOUSING`
-   `UTILITIES`
-   `SUBSCRIPTION`
-   `GROCERY`
-   `DINING`
-   `ENTERTAINMENT`
-   `TRANSPORTATION`
-   `HEALTHCARE`
-   `TRAVEL`
-   `SHOPPING`
-   `PERSONAL_CARE`
-   `EDUCATION`
-   `OTHER`

API requests must use the enum values exactly as shown above.

## REST API

Base URL:

``` text
http://localhost:8080/api/transactions
```

### Endpoints

  ---------------------------------------------------------------------------------
  Method            Endpoint                   Description       Successful
                                                                 response
  ----------------- -------------------------- ----------------- ------------------
  `GET`             `/api/transactions`        Get all           `200 OK`
                                               transactions      

  `GET`             `/api/transactions/{id}`   Get one           `200 OK`
                                               transaction by ID 

  `POST`            `/api/transactions`        Create a          `201 Created`
                                               transaction       

  `PUT`             `/api/transactions/{id}`   Update a          `200 OK`
                                               transaction       

  `DELETE`          `/api/transactions/{id}`   Delete a          `204 No Content`
                                               transaction       
  ---------------------------------------------------------------------------------

A request for a transaction ID that does not exist returns
`404 Not Found`.

### Transaction JSON example

Use this JSON body for create or update requests:

``` json
{
  "description": "Weekly groceries",
  "amount": 52.75,
  "type": "EXPENSE",
  "category": "GROCERY",
  "date": "2026-10-27"
}
```

The `description` field is optional. The `amount`, `type`, `category`,
and `date` fields are required. The amount must be greater than zero,
and the date must use ISO format `YYYY-MM-DD`.

### Example requests

Get all transactions:

``` bash
curl http://localhost:8080/api/transactions
```

Create a transaction:

``` bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Paycheck",
    "amount": 1800.00,
    "type": "INCOME",
    "category": "OTHER",
    "date": "2026-10-27"
  }'
```

Get a transaction by ID:

``` bash
curl http://localhost:8080/api/transactions/1
```

Update a transaction:

``` bash
curl -X PUT http://localhost:8080/api/transactions/1 \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Updated groceries",
    "amount": 60.00,
    "type": "EXPENSE",
    "category": "GROCERY",
    "date": "2026-10-27"
  }'
```

Delete a transaction:

``` bash
curl -X DELETE http://localhost:8080/api/transactions/1
```

The example requests use `curl`. On Windows PowerShell, `curl` may be an
alias; use `curl.exe` if needed.

## Running Tests

### Backend tests

From the `backend` directory, run the full test suite.

#### Windows PowerShell

``` powershell
.\mvnw.cmd clean test
```

#### macOS / Linux

``` bash
./mvnw clean test
```

The tests cover transaction service behavior and REST controller
behavior, including successful requests, not-found cases, and request
validation.

In IntelliJ IDEA, you can also run the test classes directly or use
**Run with Coverage** to inspect class, method, line, and branch
coverage.

### Frontend checks

From the `frontend` directory:

``` bash
npm run lint
npm run build
```

`npm run lint` runs ESLint, and `npm run build` creates a production
build with Vite.

## Project Structure

``` text
finance-tracker/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/financetracker/
│   │   │   │   ├── controller/   # REST endpoints
│   │   │   │   ├── dto/          # Request DTOs and validation
│   │   │   │   ├── entity/       # JPA entity and enums
│   │   │   │   ├── exception/    # API exception types
│   │   │   │   ├── repository/   # Database access
│   │   │   │   └── service/      # Application/business logic
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/java/com/example/financetracker/
│   │       ├── controller/       # Controller tests
│   │       └── service/          # Service tests
│   ├── mvnw
│   └── pom.xml
└── frontend/
    ├── public/
    ├── src/
    │   ├── App.jsx
    │   ├── App.css
    │   └── index.css
    ├── package.json
    └── vite.config.js
```

## Troubleshooting

### Backend cannot connect to PostgreSQL

-   Confirm the PostgreSQL service is running.
-   Confirm the `finance_tracker` database exists.
-   Check the username, host, and port in `application.properties`.
-   Confirm `DB_PASSWORD` is set in the environment used by the backend
    process.
-   Review the IntelliJ Run console or Maven output for the underlying
    connection error.

### Frontend cannot load or save transactions

-   Confirm the backend has started on port `8080`.
-   Open the browser developer tools and inspect the Console and Network
    tabs.
-   Confirm requests target `http://localhost:8080/api/transactions`.
-   The backend currently allows cross-origin requests from
    `http://localhost:5173`; if Vite runs on a different port, update
    the CORS configuration in `TransactionController`.

### `psql` is not recognized on Windows

This means PowerShell cannot find the PostgreSQL command-line client. It
does not by itself mean that the database server is unavailable. You can
use IntelliJ's Database tool window to test the PostgreSQL connection,
or add the PostgreSQL `bin` directory to your PATH if the command-line
tools are installed.

## Security and Production Notes

This project is configured for local development and is not a
production-hardened financial service.

-   Keep credentials out of source control and use environment variables
    or a secrets manager.
-   Use HTTPS and appropriate authentication and authorization before
    exposing the API publicly.
-   Review database migration and backup strategies before production
    use.
-   Configure CORS for only the trusted frontend origins needed by the
    deployment.
-   Add production-grade logging, monitoring, and error handling as
    needed.
-   The app does not connect to financial institutions or retrieve bank
    balances.

## Future Improvements

Potential next steps include:

-   Edit and delete transactions directly from the frontend.
-   Add pagination and date-range filtering.
-   Add charts for spending trends and category breakdowns.
-   Add user accounts and authentication.
-   Add configurable currency and locale settings.
-   Add integration tests using a dedicated test database.
-   Add CI to run backend tests and frontend lint/build checks on each
    push.

## License

No license is currently specified. If you intend to make this project
open source, add a `LICENSE` file that clearly states how others may
use, modify, and distribute it.
