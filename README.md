# Sports Betting Settlement Service

A small Spring Boot backend for a home assignment that demonstrates a sports betting settlement flow.

## What the service does

- Seeds OPEN bets in an H2 in-memory database on startup
- Exposes simple bet inspection endpoints:
  - `GET /api/bets`
  - `GET /api/bets/{betId}`
- Accepts event outcomes via `POST /api/event-outcomes`
- Publishes incoming outcomes to Kafka topic `event-outcomes`
- Consumes the outcome, matches it against OPEN bets, and publishes settlement messages to RocketMQ (or logs them if the broker is not reachable)

## Tech stack

- Java 21
- Spring Boot 3.3.2
- Spring Web
- Spring Data JPA
- Spring Kafka
- H2 in-memory database
- Lombok
- Apache RocketMQ
- Docker Compose

## Start everything with one command

From the project root:

```bash
docker compose up --build
```

That starts:

- Kafka on `localhost:9092`
- Kafka UI on `http://localhost:8090`
- RocketMQ NameServer on `localhost:9876`
- RocketMQ Broker
- The Spring Boot app on `http://localhost:8080`

To stop everything:

```bash
docker compose down
```

## Run the app locally without Docker

If you only want to run the Spring Boot app locally while keeping Kafka and RocketMQ in Docker:

```bash
docker compose up -d kafka namesrv broker
mvn spring-boot:run
```

## H2 console

The application uses an H2 in-memory database. After startup, open:

- `http://localhost:8080/h2-console`

Use:

- JDBC URL: `jdbc:h2:mem:bettingdb`
- Username: `sa`
- Password: empty

## Example request

Send an event outcome with:

```bash
curl -X POST http://localhost:8080/api/event-outcomes \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "event-1",
    "eventName": "Team A vs Team B",
    "eventWinnerId": "team-a"
  }'
```

## Demo flow with curl

### 1. Inspect the seeded bets

```bash
curl http://localhost:8080/api/bets
```

### 2. Submit an event outcome

```bash
curl -X POST http://localhost:8080/api/event-outcomes \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "event-1",
    "eventName": "Team A vs Team B",
    "eventWinnerId": "team-a"
  }'
```

### 3. Inspect the bets again

```bash
curl http://localhost:8080/api/bets
```

### 4. Inspect a single bet

```bash
curl http://localhost:8080/api/bets/1
```

## RocketMQ in Docker Compose

The compose file starts RocketMQ NameServer and Broker so the app can connect to `localhost:9876` in local development.
