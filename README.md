RideShare Backend (Spring Boot + MongoDB + JWT)
===============================================

Overview
--------
Minimal ride-sharing backend featuring user/driver roles, JWT auth, ride lifecycle, validation, and global error handling.

Tech Stack
----------
- Java 21, Spring Boot 4
- Spring Web, Spring Security (JWT), Spring Data MongoDB, Validation
- MongoDB

Setup
-----
1) Install JDK 21 and MongoDB running locally.
2) Create `.env` in project root (auto-loaded via `spring.config.import`):
```
MONGODB_URI=mongodb://localhost:27017/rideshare
JWT_SECRET=change-me-please-very-secret-key
JWT_EXPIRATION_MS=86400000
```
3) Run: `./gradlew bootRun` (server on 8081) or `./gradlew test`.

API Auth
--------
- JWT in header: `Authorization: Bearer <token>`
- Roles: `ROLE_USER`, `ROLE_DRIVER`

Endpoints
---------
- POST `/api/auth/register` — body `{ "username", "password", "role" }` (role must be ROLE_USER or ROLE_DRIVER)
- POST `/api/auth/login` — body `{ "username", "password" }` → returns `{ "token" }`
- POST `/api/v1/rides` — ROLE_USER, body `{ "pickupLocation", "dropLocation" }` → status REQUESTED
- GET `/api/v1/user/rides` — ROLE_USER, returns rides for caller
- GET `/api/v1/driver/rides/requests` — ROLE_DRIVER, list REQUESTED rides
- POST `/api/v1/driver/rides/{id}/accept` — ROLE_DRIVER, sets driverId and ACCEPTED
- POST `/api/v1/rides/{id}/complete` — ROLE_USER or ROLE_DRIVER (assigned), sets COMPLETED

Validation
----------
- Jakarta validation on DTOs (e.g., `@NotBlank`, length checks); errors return `VALIDATION_ERROR` with message.

Error Handling
--------------
Global handler returns JSON:
```
{ "error": "ERROR_CODE", "message": "details", "timestamp": "..." }
```
Codes: VALIDATION_ERROR, BAD_REQUEST, NOT_FOUND, FORBIDDEN, UNAUTHORIZED, INTERNAL_ERROR.

Folders
-------
`src/main/java/com/example/RideShare/`
- `model/`, `repository/`, `service/`, `controller/`, `config/`, `dto/`, `exception/`, `util/`

Quick Test (curl)
-----------------
```
# Register user
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"1234","role":"ROLE_USER"}'

# Login user
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"1234"}' | jq -r .token)

# Create ride
curl -X POST http://localhost:8081/api/v1/rides \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"pickupLocation":"A","dropLocation":"B"}'
```

