# Coursecraft

Coursecraft is a Spring Boot course-management app with a responsive web dashboard and a REST API for topics and courses.

## Run locally

Requirements: Java 11 or newer.

From this directory, start the app with the included Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8084](http://localhost:8084). The app uses an embedded, in-memory H2 database; data is reset when the application stops.

Run the test suite:

```powershell
.\mvnw.cmd test
```

## API

All endpoints return JSON. Course creation and updates require a non-empty `name` and `description`; topic creation and updates require a non-empty `course`.

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/topics` | List topics |
| `POST` | `/topics` | Create a topic: `{"course":"Computer Science"}` |
| `GET` | `/topics/{id}` | Get a topic |
| `PUT` | `/topics/{id}` | Update a topic |
| `DELETE` | `/topics/{id}` | Delete a topic and its courses |
| `GET` | `/topics/{topicId}/courses` | List a topic's courses |
| `POST` | `/topics/{topicId}/courses` | Create a course: `{"name":"Distributed Systems","description":"..."}` |
| `GET` | `/topics/{topicId}/courses/{id}` | Get a course |
| `PUT` | `/topics/{topicId}/courses/{id}` | Update a course |
| `DELETE` | `/topics/{topicId}/courses/{id}` | Delete a course |
| `GET` | `/topic/{name}/courses` | List courses by topic name |
