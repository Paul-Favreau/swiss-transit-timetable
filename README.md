# Swiss public transport timetables

[![Backend](https://github.com/Paul-Favreau/swiss-transit-timetable/actions/workflows/backend.yml/badge.svg)](https://github.com/Paul-Favreau/swiss-transit-timetable/actions/workflows/backend.yml)

A timetable app for Swiss public transport, built on the official GTFS feed: search for a stop, then see its next
departures.

> Work in progress. Current stage: the technical foundation (Spring Boot backend and PostgreSQL database).

## Stack

- **Backend:** Java 25, Spring Boot 4.1, Maven, PostgreSQL 18, Testcontainers.
- **Frontend:** Angular 22 (coming next).
- **Layout:** a single repository with `backend/`, `frontend/` (coming next) and `docs/`.

## Running locally

Prerequisites: JDK 25 and Docker.

1. Create the `.env` file from the template, then choose a database user name and password in it:

   ```
   cp .env.example .env
   ```

2. Start PostgreSQL. Port 5431 is only open to the local machine:

   ```
   docker compose up -d
   ```

3. Start the backend, passing it the same credentials as in `.env` through environment variables:

   ```
   cd backend
   USER_BDD=user MDP_BDD=password ./mvnw spring-boot:run
   ```

   On PowerShell: `$env:USER_BDD="user"; $env:MDP_BDD="password"; ./mvnw spring-boot:run`

4. Check that the application reports `UP` at <http://localhost:8080/actuator/health>.

## Tests

```
cd backend
./mvnw verify
```

Docker must be running: the integration test starts its own PostgreSQL with Testcontainers.

## Architecture decisions

Structural choices are recorded in [`docs/adr/`](docs/adr/) (in French), one short document per decision with its
context, the options considered and the reasons for the choice.

## Data

Timetable source: [opentransportdata.swiss](https://opentransportdata.swiss), "Timetable 2026 (GTFS2020)" dataset.
